package com.yyself.tool.database;

import com.yyself.tool.database.make.*;
import com.yyself.tool.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tool.database.DatabaseHelper.*;
import static com.yyself.tool.utils.CommonUtils.capitalName;

/**
 * @Author yangyu
 * @create 2020/5/19 下午3:17
 */

@Slf4j
@RestController
@RequestMapping("/database")
public class DatabaseController {

    private static final String path = "/Users/yangyu/tmp/database";

    @PostMapping("/gen")
    public List<String> gen(@RequestBody DatabaseGenVo vo) throws Exception {
        // 删除临时文件
        TextUtils.deleteDir(path);

        String tableName = tableName(vo.getDdl());

        vo.setRealTableName(tableName);
        if (StringUtils.isNotBlank(vo.getPrefix())) {
            tableName = tableName.replaceFirst(vo.getPrefix(), "");
        }
        vo.setTableName(capitalName(tableName));
        vo.setClassName(capitalName(tableName));
        vo.setFilePath(path);
        vo.setPrimaryKey(primaryKey(vo.getDdl()));
        vo.setOpenKotMybatis(true);
        vo.setColumnDefinitionList(columnList(vo.getDdl()).stream().peek(c -> c.setColumnName(c.getColumnName().replaceAll("`", ""))).collect(Collectors.toList()));

        new MakeEntity(vo).make();
        new MakeMapper(vo).make();
        new MakeService(vo).make();
        new MakeServiceImpl(vo).make();
        new MakeController(vo).make();
        return vo.getClassInfoList();
    }


}
