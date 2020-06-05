package com.yyself.tools.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyu
 * @date 2020/6/5 下午8:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnInfo {
    private String name;
    private String comment;
}
