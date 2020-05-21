package com.yyself.tools.database.make;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yyself.tools.database.DatabaseGenVo;

import javax.lang.model.element.Modifier;
import java.io.IOException;

/**
 * @author yangyu
 */

public class MakeService extends MakeBase {

    private static final String PREFIX = "I";

    public MakeService(DatabaseGenVo vo) {
        super(vo);
    }

    @Override
    public JavaFile makeClass() throws IOException {
        //泛型 BaseMapper<user>
        ClassName managerService = ClassName.get("kot.bootstarter.kotmybatis.service", "MapperManagerService");
        ClassName entity = ClassName.get(vo.getPackages() + ".entity", vo.getTableName());

        TypeSpec.Builder serviceClassBuilder = TypeSpec.interfaceBuilder(PREFIX + vo.getTableName() + "Service")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("@author " + vo.getAuthor() + "\n")
                .addSuperinterface(ParameterizedTypeName.get(managerService, entity));

        return JavaFile.builder(vo.getPackages() + ".service", serviceClassBuilder.build()).build();
    }


}

