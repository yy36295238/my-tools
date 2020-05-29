package com.test;

import com.yyself.tools.database.DatabaseHelper;
import net.sf.jsqlparser.JSQLParserException;

/**
 * @Author yangyu
 * @create 2020/5/29 上午10:31
 */
public class CreateTableTest {

    public static void main(String[] args) throws JSQLParserException {

        System.out.println(DatabaseHelper.indexs(cerateSql));

    }

    private static final String cerateSql = "CREATE TABLE `t_human_statistics` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "  `create_year` int(255) DEFAULT NULL COMMENT '年',\n" +
            "  `create_month` int(255) DEFAULT NULL COMMENT '月',\n" +
            "  `create_day` int(255) DEFAULT NULL COMMENT '日',\n" +
            "  `create_hour` int(255) DEFAULT NULL COMMENT '小时',\n" +
            "  `rc_type` varchar(20) DEFAULT NULL COMMENT 'rc_type',\n" +
            "  `gender` smallint(6) DEFAULT NULL COMMENT '性别(1-男,2-女)',\n" +
            "  `age` int(11) DEFAULT NULL COMMENT '年龄',\n" +
            "  `num` int(11) DEFAULT NULL COMMENT '数量',\n" +
            "  `create_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `create_time` (`create_time`) USING BTREE,\n" +
            "  KEY `create_year` (`create_year`,`create_month`,`create_day`,`create_hour`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
}
