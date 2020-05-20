package com.yyself.tool.database.make;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yyself.tool.database.DatabaseGenVo;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * @author yangyu
 */

public class MakeServiceImpl extends MakeBase {

    private static final String PREFIX = "I";

    public MakeServiceImpl(DatabaseGenVo vo) {
        super(vo);
    }

    @Override
    public JavaFile makeClass() throws IOException {
        //泛型 BaseMapper<user>
        ClassName managerService = ClassName.get("kot.bootstarter.kotmybatis.service.impl", "MapperManagerServiceImpl");
        ClassName service = ClassName.bestGuess(vo.getPackages() + ".service." + PREFIX + vo.getTableName() + "Service");
        ClassName entity = ClassName.get(vo.getPackages() + ".entity", vo.getTableName());

        TypeSpec.Builder serviceClassBuilder = TypeSpec.classBuilder(vo.getTableName() + "ServiceImpl")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("@author " + vo.getAuthor() + "\n")
                .addAnnotation(Service.class)
                .addSuperinterface(service)
                .superclass(ParameterizedTypeName.get(managerService, entity));

        return JavaFile.builder(vo.getPackages() + ".service.impl", serviceClassBuilder.build()).build();
    }

}

