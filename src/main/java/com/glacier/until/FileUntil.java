package com.glacier.until;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 关于文件操作类
 * @since 2024/9/25 23:00
 */
public class FileUntil {


    /**
     * 读取文件内容
     */
    public static String methodGetFileContent(String filePath, String fileName) {
        String content = "";
        try {
            File file = new File(filePath + fileName);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuffer SB = new StringBuffer();
            while ((line = br.readLine()) != null) {
                SB.append(line).append("\n");
            }
            content = SB.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return content;
    }

    /**
     * 读取文件内容，按行读取
     */
    public static List<String> methodGetFileContentByLine(String filePath, String fileName) {
        try {
            List<String> lines = new ArrayList<>();
            InputStreamReader fReader = new InputStreamReader(Files.newInputStream(Paths.get(filePath + fileName)), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(fReader);
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.toString());
            }
            reader.close();
            return lines;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while reading the file.");
        }
    }

    /**
     * 获取文件夹下全部文件名称
     */
    public static List<String> methodGetFileNames(String filePath) {
        try {
            // 检查路径是否为目录
            Path path = Paths.get(filePath);
            if (!Files.isDirectory(path)) {
                throw new NotDirectoryException("The provided path is not a directory: " + filePath);
            }
            // 1为 仅限当前目录层级
            try (Stream<Path> walk = Files.walk(path, 1)) {
                return walk
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while reading the directory.");
        }
    }


    /**
     * 保存文件的方法--覆盖
     */
    public static void methodSaveFile(String savePath, String fileName, String content) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(savePath + fileName, false), 331074);
            bufferedOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 保存文件的方法--追加
     */
    public static void methodSaveFileAppend(String savePath, String fileName, String content) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(savePath + fileName, true), 331074);
            bufferedOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 创建文件夹
     */
    public static void methodCreateFolder(String folderPath) {
        File file = new File(folderPath);
        if (!file.exists()) {
            boolean a = file.mkdirs();
            System.out.println("创建文件夹 :\t" + folderPath + "\t" + a);
        }
    }

}
