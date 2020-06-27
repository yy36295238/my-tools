package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyu
 * @date 2020/6/25 下午10:48
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentInfo {
    private String tableName;
    private String columnName;
    private String type;
    private String comment;


}
