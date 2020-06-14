package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yangyu
 * @date 2020/5/20 上午10:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseGenVo {
    private String realTableName;
    private String tableName;
    private String className;
    private String author;
    private String packages;
    private String prefix;
    private String filePath;
    private String primaryKey;
    private boolean openKotMybatis = true;
    private boolean enableSwagger = true;
    List<ColumnDefinition> columnDefinitionList;
    private List<ClassModel> classInfoList = new LinkedList<>();
    private String ddl;
    private String[] ddlList;
    private String zipName;
}
