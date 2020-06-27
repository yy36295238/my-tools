package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author yangyu
 * @date 2020/5/20 上午10:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseGenVo {
    private String dialect;
    private String realTableName;
    private String tableName;
    private String className;
    private String author;
    private String packages;
    private String prefix;
    private String filePath;
    private boolean enableSwagger = true;
    List<ColumnInfo> columnInfos;
    private List<ClassModel> classInfoList = new LinkedList<>();
    Map<String, List<ClassModel>> classModelMap;
    private String ddl;
    private String[] ddlList;
    private String zipName;
}
