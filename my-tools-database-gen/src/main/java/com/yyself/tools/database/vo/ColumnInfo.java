package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/5 下午8:06
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColumnInfo {
    private String name;
    private String type;
    private String length;
    private String comment;
    private String other;

    private List<IndexInfo> indexInfos;
}
