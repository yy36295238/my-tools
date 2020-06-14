package com.test;

import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

/**
 * @author yangyu
 * @date 2020/6/14 下午6:54
 */
public class DruidTest {

    @Test
    public void tableInfoTest() {
        String dbType = JdbcConstants.MYSQL;
        SchemaRepository repository = new SchemaRepository(dbType);
        repository.acceptDDL(sql);
        System.out.println(repository.console("show columns from t_human_statistics"));

        MySqlCreateTableStatement createTableStmt = (MySqlCreateTableStatement) repository.findTable("t_human_statistics").getStatement();
        System.out.println(createTableStmt.getTableOptions());
        System.out.println(createTableStmt.getHints());
        System.out.println();
        for (SQLObject child : createTableStmt.getChildren()) {
            if (child instanceof SQLColumnDefinition) {
                System.out.println(child.toString());
                SQLColumnDefinition def = (SQLColumnDefinition) child;
                String joinPrint = joinPrint(def.getName().getSimpleName(),
                        def.getDataType().getName(),
                        def.getDataType().getArguments().size() > 0 ? def.getDataType().getArguments().get(0).toString() : "",
                        String.valueOf(def.containsNotNullConstaint()),
                        (def.getDefaultExpr() == null ? "" : def.getDefaultExpr().toString()),
                        def.getComment().toString());
                System.err.println(joinPrint);

            }
        }


//        System.out.println(createTableStmt.getName().getSimpleName());
//        System.out.println(repository.console("show full columns from t_human_statistics"));

    }

    private String joinPrint(String... strings) {
        return String.join(",", strings);
    }

    private static final String sql = "CREATE TABLE `t_human_statistics` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "  `create_year` int(255) DEFAULT NULL COMMENT '年',\n" +
            "  `create_month` int(255) DEFAULT NULL COMMENT '月',\n" +
            "  `create_day` int(255) DEFAULT NULL COMMENT '日',\n" +
            "  `create_hour` int(255) DEFAULT NULL COMMENT '小时',\n" +
            "  `rc_type` varchar(20) DEFAULT NULL COMMENT 'rc_type',\n" +
            "  `gender` smallint(6) DEFAULT 2 COMMENT '性别(1-男,2-女)',\n" +
            "  `age` int(11) DEFAULT NULL COMMENT '年龄',\n" +
            "  `num` int(11) DEFAULT NULL COMMENT '数量',\n" +
            "  `create_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `create_time` (`create_time`) USING BTREE,\n" +
            "  KEY `create_year` (`create_year`,`create_month`,`create_day`,`create_hour`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

}
