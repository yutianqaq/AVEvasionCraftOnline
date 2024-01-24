package com.yutian4060.avevasioncraftonline.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static final String TEXT_CONTENT = """
            本工具仅供安全研究和教学目的使用，用户须自行承担因使用该工具而引起的一切法律及相关责任。
            作者概不对任何法律责任承担责任，且保留随时中止、修改或终止本工具的权利。使用者应当遵循当地法律法规，并理解并同意本声明的所有内容。
            https://github.com/yutianqaq
            """;

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void saveFileZIP(String zipFileName, String filePath, String outputShellcodeFilePath, String storageDirectory) throws IOException {
        Path storagePath = Path.of(storageDirectory);
        Files.createDirectories(storagePath);

        String zipPassword = "yutian";

        try (ZipFile zipFile = new ZipFile(storagePath + File.separator + zipFileName + ".zip", zipPassword.toCharArray())) {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

            // 添加文件到压缩包，包括文本内容
            List<File> filesToAdd = Arrays.asList(
                    new File(filePath),
                    new File(outputShellcodeFilePath),
                    createTextFileInMemory()
            );

            zipFile.addFiles(filesToAdd, zipParameters);
        }

        Files.delete(Path.of(filePath)); // 删除二进制文件
        Files.delete(Path.of(outputShellcodeFilePath)); // 删除其他文件

    }

    private static File createTextFileInMemory() throws IOException {
        Path tempTextFilePath = Files.createTempFile("README", ".txt");
        Files.write(tempTextFilePath, TEXT_CONTENT.getBytes());
        return tempTextFilePath.toFile();
    }

    public static byte[] readFileBytes(String filePath) {
        try {
            return Files.readAllBytes(Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copyFile(String sourcePath, String destinationPath) {
        try {
            Files.copy(Path.of(sourcePath), Path.of(destinationPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveFileBytes(Path filePath, byte[] content) {
        try {
            Files.write(filePath, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(Path directoryPath) throws IOException {
        try {
            FileSystemUtils.deleteRecursively(directoryPath);
            logger.info("Directory deletion successful: {}", directoryPath);
        } catch (IOException e) {
            logger.info("Directory deletion failed: {}", directoryPath);
        }
    }

}
