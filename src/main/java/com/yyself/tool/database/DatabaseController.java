package com.yyself.tool.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yangyu
 * @create 2020/5/19 下午3:17
 */

@Slf4j
@RestController
@RequestMapping("/database")
public class DatabaseController {

    @PostMapping("/gen")
    public String gen(String ddl) {
        log.info(ddl);
        return "Success";
    }

}
