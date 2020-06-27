package com.yyself.tools.database.dialect;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.yyself.tools.database.vo.TableInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/25 下午12:58
 */


@Data
public abstract class BaseSqlHandler {

    protected static final String PRIMARY_KEY = "PRIMARY KEY";
    protected static final String INDEX = "INDEX";
    protected static final String UNIQUE = "UNIQUE";
    protected static final String KEY = "KEY";

    public static final String CREATE_PREFIX = "CREATE TABLE";
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    private String sql;

    public abstract List<TableInfo> tableInfos();

    public static final List<SelectText> SELECT_TEXTS = Arrays.asList(
            new SelectText("CREATE TABLE"),
            new SelectText("COMMENT ON COLUMN"),
            new SelectText("COMMENT ON TABLE"),
            new SelectText("CREATE INDEX"),
            new SelectText("ALTER TABLE")
    );

    public static boolean isSelectStart(String line) {
        for (SelectText text : SELECT_TEXTS) {
            if (line.startsWith(text.start)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSelectEnd(String line) {
        return line.endsWith(SEMICOLON);
    }


    public static final List<String> SKIP_KEYWORDS = Arrays.asList(" OWNER TO ");

    public static boolean isSkip(String line) {
        for (String skipKeyword : SKIP_KEYWORDS) {
            if (line.contains(skipKeyword)) {
                return true;
            }
        }
        return false;
    }

    public String tableSql(List<String> lines) {

        List<String> tables = new ArrayList<>();
        boolean isSelect = false;
        for (String line : lines) {
            if (isSelectStart(line)) {
                isSelect = true;
            }
            if (isSelect && !isSkip(line)) {
                System.err.println("选择行: " + line);
                tables.add(this.formatLine(line));
            }
            if (isSelectEnd(line)) {
                isSelect = false;
            }
        }

        return String.join("", tables);
    }

    public abstract String formatLine(String line);

    public static String primaryKey(SQLCreateTableStatement statement) {
        if (statement.findPrimaryKey() == null) {
            return "";
        }
        return statement.findPrimaryKey().getColumns().get(0).getExpr().toString();
    }

    public static String removeQuota(String str) {
        return str.replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectText {

        private String type;
        private String start;
        private String end;

        public SelectText(String start) {
            this.start = start;
        }

        public SelectText(String start, String end) {
            this.start = start;
            this.end = end;
            this.type = start;
        }
    }

}
