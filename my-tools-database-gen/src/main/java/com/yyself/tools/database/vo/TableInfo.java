package com.yyself.tools.database.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/13 下午5:34
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableInfo {

    private String tableName;
    private List<ColumnInfo> columnInfos;
    private List<IndexInfo> indexInfos;

}
