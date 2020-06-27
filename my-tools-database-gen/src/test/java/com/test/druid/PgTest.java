package com.test.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.util.JdbcConstants;
import com.yyself.tool.utils.JacksonUtils;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.CommentInfo;
import com.yyself.tools.database.vo.TableInfo;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.yyself.tool.utils.ConvertUtils.convertString;
import static com.yyself.tools.database.dialect.BaseSqlHandler.DOT;
import static com.yyself.tools.database.dialect.BaseSqlHandler.removeQuota;
import static java.util.stream.Collectors.toMap;

/**
 * @author yangyu
 * @date 2020/6/14 下午6:54
 */
public class PgTest {

    @Test
    public void tableInfoTest() {
        String dbType = JdbcConstants.POSTGRESQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        // 备注信息
        Map<String, String> commentMap = commentMap(stmtList);

        List<TableInfo> tableInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            System.err.println("L1: " + sqlStatement.getClass());
            if (sqlStatement instanceof SQLCreateTableStatement) {

                List<ColumnInfo> columnInfos = new LinkedList<>();
                SQLCreateTableStatement tableStatement = (SQLCreateTableStatement) sqlStatement;
                String primaryKey = tableStatement.findPrimaryKey().getColumns().get(0).getExpr().toString();
                String tableName = removeQuota(tableStatement.getName().getSimpleName());

                for (SQLTableElement sqlTableElement : tableStatement.getTableElementList()) {
                    System.err.println("L2: " + sqlTableElement.getClass());
                    if (sqlTableElement instanceof SQLColumnDefinition) {
                        // 构建字段信息
                        SQLColumnDefinition def = (SQLColumnDefinition) sqlTableElement;
                        String defaultVal = convertString(def.getDefaultExpr());
                        ColumnInfo columnInfo = ColumnInfo.builder()
                                .name(removeQuota(def.getName().getSimpleName()))
                                .type(def.getDataType().getName())
                                .length(def.getDataType().getArguments().size() > 0 ? def.getDataType().getArguments().get(0).toString() : "")
                                .notNull(def.containsNotNullConstaint())
                                .defaultVal(defaultVal.contains("nextval") ? "nextval" : defaultVal)
                                .pk(def.getName().getSimpleName().equals(primaryKey))
                                .build();
                        columnInfo.setComment(getComment(commentMap, tableName, columnInfo.getName()));
                        columnInfos.add(columnInfo);
                    }

                }
                // 构建表信息
                TableInfo tableInfo = TableInfo.builder()
                        .tableName(tableName)
                        .tableDesc(getComment(commentMap, tableName, tableName))
                        .columnInfos(columnInfos)
//                        .indexInfos()
                        .build();
                tableInfos.add(tableInfo);
            }


        }
        System.err.println(JacksonUtils.toJson(tableInfos, true));

    }

    private static Map<String, String> commentMap(List<SQLStatement> stmtList) {
        List<CommentInfo> commentInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            // 备注
            if (sqlStatement instanceof SQLCommentStatement) {
                SQLCommentStatement commentStatement = (SQLCommentStatement) sqlStatement;
                SQLPropertyExpr expr = (SQLPropertyExpr) commentStatement.getOn().getExpr();
                CommentInfo.CommentInfoBuilder builder = CommentInfo.builder()
                        .columnName(removeQuota(expr.getName()))
                        .type(commentStatement.getType().name())
                        .comment(removeQuota(commentStatement.getComment().toString()));

                // 字段备注
                if (expr.getOwner() instanceof SQLPropertyExpr) {
                    SQLPropertyExpr owner = (SQLPropertyExpr) expr.getOwner();
                    builder.tableName(removeQuota(owner.getName()));
                }
                // 表备注
                if (expr.getOwner() instanceof SQLIdentifierExpr) {
                    builder.tableName(removeQuota(expr.getName()));
                }

                commentInfos.add(builder.build());
            }
        }
        return commentInfos.stream().collect(toMap(c -> c.getTableName() + DOT + c.getColumnName(), CommentInfo::getComment));
    }

    private String getComment(Map<String, String> commentMap, String tableName, String columnName) {
        return commentMap.get(tableName + DOT + columnName);
    }

    @Test
    public void indexs() {
        String dbType = JdbcConstants.POSTGRESQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        System.out.println(JacksonUtils.toJson(commentMap(stmtList), true));
    }

    private static final String sql = "CREATE TABLE \"public\".\"t_user\" (\n" +
            "  \"id\" int8 NOT NULL DEFAULT nextval('t_user_id_seq'::regclass),\n" +
            "  \"name\" varchar(255) COLLATE \"default\" NOT NULL,\n" +
            "  \"age\" int4,\n" +
            "  \"create_time\" timestamp(6) NOT NULL,\n" +
            "  \"remark\" varchar(10) COLLATE \"default\",\n" +
            "  \"status\" int2 NOT NULL DEFAULT 1,\n" +
            "  CONSTRAINT \"t_user_pkey\" PRIMARY KEY (\"id\")\n" +
            ")\n" +
            ";\n" +
            "\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"id\" IS '主键';\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"name\" IS '姓名';\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"age\" IS '年龄';\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"create_time\" IS '创建时间';\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"remark\" IS '备注';\n" +
            "\n" +
            "COMMENT ON COLUMN \"public\".\"t_user\".\"status\" IS '状态';\n" +
            "\n" +
            "COMMENT ON TABLE \"public\".\"t_user\" IS '用户表';\n" +
            "\n";
//            "COMMENT ON INDEX \"public\".\"idx\" IS 'name索引';";


}
