package com.yyself.tool.utils;

import com.yyself.tool.exception.KotException;
import lombok.Cleanup;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * @author yangyu
 * @date 2020/6/24 下午7:51
 */
public class FileUtils {

    public static void download(String filePath, String fileName, HttpServletResponse response) {
        download(new File(filePath), fileName, response);
    }

    public static void download(File file, String fileName, HttpServletResponse response) {
        if (file.exists()) {
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("file-name", fileName);
            //  自定义响应头信息
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Access-Control-Expose-Headers", "file-name");
            byte[] buffer = new byte[1024];
            try {
                @Cleanup FileInputStream fis = new FileInputStream(file);
                @Cleanup BufferedInputStream bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                throw new KotException(e);
            }
        }
    }
}
