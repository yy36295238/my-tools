package com.test.template;

import com.google.common.collect.Lists;
import com.yyself.tool.utils.TextUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/5 下午1:37
 */
public class VueTemplate {

    private static final List<String> COLUMNS = Arrays.asList("id", "name", "password", "birthday", "nick", "createUser", "createTime");

    private static final String ADD_FORM_DATA_KEY = "_addFormData_";
    private static final String FORM_DATA_JSON = "_formDataJson_";
    private static final String COLUMNS_DATA_JSON = "_columnsDataJson_";

    public static void main(String[] args) {
        String templatePath = TextUtils.userDir() + "/my-tools-web/src/main/resources/template/curd-template.vue";
        String genDemo = TextUtils.userDir() + "/my-tools-web/src/main/resources/template/gen-demo.vue";
        System.out.println(templatePath);
        String vueContent = TextUtils.readHtml(templatePath);
        String content = vueContent.replaceAll(ADD_FORM_DATA_KEY, addFormTemplate())
                .replaceAll(FORM_DATA_JSON, formDataJson(COLUMNS))
                .replaceAll(COLUMNS_DATA_JSON, columnsDataJson(COLUMNS));
        System.out.println(content);

        TextUtils.write(genDemo, content);

    }


    /**
     * <Row :gutter="20">
     * <Col span="12">
     * <FormItem label="类名称" label-position="top">
     * <Input v-model="formData.className" placeholder="类名称" />
     * </FormItem>
     * </Col>
     * <Col span="12">
     * <FormItem label="还原状态" label-position="top">
     * <Input v-model="formData.revert" placeholder="还原状态" />
     * </FormItem>
     * </Col>
     * </Row>
     */
    public static String addFormTemplate() {
        StringBuilder sb = new StringBuilder();
        List<List<String>> batch = batch(2, COLUMNS);
        for (List<String> list : batch) {
            StringBuilder rowBuilder = new StringBuilder("<Row :gutter=\"20\">\n");
            for (String column : list) {
                String col = "\t<Col span=\"12\">\n" +
                        "      <FormItem label=\"" + column + "\" label-position=\"top\">\n" +
                        "           <Input v-model=\"formData." + column + "\" placeholder=\"" + column + "\" />\n" +
                        "       </FormItem>\n" +
                        "     </Col>\n";
                rowBuilder.append(col);
            }
            rowBuilder.append("</Row>\n");
            sb.append(rowBuilder.toString());
        }
        return sb.toString();
    }

    private static List<List<String>> batch(int limit, List<String> list) {
        List<List<String>> newList = Lists.newArrayList();
        int size = list.size();
        if (limit < size) {
            //分批数
            int part = size / limit;
            System.out.println("共有 ： " + size + "条，！" + " 分为 ：" + part + "批");
            int last = 0;
            for (int i = 0; i < part; i++) {
                List<String> listPage = list.subList(i * limit, (i + 1) * limit);
                last = (i + 1) * limit;
                newList.add(listPage);
            }
            //表示最后剩下的数据
            List<String> lastList = list.subList(last, size);
            if (lastList.size() > 0) {
                newList.add(lastList);
            }
        } else {
            newList.add(list);
        }
        return newList;
    }

    /**
     * formData: {
     * className: '',
     * revert: '0',
     * },
     */
    private static String formDataJson(List<String> columns) {
        StringBuilder sb = new StringBuilder("formData: {\n");
        for (String column : columns) {
            sb.append(space(4)).append(column).append(": ").append("undefined").append(",\n");
        }
        sb.append(space(3)).append("},");
        return sb.toString();
    }

    private static String columnsDataJson(List<String> columns) {

        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            StringBuilder sb = new StringBuilder();
            sb.append(space(4)).append("{\n");
            sb.append(space(5)).append("title").append(": ").append("\"").append(column).append("\"").append(",\n");
            sb.append(space(5)).append("key").append(": ").append("undefined").append(",\n");
            sb.append(space(4)).append("},\n");
            builder.append(sb.toString());
        }
        return builder.toString();
    }

    private static String space(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

}
