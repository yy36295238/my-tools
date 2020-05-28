package com.yyself.tool.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author yangyu
 * @create 2020/5/27 下午1:46
 */
@Slf4j
public class ZipUtils {
    static final int BUFFER = 8192;

    public static void compress(String zipName, String... packagePaths) {
        compress(zipName, Arrays.asList(packagePaths), new ArrayList<>());
    }

    public static void compress(String zipName, List<String> packagePaths) {
        compress(zipName, packagePaths, new ArrayList<>());
    }

    public static void compress(String zipName, String packagePath, String exclude) {
        compress(zipName, Collections.singletonList(packagePath), Collections.singletonList(exclude));
    }

    public static void compress(String zipName, List<String> packagePaths, List<String> excludeList) {
        zipName = zipName + ".zip";
        ZipOutputStream out;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(zipName));
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            for (String path : packagePaths) {
                File file = new File(path);

                compress(file, out, basedir, excludeList);
            }
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void compress(File file, ZipOutputStream out, String basedir, List<String> excludeList) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            log.info("压缩目录,{}", basedir + file.getName());
            compressDirectory(file, out, basedir, excludeList);
        } else {
            if (!excludeList.contains(file.getName())) {
                log.info("压缩文件,{}", basedir + file.getName());
                compressFile(file, out, basedir);
            } else {
                log.info("排除文件,{}", basedir + file.getName());
            }
        }
    }

    /**
     * 压缩目录
     */
    private static void compressDirectory(File dir, ZipOutputStream out, String basedir, List<String> excludeList) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            /* 递归 */
            compress(file, out, basedir + dir.getName() + "/", excludeList);
        }
    }

    /**
     * 压缩文件
     */
    private static void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte[] data = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        String prefix = "/Users/yangyu/tmp/database/com/company/example/";
        String prefix = "/Users/yangyu/tmp/database/";
        ZipUtils.compress(prefix + "user.zip", prefix);
    }
}
