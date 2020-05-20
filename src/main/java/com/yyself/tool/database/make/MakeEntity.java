package com.yyself.tool.database.make;

import com.squareup.javapoet.*;
import com.yyself.tool.database.DatabaseGenVo;
import com.yyself.tool.utils.CommonUtils;
import io.swagger.annotations.ApiModelProperty;
import kot.bootstarter.kotmybatis.annotation.Column;
import kot.bootstarter.kotmybatis.annotation.ID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Modifier;

import static com.yyself.tool.database.DatabaseHelper.comment;

/**
 * @author yangyu
 */

public class MakeEntity extends MakeBase {


    public MakeEntity(DatabaseGenVo vo) {
        super(vo);
    }

    @Override
    public JavaFile makeClass() {

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(vo.getClassName());

        vo.getColumnDefinitionList().forEach(c -> {
            String columnName = c.getColumnName();
            String dataType = c.getColDataType().getDataType();
            String comment = comment(c);
            final FieldSpec.Builder fieldBuilder = FieldSpec.builder(CommonUtils.changeType(dataType), CommonUtils.camelCaseName(columnName.toLowerCase()), Modifier.PRIVATE)
                    .addJavadoc(comment + "\n");

            if (vo.isEnableSwagger()) {
                fieldBuilder.addAnnotation(AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("value", "$S", comment(c))
                        .addMember("dataType", "$S", CommonUtils.changeType(dataType).getSimpleName())
                        .addMember("name", "$S", CommonUtils.camelCaseName(columnName.toLowerCase()))
                        .build());
            }
            if (vo.isOpenKotMybatis()) {
                fieldBuilder.addAnnotation(AnnotationSpec.builder(Column.class).addMember("value", "$S", columnName).build());
                fieldBuilder.addAnnotation(AnnotationSpec.builder(ID.class).addMember("value", "$S", columnName).build());
            }
            classBuilder.addField(fieldBuilder.build());

        });


        // 公共方法
        classBuilder.addModifiers(Modifier.PUBLIC)
                // 添加类注解
                .addAnnotation(Data.class)
                .addAnnotation(AllArgsConstructor.class)
                .addAnnotation(NoArgsConstructor.class)
                .addAnnotation(Builder.class)
                // 注解添加属性
                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("kot.bootstarter.kotmybatis.annotation.TableName"))
                        .addMember("value", "$S", vo.getRealTableName())
                        .build())
                .addJavadoc("@author " + vo.getAuthor() + "\n");
        return JavaFile.builder(vo.getPackages() + ".entity", classBuilder.build()).build();

    }

}

