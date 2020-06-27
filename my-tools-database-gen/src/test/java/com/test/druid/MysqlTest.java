package com.test.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.util.JdbcConstants;
import com.yyself.tools.database.vo.IndexInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/14 下午6:54
 */
public class MysqlTest {

    @Test
    public void tableInfoTest() {
        String dbType = JdbcConstants.MYSQL;

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);



        for (SQLStatement sqlStatement : stmtList) {
//            System.out.println(sqlStatement.getClass());
            if (sqlStatement instanceof SQLCreateTableStatement) {
                SQLCreateTableStatement tableStatement = (SQLCreateTableStatement) sqlStatement;
                System.out.println("tableName: " + tableStatement.getName());
                System.out.println("pk: " + tableStatement.findPrimaryKey().getColumns().get(0).getExpr());
                for (SQLTableElement sqlTableElement : tableStatement.getTableElementList()) {
                    System.out.println(sqlTableElement.getClass());
                    if (sqlTableElement instanceof SQLColumnDefinition) {
                        SQLColumnDefinition def = (SQLColumnDefinition) sqlTableElement;
                        String joinPrint = joinPrint(def.getName().getSimpleName(), // 1、名称
                                def.getDataType().getName(), // 2、类型
                                def.getDataType().getArguments().size() > 0 ? def.getDataType().getArguments().get(0).toString() : "",// 3、长度
                                String.valueOf(def.containsNotNullConstaint()), // 4、是否为空
                                (def.getDefaultExpr() == null ? "" : def.getDefaultExpr().toString()) // 5、默认值
                        );
                        System.err.println(joinPrint);
                    } else if (sqlTableElement instanceof MySqlPrimaryKey) {// 主键索引
                        MySqlPrimaryKey primaryKey = (MySqlPrimaryKey) sqlTableElement;
                        SQLSelectOrderByItem item = primaryKey.getColumns().get(0);
                        IndexInfo.builder()
                                .key(item.getExpr().toString())
                                .type("PRIMARY KEY")
                                .build();

                    } else if (sqlTableElement instanceof MySqlKey) {// 普通索引
                        MySqlKey mySqlKey = (MySqlKey) sqlTableElement;
                        IndexInfo.builder()
                                .key(mySqlKey.getName().getSimpleName())
                                .type(mySqlKey.getIndexType())
                                .indexes(mySqlKey.getComment().toString())
                                .build();
                    }

                }
                System.err.println("index:" + tableStatement);

            }
        }

    }

    private String joinPrint(String... strings) {
        return String.join(",", strings);
    }

    private static final String sql = "CREATE TABLE `customer` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '主键',\n" +
            "  `name` varchar(255) NOT NULL COMMENT '名称',\n" +
            "  `amount` double DEFAULT NULL COMMENT '金额',\n" +
            "  `text` text COMMENT '文本',\n" +
            "  `status` smallint(6) NOT NULL DEFAULT '1' COMMENT '状态',\n" +
            "  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `idx_status` (`status`) USING BTREE COMMENT '状态索引',\n" +
            "  KEY `idx_name_createTime` (`name`,`create_time`) USING BTREE COMMENT '名称_时间'\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='客户表';";

//    private static final String sql = "CREATE TABLE `t_human_statistics` (\n" +
//            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
//            "  `create_year` int(255) DEFAULT NULL COMMENT '年',\n" +
//            "  `create_month` int(255) DEFAULT NULL COMMENT '月',\n" +
//            "  `create_day` int(255) DEFAULT NULL COMMENT '日',\n" +
//            "  `create_hour` int(255) DEFAULT NULL COMMENT '小时',\n" +
//            "  `rc_type` varchar(20) DEFAULT NULL COMMENT 'rc_type',\n" +
//            "  `gender` smallint(6) DEFAULT 2 COMMENT '性别(1-男,2-女)',\n" +
//            "  `age` int(11) DEFAULT NULL COMMENT '年龄',\n" +
//            "  `num` int(11) DEFAULT NULL COMMENT '数量',\n" +
//            "  `create_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',\n" +
//            "  PRIMARY KEY (`id`),\n" +
//            "  KEY `create_time` (`create_time`) USING BTREE,\n" +
//            "  KEY `create_year` (`create_year`,`create_month`,`create_day`,`create_hour`) USING BTREE\n" +
//            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

}
