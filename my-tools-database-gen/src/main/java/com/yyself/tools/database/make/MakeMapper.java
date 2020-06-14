package com.yyself.tools.database.make;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yyself.tools.database.vo.DatabaseGenVo;

import javax.lang.model.element.Modifier;

/**
 * @author yangyu
 */

public class MakeMapper extends MakeBase {
    public MakeMapper(DatabaseGenVo vo) {
        super(vo);
    }

    @Override
    public JavaFile makeClass() {

        //泛型 BaseMapper<user>
        ClassName baseMapper = ClassName.get("kot.bootstarter.kotmybatis.mapper", "BaseMapper");
        ClassName entity = ClassName.get(vo.getPackages() + ".entity", vo.getTableName());

        TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(vo.getTableName() + "Mapper")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("@author " + vo.getAuthor() + "\n")
                .addSuperinterface(ParameterizedTypeName.get(baseMapper, entity));

        return JavaFile.builder(vo.getPackages() + ".mapper", classBuilder.build()).build();
    }

}

