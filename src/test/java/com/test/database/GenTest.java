package com.test.database;


import com.alibaba.fastjson.JSONObject;
import com.yyself.tool.database.DatabaseGenVo;
import org.junit.jupiter.api.Test;

/**
 * @Author yangyu
 * @create 2020/5/20 上午11:20
 */
public class GenTest {

    @Test
    public void gen() {
        DatabaseGenVo vo = DatabaseGenVo.builder()
                .ddl(ddl())
                .author("yangyu")
                .enableSwagger(true)
                .packages("com.yy.test")
                .prefix("t_")
                .build();
        System.out.println(JSONObject.toJSONString(vo,true));
    }

    private static String ddl() {
        return "CREATE TABLE `t_jsonpath_parser` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `param_id` bigint(20) DEFAULT NULL COMMENT '变量主键',\n" +
                "  `business_id` varchar(20) NOT NULL,\n" +
                "  `app_id` varchar(20) NOT NULL,\n" +
                "  `rc_type` varchar(20) NOT NULL,\n" +
                "  `data_source` varchar(30) NOT NULL COMMENT '数据源名称',\n" +
                "  `var_name` varchar(64) NOT NULL COMMENT '变量名称',\n" +
                "  `path` varchar(255) NOT NULL COMMENT '变量路径',\n" +
                "  `var_type` varchar(20) NOT NULL COMMENT '变量类型,String,Ingeger,Boolean,...',\n" +
                "  `defalut_val` varchar(255) DEFAULT NULL COMMENT '变量为空时,默认值',\n" +
                "  `create_user` bigint(20) NOT NULL DEFAULT '-1' COMMENT '创建人',\n" +
                "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;";
    }
}
