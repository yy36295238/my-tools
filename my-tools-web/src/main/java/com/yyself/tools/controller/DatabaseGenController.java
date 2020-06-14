package com.yyself.tools.controller;

import com.yyself.tool.utils.ResponseResult;
import com.yyself.tool.utils.TextUtils;
import com.yyself.tool.utils.ZipUtils;
import com.yyself.tools.database.DatabaseHelper;
import com.yyself.tools.database.make.*;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.DatabaseGenVo;
import com.yyself.tools.database.vo.TableInfo;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.CommonUtils.capitalName;
import static com.yyself.tool.utils.ResponseResult.ok;
import static com.yyself.tools.database.DatabaseHelper.*;

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

    @PostMapping("/gen")
    public synchronized ResponseResult gen(@RequestBody DatabaseGenVo vo) throws Exception {

        String zipName = PREFIX_ZIP + LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        TextUtils.deleteDir(path);
        for (String ddl : vo.getDdlList()) {
            if (StringUtils.isBlank(ddl)) {
                continue;
            }
            vo.setDdl(ddl);
            String capitalTableName = capitalTableName(vo);

            vo.setTableName(capitalTableName);
            vo.setClassName(capitalTableName);
            vo.setFilePath(path);
            vo.setPrimaryKey(primaryKey(vo.getDdl()));
            vo.setOpenKotMybatis(true);
            vo.setColumnDefinitionList(columnList(vo.getDdl()).stream().peek(c -> c.setColumnName(removeQuota(c.getColumnName()))).collect(Collectors.toList()));

            new MakeEntity(vo).make();
            new MakeMapper(vo).make();
            new MakeService(vo).make();
            new MakeServiceImpl(vo).make();
            new MakeController(vo).make();

            // 生成vue文件
            MakeVue.makeVue(vo);
        }
        // 打包文件
        vo.setZipName(zipName + ".zip");
        ZipUtils.compress(path + zipName, path, vo.getZipName());
        return ok(vo);
    }

    private String capitalTableName(DatabaseGenVo vo) throws JSQLParserException {
        String tableName = tableName(vo.getDdl());

        vo.setRealTableName(tableName);
        if (StringUtils.isNotBlank(vo.getPrefix())) {
            tableName = tableName.replaceFirst(vo.getPrefix(), "");
        }
        return capitalName(tableName);
    }

    @PostMapping(value = "/download")
    public void download(HttpServletResponse response, @RequestBody DatabaseGenVo vo) {

        String zipName = path + vo.getZipName();

        //设置文件路径
        File file = new File(zipName);
        if (file.exists()) {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + vo.getZipName());
            response.addHeader("file-name", vo.getZipName());
            //  自定义响应头信息
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Access-Control-Expose-Headers", "file-name");
            byte[] buffer = new byte[1024];
            try {
                @Cleanup FileInputStream fis = new FileInputStream(file);
                @Cleanup BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @PostMapping("/info")
    public synchronized ResponseResult sql(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        List<TableInfo> tableInfos = new ArrayList<>();
        List<String> tableDdls = tableDdl(TextUtils.read(multipartFile.getInputStream()));

        for (String ddl : tableDdls) {
            List<ColumnInfo> columnInfos = DatabaseHelper.columnList(ddl).stream().map(col -> ColumnInfo.builder()
                    .name(removeQuota(col.getColumnName()))
                    .type(col.getColDataType().getDataType())
                    .length(columnLength(col))
                    .comment(comment(col))
                    .other(String.join(" ", other(col)))
                    .build()).collect(Collectors.toList());
            tableInfos.add(TableInfo.builder().tableName(tableName(ddl)).columnInfos(columnInfos).indexInfos(indexList(ddl)).build());
        }
        return ok(tableInfos);

    }


}
