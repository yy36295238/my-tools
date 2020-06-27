package com.yyself.tools.database.vo;

import com.squareup.javapoet.JavaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author yangyu
 * @create 2020/5/21 下午2:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassModel {
    private String tableName;
    private String className;
    private String classInfo;
    private String lang;
    private String classType;
    private JavaFile javaFile;
    private List<ClassModel> classModelList;

}
