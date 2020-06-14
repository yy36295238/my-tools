package com.test;

import com.yyself.tools.database.DatabaseHelper;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.TableInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;

import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tools.database.DatabaseHelper.*;

/**
 * @Author yangyu
 * @create 2020/5/29 上午10:31
 */

@Slf4j
public class CreateTableTest {

    public static void main(String[] args) throws JSQLParserException {

//        System.out.println(indexList(createSql));

        List<ColumnInfo> columnInfos = DatabaseHelper.columnList(createSql1).stream().map(col -> ColumnInfo.builder()
                .name(col.getColumnName().replaceAll("`", "").replaceAll("\"", ""))
                .type(col.getColDataType().getDataType())
                .length(columnLength(col))
                .comment(comment(col))
                .other(String.join(" ", other(col)))
                .build()).collect(Collectors.toList());

        TableInfo.builder().columnInfos(columnInfos).indexInfos(indexList(createSql)).build();

        columnInfos.forEach(System.out::println);

    }

    private static final String createSql = "CREATE TABLE `t_human_statistics` (\n" +
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

    private static final String createSql1="CREATE TABLE `dictionary_history` (\n" +
            "\t`id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
            "\t`dic_id` int(11) NOT NULL,\n" +
            "\t`dic_code` varchar(64) DEFAULT '',\n" +
            "\t`dic_name` varchar(64) DEFAULT '',\n" +
            "\t`dic_value` text COMMENT '字典数据',\n" +
            "\t`date_time` datetime NOT NULL COMMENT '数据时间',\n" +
            "\t`date_username` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名',\n" +
            "\t`date_user_real_name` varchar(64) NOT NULL DEFAULT '' COMMENT '真实姓名',\n" +
            "\t`create_time` datetime NOT NULL,\n" +
            "\t`create_username` varchar(64) DEFAULT NULL,\n" +
            "\tPRIMARY KEY (`id`)\n" +
            ") ENGINE = InnoDB AUTO_INCREMENT = 111 CHARSET = utf8;";
}
