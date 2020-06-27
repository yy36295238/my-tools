package com.test.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.util.JdbcConstants;
import com.sun.org.apache.xerces.internal.impl.xs.identity.UniqueOrKey;
import com.yyself.tool.utils.JacksonUtils;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.IndexInfo;
import com.yyself.tools.database.vo.TableInfo;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.ConvertUtils.convertString;
import static com.yyself.tools.database.dialect.BaseSqlHandler.COMMA;
import static com.yyself.tools.database.dialect.BaseSqlHandler.removeQuota;

/**
 * @author yangyu
 * @date 2020/6/14 下午6:54
 */
public class NewMysqlTest {

    @Test
    public void tableInfoTest() {
        String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        List<TableInfo> tableInfos = new LinkedList<>();
        for (SQLStatement sqlStatement : stmtList) {
            System.err.println("L1: " + sqlStatement.getClass());
            if (sqlStatement instanceof SQLCreateTableStatement) {

                List<ColumnInfo> columnInfos = new LinkedList<>();
                List<IndexInfo> indexInfos = new LinkedList<>();

                SQLCreateTableStatement tableStatement = (SQLCreateTableStatement) sqlStatement;
                String primaryKey = tableStatement.findPrimaryKey().getColumns().get(0).getExpr().toString();
                String tableName = removeQuota(tableStatement.getName().getSimpleName());

                for (SQLTableElement sqlTableElement : tableStatement.getTableElementList()) {
                    System.err.println("L2: " + sqlTableElement.getClass());
                    if (sqlTableElement instanceof SQLColumnDefinition) {
                        // 构建字段信息
                        SQLColumnDefinition def = (SQLColumnDefinition) sqlTableElement;
                        ColumnInfo columnInfo = ColumnInfo.builder()
                                .name(removeQuota(def.getName().getSimpleName()))
                                .type(def.getDataType().getName())
                                .length(def.getDataType().getArguments().size() > 0 ? def.getDataType().getArguments().get(0).toString() : "")
                                .notNull(def.containsNotNullConstaint())
                                .defaultVal(convertString(def.getDefaultExpr()))
                                .pk(def.getName().getSimpleName().equals(primaryKey))
                                .comment(convertString(def.getComment()))
                                .build();
                        columnInfos.add(columnInfo);
                    } else if (sqlTableElement instanceof MySqlPrimaryKey) {// 主键索引
                        MySqlPrimaryKey key = (MySqlPrimaryKey) sqlTableElement;
                        IndexInfo indexInfo = IndexInfo.builder()
                                .key(key.getColumns().get(0).getExpr().toString())
                                .type("PRIMARY KEY")
                                .build();
                        indexInfos.add(indexInfo);
                    } else if (sqlTableElement instanceof MySqlKey) {// 普通索引
                        MySqlKey mySqlKey = (MySqlKey) sqlTableElement;
                        IndexInfo indexInfo = IndexInfo.builder()
                                .key(mySqlKey.getName().getSimpleName())
                                .type(sqlTableElement instanceof MySqlUnique ? "UNIQUE" : "KEY")
                                .using(mySqlKey.getIndexType())
                                .indexes(mySqlKey.getColumns().stream().map(item -> item.getExpr().toString()).collect(Collectors.joining(COMMA)))
                                .comment(convertString(mySqlKey.getComment()))
                                .build();
                        indexInfos.add(indexInfo);
                    }

                }
                // 构建表信息
                TableInfo tableInfo = TableInfo.builder()
                        .tableName(tableName)
                        .tableDesc(tableStatement.getComment().toString())
                        .columnInfos(columnInfos)
                        .indexInfos(indexInfos)
                        .build();
                tableInfos.add(tableInfo);
            }
        }
        System.err.println(JacksonUtils.toJson(tableInfos, true));

    }

    private static final String sql = "" +
            "CREATE TABLE `customer` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
            "  `name` varchar(255) NOT NULL COMMENT '名称',\n" +
            "  `amount` double DEFAULT NULL COMMENT '金额',\n" +
            "  `text` text COMMENT '文本',\n" +
            "  `status` smallint(6) NOT NULL DEFAULT '1' COMMENT '状态',\n" +
            "  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `unq_name` (`name`) USING BTREE COMMENT '名称唯一索引',\n" +
            "  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引',\n" +
            "  KEY `idx_name_createTime` (`name`,`create_time`) USING BTREE COMMENT '名称_时间'\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='客户表';" +


            "CREATE TABLE `order` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
            "  `name` varchar(255) NOT NULL COMMENT '名称',\n" +
            "  `amount` double DEFAULT NULL COMMENT '金额',\n" +
            "  `text` text COMMENT '文本',\n" +
            "  `status` smallint(6) NOT NULL DEFAULT '1' COMMENT '状态',\n" +
            "  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `unq_name` (`name`) USING BTREE COMMENT '名称唯一索引',\n" +
            "  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引',\n" +
            "  KEY `idx_name_createTime` (`name`,`create_time`) USING BTREE COMMENT '名称_时间'\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='订单表';";


}
