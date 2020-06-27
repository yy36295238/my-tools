package com.yyself.tools.database.dialect;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.util.JdbcConstants;
import com.yyself.tool.utils.JacksonUtils;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.IndexInfo;
import com.yyself.tools.database.vo.TableInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.ConvertUtils.convertString;

/**
 * @author yangyu
 * @date 2020/6/25 下午12:58
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MySqlHandler extends BaseSqlHandler {


    @Override
    public List<TableInfo> tableInfos() {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(super.getSql(), JdbcConstants.MYSQL);

        List<TableInfo> tableInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            if (sqlStatement instanceof SQLCreateTableStatement) {

                List<ColumnInfo> columnInfos = new LinkedList<>();
                List<IndexInfo> indexInfos = new LinkedList<>();

                SQLCreateTableStatement tableStatement = (SQLCreateTableStatement) sqlStatement;

                String tableName = removeQuota(tableStatement.getName().getSimpleName());
                String primaryKey = primaryKey(tableStatement);
                for (SQLTableElement sqlTableElement : tableStatement.getTableElementList()) {
                    if (sqlTableElement instanceof SQLColumnDefinition) {
                        // 构建字段信息
                        SQLColumnDefinition def = (SQLColumnDefinition) sqlTableElement;
                        ColumnInfo columnInfo = ColumnInfo.builder()
                                .name(removeQuota(def.getName().getSimpleName()))
                                .type(def.getDataType().getName())
                                .length(def.getDataType().getArguments().size() > 0 ? def.getDataType().getArguments().get(0).toString() : "")
                                .notNull(def.containsNotNullConstaint())
                                .defaultVal(removeQuota(convertString(def.getDefaultExpr())))
                                .pk(def.getName().getSimpleName().equals(primaryKey))
                                .comment(removeQuota(convertString(def.getComment())))
                                .build();
                        columnInfos.add(columnInfo);
                    } else if (sqlTableElement instanceof MySqlPrimaryKey) {
                        // 主键索引
                        MySqlPrimaryKey key = (MySqlPrimaryKey) sqlTableElement;
                        IndexInfo indexInfo = IndexInfo.builder()
                                .key(removeQuota(key.getColumns().get(0).getExpr().toString()))
                                .type(PRIMARY_KEY)
                                .build();
                        indexInfos.add(indexInfo);
                    } else if (sqlTableElement instanceof MySqlKey) {
                        // 普通索引
                        MySqlKey mySqlKey = (MySqlKey) sqlTableElement;
                        IndexInfo indexInfo = IndexInfo.builder()
                                .key(removeQuota(mySqlKey.getName().getSimpleName()))
                                .type(sqlTableElement instanceof MySqlUnique ? UNIQUE : KEY)
                                .using(mySqlKey.getIndexType())
                                .indexes(mySqlKey.getColumns().stream().map(item -> removeQuota(item.getExpr().toString())).collect(Collectors.joining(COMMA)))
                                .comment(removeQuota(convertString(mySqlKey.getComment())))
                                .build();
                        indexInfos.add(indexInfo);
                    }

                }
                // 构建表信息
                TableInfo tableInfo = TableInfo.builder()
                        .tableName(tableName)
                        .tableDesc(removeQuota(convertString(tableStatement.getComment())))
                        .columnInfos(columnInfos)
                        .indexInfos(indexInfos)
                        .build();
                tableInfos.add(tableInfo);
            }
        }
        return tableInfos;
    }

    @Override
    public String formatLine(String line) {
        return line;
    }


}
