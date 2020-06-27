package com.yyself.tools.controller;

import com.alibaba.druid.util.JdbcConstants;
import com.yyself.tool.exception.KotException;
import com.yyself.tool.utils.FileUtils;
import com.yyself.tool.utils.ResponseResult;
import com.yyself.tool.utils.TextUtils;
import com.yyself.tool.utils.ZipUtils;
import com.yyself.tools.database.dialect.BaseSqlHandler;
import com.yyself.tools.database.dialect.MySqlHandler;
import com.yyself.tools.database.dialect.PostgreSqlHandler;
import com.yyself.tools.database.make.*;
import com.yyself.tools.database.vo.ClassModel;
import com.yyself.tools.database.vo.DatabaseGenVo;
import com.yyself.tools.database.vo.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.CommonUtils.capitalName;
import static com.yyself.tool.utils.ResponseResult.ok;

/**
 * @Author yangyu
 * @create 2020/5/19 下午3:17
 */

@Slf4j
@RestController
@RequestMapping("/database")
public class DatabaseGenController {

    private static final String PREFIX_ZIP = "kot-gen-";

    @Value("${tools.database.genPath}")
    private java.lang.String path;

    public BaseSqlHandler sqlHandler(String dialect) {
        if (JdbcConstants.MYSQL.equals(dialect)) {
            return new MySqlHandler();
        }
        if (JdbcConstants.POSTGRESQL.equals(dialect)) {
            return new PostgreSqlHandler();
        }
        throw new KotException("不支持数据库类型: " + dialect);

    }

    @PostMapping("/gen")
    public synchronized ResponseResult gen(@RequestBody DatabaseGenVo vo) throws Exception {

        String zipName = PREFIX_ZIP + LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        TextUtils.deleteDir(path);

        BaseSqlHandler baseSqlHandler = sqlHandler(vo.getDialect());
        baseSqlHandler.setSql(String.join("", vo.getDdlList()));
        List<TableInfo> tableInfos = baseSqlHandler.tableInfos();
        for (TableInfo tableInfo : tableInfos) {

            vo.setRealTableName(tableInfo.getTableName());
            capitalTableName(vo);
            vo.setFilePath(path);
            vo.setColumnInfos(tableInfo.getColumnInfos());

            new MakeEntity(vo).make();
            new MakeMapper(vo).make();
            new MakeService(vo).make();
            new MakeServiceImpl(vo).make();
            new MakeController(vo).make();

            // 生成vue文件
            MakeVue.makeVue(vo);
        }

        Map<String, List<ClassModel>> classModelMap = vo.getClassInfoList().stream().collect(Collectors.groupingBy(ClassModel::getTableName, LinkedHashMap::new, Collectors.toList()));
        vo.setClassModelMap(classModelMap);

        // 打包文件
        vo.setZipName(zipName + ".zip");
        ZipUtils.compress(path + zipName, path, vo.getZipName());
        return ok(vo);
    }

    private void capitalTableName(DatabaseGenVo vo) {
        String realTableName = vo.getRealTableName();
        if (StringUtils.isNotBlank(vo.getPrefix())) {
            realTableName = realTableName.replaceFirst(vo.getPrefix(), "");
        }
        String capitalName = capitalName(realTableName);
        vo.setTableName(capitalName);
        vo.setClassName(capitalName);
    }

    @PostMapping(value = "/download")
    public void download(HttpServletResponse response, @RequestBody DatabaseGenVo vo) {
        FileUtils.download(path + vo.getZipName(), vo.getZipName(), response);
    }


    @PostMapping("/info")
    public synchronized ResponseResult sql(@RequestParam("file") MultipartFile multipartFile, String dialect) throws IOException {

        BaseSqlHandler sqlHandler = sqlHandler(dialect);
        String tableSql = sqlHandler.tableSql(TextUtils.read(multipartFile.getInputStream()));
        sqlHandler.setSql(tableSql);
        return ok(sqlHandler.tableInfos());

    }


}
