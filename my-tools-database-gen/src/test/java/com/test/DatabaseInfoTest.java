package com.test;

import com.yyself.tool.utils.TextUtils;
import net.sf.jsqlparser.parser.JSqlParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangyu
 * @date 2020/6/13 下午1:20
 */
public class DatabaseInfoTest {


    String sql_path = "/Users/yangyu/Desktop/test.sql";


    @Test
    public void main() {

        List<String> tables = new ArrayList<>();
        List<String> list = TextUtils.read(sql_path);
        boolean skip = false;
        boolean isFirstTable = true;
        StringBuilder tableBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String line = list.get(i);
            if (isNoteStart(line)) {
                skip = true;
            }
            if (isNoteEnd(line)) {
                skip = false;
            }
            if (isSkip(line) || skip) {
                continue;
            }
            if (line.startsWith("CREATE TABLE")) {
                if (!isFirstTable) {
                    tables.add(tableBuilder.toString());
                    tableBuilder = new StringBuilder();
                } else {
                    isFirstTable = false;
                }
            }
            tableBuilder.append(line);
        }
        tables.forEach(System.out::println);

    }

    public boolean isNoteStart(String line) {
        return line.startsWith("/*") || line.startsWith("/**");
    }

    public boolean isNoteEnd(String line) {
        return line.startsWith("*/") || line.startsWith("**/");
    }


    public boolean isSkip(String line) {
        for (String keyword : Arrays.asList("--", "SET", "DROP", "/*", "/**", "*/", "**/")) {
            if (line.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }


}
