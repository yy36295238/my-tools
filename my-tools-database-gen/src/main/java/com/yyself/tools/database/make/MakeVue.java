package com.yyself.tools.database.make;

import com.google.common.collect.Lists;
import com.yyself.tool.utils.CommonUtils;
import com.yyself.tool.utils.TextUtils;
import com.yyself.tools.database.vo.ClassModel;
import com.yyself.tools.database.vo.ColumnInfo;
import com.yyself.tools.database.vo.DatabaseGenVo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.yyself.tool.utils.CommonUtils.lowerName;


/**
 * @author yangyu
 */

public class MakeVue {

    private static final String ADD_FORM_DATA_KEY = "_addFormData_";
    private static final String FORM_DATA_JSON = "_formDataJson_";
    private static final String COLUMNS_DATA_JSON = "_columnsDataJson_";
    private static final String API = "_api_";
    private static final String INIT_FORM = "_initForm_";


    public static void makeVue(DatabaseGenVo vo) {

        List<ColumnInfo> columnInfoList = vo.getColumnInfos().stream().map(c -> ColumnInfo.builder().name(CommonUtils.camelCaseName(c.getName().toLowerCase())).comment(StringUtils.isBlank(c.getComment()) ? c.getName() : c.getComment()).build()).collect(Collectors.toList());

        String userDir = TextUtils.userDir().replace("target", "").replace("my-tools-web", "");

        String templatePath = userDir + "/my-tools-web/src/main/resources/template/curd-template.vue";
        String genDemo = userDir + "/my-tools-web/src/main/resources/template/gen-demo.vue";
        String vueContent = TextUtils.readHtml(templatePath);
        String content = vueContent.replaceAll(ADD_FORM_DATA_KEY, addFormTemplate(columnInfoList))
                .replaceAll(FORM_DATA_JSON, formDataJson(columnInfoList))
                .replaceAll(COLUMNS_DATA_JSON, columnsDataJson(columnInfoList))
                .replaceAll(API, "/api/v1/" + lowerName(vo.getTableName()))
                .replaceAll(INIT_FORM, initForm(columnInfoList));

        vo.getClassInfoList().add(ClassModel.builder().tableName(vo.getTableName()).className(vo.getClassName() + ".vue").classInfo(content).lang("vue").build());
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
    public static String addFormTemplate(List<ColumnInfo> columnInfoList) {
        StringBuilder sb = new StringBuilder();
        List<List<ColumnInfo>> batch = batch(2, columnInfoList);
        for (List<ColumnInfo> list : batch) {
            StringBuilder rowBuilder = new StringBuilder("<Row :gutter=\"20\">\n");
            for (ColumnInfo column : list) {
                String col = "\t<Col span=\"12\">\n" +
                        "      <FormItem label=\"" + column.getComment() + "\" label-position=\"top\">\n" +
                        "           <Input v-model=\"formData." + column.getName() + "\" placeholder=\"" + column.getComment() + "\" />\n" +
                        "       </FormItem>\n" +
                        "     </Col>\n";
                rowBuilder.append(col);
            }
            rowBuilder.append("</Row>\n");
            sb.append(rowBuilder.toString());
        }
        return sb.toString();
    }

    private static List<List<ColumnInfo>> batch(int limit, List<ColumnInfo> list) {
        List<List<ColumnInfo>> newList = Lists.newArrayList();
        int size = list.size();
        if (limit < size) {
            //分批数
            int part = size / limit;
            System.out.println("共有 ： " + size + "条，！" + " 分为 ：" + part + "批");
            int last = 0;
            for (int i = 0; i < part; i++) {
                List<ColumnInfo> listPage = list.subList(i * limit, (i + 1) * limit);
                last = (i + 1) * limit;
                newList.add(listPage);
            }
            //表示最后剩下的数据
            List<ColumnInfo> lastList = list.subList(last, size);
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
    private static String formDataJson(List<ColumnInfo> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append(space(2)).append("formData: {\n");
        for (ColumnInfo column : columns) {
            sb.append(space(3)).append(column.getName()).append(": ").append("undefined").append(",\n");
        }
        sb.append(space(2)).append("},");
        return sb.toString();
    }

    /**
     * {
     * title: "ID",
     * key: "id",
     * },
     * {
     * title: "类名称",
     * key: "className",
     * },
     */
    private static String columnsDataJson(List<ColumnInfo> columns) {

        StringBuilder builder = new StringBuilder();
        for (ColumnInfo column : columns) {
            StringBuilder sb = new StringBuilder();
            sb.append(space(2)).append("{\n");
            sb.append(space(3)).append("title").append(": ").append("\"").append(column.getComment()).append("\"").append(",\n");
            sb.append(space(3)).append("key").append(": ").append("\"").append(column.getName()).append("\"").append(",\n");
            sb.append(space(2)).append("},\n");
            builder.append(sb.toString());
        }
        return builder.toString();
    }

    /**
     * if (row) {
     * this.formData.id = row.id;
     * this.formData.className = row.className;
     * this.formData.revert = row.revert;
     * } else {
     * this.formData.className = undefined;
     * this.formData.revert = undefined;
     * }
     */

    public static String initForm(List<ColumnInfo> columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(space(2)).append("if (row) {\n");
        for (ColumnInfo column : columns) {
            StringBuilder sb = new StringBuilder();
            sb.append(space(3)).append("this.formData.").append(column.getName()).append(" = row.").append(column.getName()).append(";\n");
            builder.append(sb.toString());
        }
        builder.append(space(2)).append("} else {\n");
        for (ColumnInfo column : columns) {
            if (column.isPk()) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(space(3)).append("this.formData.").append(column.getName()).append(" = undefined").append(";\n");;
            builder.append(sb.toString());
        }
        return builder.append(space(2)).append("}\n").toString();
    }

    private static String space(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }


}

