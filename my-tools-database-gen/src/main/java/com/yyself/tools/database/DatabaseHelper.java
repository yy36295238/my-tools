package com.yyself.tools.database;

import com.google.common.collect.Lists;
import com.yyself.tools.database.vo.IndexInfo;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yangyu
 * @create 2020/5/20 上午10:19
 */

public class DatabaseHelper {

    private static final String COMMENT = "COMMENT";


    public static String formatSql(String sql) {
        return sql.replaceAll("USING BTREE", "");
    }

    public static String removeQuota(String str) {
        return str.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
    }

    public static String tableName(String sql) {
        return removeQuota(createTable(sql).getTable().getName());
    }

    public static String primaryKey(String sql) {
        return indexes(sql).stream().filter(DatabaseHelper::isPrimaryKey).map(index -> index.getColumnsNames().stream().findFirst().orElse("")).findFirst().orElse("").replaceAll("`", "");
    }

    public static boolean isPrimaryKey(Index index) {
        return "PRIMARY KEY".equals(index.getType());
    }

    public static String columnLength(ColumnDefinition col) {
        return col.getColDataType().getArgumentsStringList() == null ? "" : String.join("", col.getColDataType().getArgumentsStringList());
    }

    public static List<Index> indexes(String sql) {
        return createTable(sql).getIndexes();
    }

    public static List<IndexInfo> indexList(String sql) {
        return indexes(sql).stream().map(index -> IndexInfo.builder()
                .key((isPrimaryKey(index) ? index.getColumnsNames().get(0) : index.getName()).replaceAll("`", ""))
                .type(index.getType())
                .using(index.getUsing())
                .content(removeQuota(String.join(",", index.getColumnsNames())))
                .build()).collect(Collectors.toList());
    }

    public static List<ColumnDefinition> columnList(String sql) {
        return createTable(sql).getColumnDefinitions();
    }

    private static CreateTable createTable(String sql) {
        return ((CreateTable) statement(sql));
    }

    private static Statement statement(String sql) {
        CCJSqlParserManager parser = new CCJSqlParserManager();
        try {
            return parser.parse(new StringReader(formatSql(sql)));
        } catch (JSQLParserException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
        return null;
    }

    public static String comment(ColumnDefinition columnDefinition) {
        List<String> columnSpecStrings = columnDefinition.getColumnSpecStrings();
        if (CollectionUtils.isEmpty(columnSpecStrings) || !columnSpecStrings.contains(COMMENT)) {
            return "";
        }
        String comment = columnSpecStrings.get(columnSpecStrings.size() - 1);
        return removeQuota((comment == null || "NULL".equals(comment.toUpperCase())) ? "" : comment);
    }

    public static List<String> other(ColumnDefinition columnDefinition) {
        List<String> specStrings = columnDefinition.getColumnSpecStrings();
        if (CollectionUtils.isEmpty(specStrings) || !specStrings.contains(COMMENT)) {
            return Lists.newArrayList();
        }
        int index = specStrings.indexOf(COMMENT);
        return specStrings.subList(0, index);
    }

    public static List<String> tableDdl(List<String> lines) {

        List<String> tables = new ArrayList<>();
        boolean skip = false;
        boolean isFirstTable = true;
        StringBuilder tableBuilder = new StringBuilder();
        for (String line : lines) {
            if (isNoteStart(line)) {
                skip = true;
            }
            if (isNoteEnd(line)) {
                skip = false;
            }
            if (isSkip(line) || skip) {
                continue;
            }
            if (line.startsWith("CREATE TABLE")) {
                if (!isFirstTable) {
                    tables.add(tableBuilder.toString());
                    tableBuilder = new StringBuilder();
                } else {
                    isFirstTable = false;
                }
            }
            tableBuilder.append(line);
        }
        return tables;

    }

    public static boolean isNoteStart(String line) {
        return line.startsWith("/*") || line.startsWith("/**");
    }

    public static boolean isNoteEnd(String line) {
        return line.startsWith("*/") || line.startsWith("**/");
    }


    public static boolean isSkip(String line) {
        for (String keyword : Arrays.asList("--", "SET", "DROP", "/*", "/**", "*/", "**/")) {
            if (line.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }


}
