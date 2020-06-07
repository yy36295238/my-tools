package com.yyself.tools.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yangyu
 * @create 2020/5/21 下午2:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassModel {
    private String className;
    private String classInfo;
    private String lang;

    public ClassModel(String className, String classInfo) {
        this.className = className;
        this.classInfo = classInfo;
    }
}
