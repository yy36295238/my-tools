package com.yyself.tool.utils;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author yangyu
 * @create 2020/5/20 下午1:29
 */
@Slf4j
public class TextUtils {

    private static final List<String> DANGER_PATHS = Arrays.asList(userHome(), "/");

    /**
     * 读取文件
     */
    public static List<String> read(InputStream inputStream) {
        try {
            @Cleanup Reader reader = null;
            List<String> lines = new LinkedList<>();
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件
     */
    public static List<String> read(String fileName) {
        try {
            return Files.lines(Paths.get(fileName)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readText(String fileName) {
        return String.join("", Objects.requireNonNull(read(fileName)));
    }

    public static String readHtml(String fileName) {
        return String.join("\r", Objects.requireNonNull(read(fileName)));
    }

    /**
     * 删除文件
     */
    public static void delete(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除多级目录下的文件
     */
    public static void deleteDir(String dir) {
        log.warn("删除路径,{}", dir);
        if (isDangerPath(dir)) {
            throw new RuntimeException("不被允许执行," + dir);
        }
        if (!Files.exists(Paths.get(dir))) {
            log.warn("路径不存在,{}", dir);
            return;
        }
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    log.warn("删除文件,{}", file.getFileName());
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isDangerPath(String path) {
        return DANGER_PATHS.contains(path);
    }

    /**
     * 写入文件
     */
    public static void write(String fileName, String content) {
        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加文件
     */
    public static void writeAppend(String fileName, String content) {
        try {
            Files.write(Paths.get(fileName), (content + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     */
    public static void create(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建临时文件
     */
    private String createTmpText(String fileName, String content) {
        String path = null;
        try {
            path = Files.createTempFile(fileName, ".txt").toString();
            Files.write(Paths.get(path), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    /**
     * /Users/yangyu
     */
    public static String userHome() {
        return System.getProperties().getProperty("user.home");
    }

    /**
     * /Users/yangyu/workspace/yyself/my-tools
     */
    public static String userDir() {
        return System.getProperties().getProperty("user.dir");
    }

    private static String dir(String name) {
        return System.getProperties().getProperty(name);
    }

    public static void main(String[] args) {
        System.out.println(userHome());
    }


}
