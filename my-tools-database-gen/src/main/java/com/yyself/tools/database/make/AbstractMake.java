package com.yyself.tools.database.make;

import com.squareup.javapoet.JavaFile;
import com.yyself.tools.database.vo.ClassModel;
import com.yyself.tools.database.vo.DatabaseGenVo;

import java.io.File;
import java.io.IOException;

/**
 * @author yangyu
 * @create 2020/5/20 下午2:23
 */
public abstract class AbstractMake {

    DatabaseGenVo vo;

    public AbstractMake(DatabaseGenVo vo) {
        this.vo = vo;
    }

    public abstract ClassModel makeClass() throws IOException;

    public void make() throws IOException {
        ClassModel classModel = this.makeClass();
        JavaFile javaFile = classModel.getJavaFile();
        classModel.setTableName(vo.getTableName());
        classModel.setClassInfo(javaFile.toString());
        classModel.setClassName(javaFile.typeSpec.name);
        javaFile.writeTo(new File(vo.getFilePath()));
        vo.getClassInfoList().add(classModel);
    }

}
