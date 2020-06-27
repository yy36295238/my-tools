package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyu
 * @date 2020/6/13 下午5:34
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndexInfo {

    private String tableName;
    private String key;
    private String type;
    private String using;
    private String indexes;
    private String comment;

}
