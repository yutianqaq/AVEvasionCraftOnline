package com.yutian4060.avevasioncraftonline.utils;

import com.yutian4060.avevasioncraftonline.config.BypassAVConfigProperties;
import com.yutian4060.avevasioncraftonline.service.impl.CompileServiceImpl;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

@Component
public class CompilerCode {

    public static BypassAVConfigProperties bypassAVConfigProperties;
    private static final Logger logger = LoggerFactory.getLogger(CompilerCode.class);
    private static String WORKING_DIRECTORY;
    public static final int RANDOM_FILENAME_LEN = 8; // 指定固定的编译工作目录
    @Autowired
    public void setApplicationProperties(BypassAVConfigProperties bypassAVConfigProperties) {
        CompilerCode.bypassAVConfigProperties = bypassAVConfigProperties;
    }

    @PostConstruct
    private void initializeConstants() {
        WORKING_DIRECTORY = bypassAVConfigProperties.getCompilerWorkDirectory();
    }

    public static void compileNim(String destinationPath, String builderWorkPath) {
        List<String> command = List.of("nim", "c", "-d=release", "-d=mingw", "--app=gui", "-d:strip", "--opt:size",
                "--cpu=amd64", "-o:" + destinationPath + ".exe", destinationPath);
        logger.info("Builder Command: {}", command);
        executeCommand(command, builderWorkPath);
    }

    public static void compileGo(String destinationPath, String builderWorkPath) {
        List<String> command = List.of("go", "build", "-ldflags", "-H=windowsgui", "-o", destinationPath + ".exe", destinationPath);
        logger.info("Builder Command: {}", command);
        executeCommand(command, builderWorkPath);
    }

    public static void compileC(String destinationPath, String builderWorkPath) {
        List<String> command = List.of("x86_64-w64-mingw32-gcc", "-o", destinationPath + ".exe", destinationPath);
        logger.info("Builder Command: {}", command);
        executeCommand(command, builderWorkPath);
    }

    private static void executeCommand(List<String> command, String builderWorkPath) {
        try {

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(WORKING_DIRECTORY + File.separator + builderWorkPath));
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("Compilation successful");
            } else {
                logger.error("Compilation failed with exit code: {}", exitCode);
                printErrorStream(process);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Error during compilation", e);
        }
    }

    private static void printErrorStream(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logger.error(line);
            }
        }
    }

    public static String getRandomDirectorName() {
        return UUID.randomUUID().toString();
    }

}
