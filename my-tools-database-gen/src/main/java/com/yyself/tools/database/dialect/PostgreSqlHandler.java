package com.yyself.tools.database.dialect;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import com.yyself.tool.utils.JacksonUtils;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.CommentInfo;
import com.yyself.tools.database.vo.IndexInfo;
import com.yyself.tools.database.vo.TableInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.ConvertUtils.convertString;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * @author yangyu
 * @date 2020/6/25 下午12:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostgreSqlHandler extends BaseSqlHandler {

    @Override
    public List<TableInfo> tableInfos() {
        String dbType = JdbcConstants.POSTGRESQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(super.getSql(), dbType);
        // 备注信息
        Map<String, String> commentMap = commentMap(stmtList);
        // 索引信息
        Map<String, List<IndexInfo>> indexMap = indexMap(stmtList);

        List<TableInfo> tableInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            if (sqlStatement instanceof SQLCreateTableStatement) {

                List<ColumnInfo> columnInfos = new LinkedList<>();
                SQLCreateTableStatement tableStatement = (SQLCreateTableStatement) sqlStatement;
                String tableName = removeQuota(tableStatement.getName().getSimpleName());
                String primaryKey = myPrimaryKey(tableStatement, tableName, indexMap);

                for (SQLTableElement sqlTableElement : tableStatement.getTableElementList()) {
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
                                .pk(removeQuota(def.getName().getSimpleName()).equals(removeQuota(primaryKey)))
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
                        .indexInfos(indexMap.get(tableName))
                        .build();
                tableInfos.add(tableInfo);
            }

        }
        return tableInfos;
    }

    private String myPrimaryKey(SQLCreateTableStatement tableStatement, String tableName, Map<String, List<IndexInfo>> indexMap) {
        String primaryKey = primaryKey(tableStatement);
        if (StringUtils.isNotBlank(primaryKey)) {
            return primaryKey;
        }
        if (!indexMap.containsKey(tableName)) {
            return "";
        }
        List<IndexInfo> indexInfos = indexMap.get(tableName);
        for (IndexInfo indexInfo : indexInfos) {
            if (indexInfo.getType().equals(PRIMARY_KEY)) {
                return indexInfo.getIndexes();
            }
        }
        return "";

    }

    /**
     * 索引信息
     */
    private static Map<String, List<IndexInfo>> indexMap(List<SQLStatement> stmtList) {
        List<IndexInfo> indexInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            // 唯一索引
            if (sqlStatement instanceof SQLAlterTableStatement) {
                SQLAlterTableStatement alterTableStatement = (SQLAlterTableStatement) sqlStatement;
                SQLAlterTableAddConstraint sqlAlterTableAddConstraint = ((SQLAlterTableAddConstraint) alterTableStatement.getItems().get(0));
                SQLConstraint constraint = sqlAlterTableAddConstraint.getConstraint();
                if (constraint instanceof SQLPrimaryKeyImpl) {
                    SQLPrimaryKeyImpl primaryKey = (SQLPrimaryKeyImpl) constraint;
                    IndexInfo indexInfo = IndexInfo.builder()
                            .tableName(BaseSqlHandler.removeQuota(((SQLPropertyExpr) alterTableStatement.getTableSource().getExpr()).getName()))
                            .key(removeQuota(primaryKey.getName().getSimpleName()))
                            .type(PRIMARY_KEY)
                            .indexes(primaryKey.getColumns().stream().map(item -> removeQuota(item.getExpr().toString())).collect(Collectors.joining(COMMA)))
                            .build();
                    indexInfos.add(indexInfo);
                } else if (constraint instanceof SQLUnique) {
                    SQLUnique sqlUnique = (SQLUnique) constraint;
                    IndexInfo indexInfo = IndexInfo.builder()
                            .tableName(BaseSqlHandler.removeQuota(((SQLPropertyExpr) alterTableStatement.getTableSource().getExpr()).getName()))
                            .key(removeQuota(sqlUnique.getName().getSimpleName()))
                            .type(UNIQUE)
                            .indexes(sqlUnique.getColumns().stream().map(item -> removeQuota(item.getExpr().toString())).collect(Collectors.joining(COMMA)))
                            .build();
                    indexInfos.add(indexInfo);
                }
            }
            // 普通索引
            if (sqlStatement instanceof SQLCreateIndexStatement) {
                SQLCreateIndexStatement indexStatement = (SQLCreateIndexStatement) sqlStatement;
                IndexInfo indexInfo = IndexInfo.builder()
                        .tableName(removeQuota(((SQLPropertyExpr) ((SQLExprTableSource) indexStatement.getTable()).getExpr()).getName()))
                        .key(removeQuota(indexStatement.getName().getSimpleName()))
                        .type(INDEX)
                        .using(indexStatement.getUsing().toUpperCase())
                        .indexes(indexStatement.getItems().stream().map(item -> removeQuota(item.getExpr().toString())).collect(Collectors.joining(COMMA)))
                        .build();
                indexInfos.add(indexInfo);
            }
        }
        return indexInfos.stream().collect(groupingBy(IndexInfo::getTableName));
    }

    /**
     * 备注信息
     */
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

    @Override
    public String formatLine(String line) {
        line = removeCatalog(line);
        return line;
    }

    private static String removeCatalog(String line) {
        String keyword = "_catalog\".";
        if (line.contains(keyword)) {
            String targetWordTmp = "";
            String[] split = line.split(" ");
            for (String word : split) {
                if (word.contains(keyword)) {
                    targetWordTmp = word;
                    break;
                }
            }
            String targetWord = targetWordTmp.split("\\.")[0] + DOT;
            return line.replace(targetWord, "");
        }
        return line;
    }

    public static void main(String[] args) {
        String sql = "CREATE SEQUENCE \"public\".\"t_user_id_seq\" INCREMENT 1 MINVALUE  1 MAXVALUE 9223372036854775807 START 1 CACHE 1;";
        SQLUtils.parseStatements(sql, JdbcConstants.POSTGRESQL);
    }
}
