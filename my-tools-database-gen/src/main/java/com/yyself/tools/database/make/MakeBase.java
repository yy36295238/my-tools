package com.yyself.tools.database.make;

import com.squareup.javapoet.JavaFile;
import com.yyself.tools.database.vo.ClassModel;
import com.yyself.tools.database.vo.DatabaseGenVo;

import java.io.File;
import java.io.IOException;

/**
 * @Author yangyu
 * @create 2020/5/20 下午2:23
 */
public abstract class MakeBase {

    DatabaseGenVo vo;

    public MakeBase(DatabaseGenVo vo) {
        this.vo = vo;
    }

    public abstract JavaFile makeClass() throws IOException;

    public void make() throws IOException {
        JavaFile javaFile = this.makeClass();
        javaFile.writeTo(new File(vo.getFilePath()));
        vo.getClassInfoList().add(new ClassModel(javaFile.typeSpec.name, javaFile.toString()));
    }

}
