package com.yutian4060.avevasioncraftonline.controller;

import com.yutian4060.avevasioncraftonline.dto.CompilationResponseDTO;
import com.yutian4060.avevasioncraftonline.dto.ShellcodeUploadDTO;
import com.yutian4060.avevasioncraftonline.enums.Result;
import com.yutian4060.avevasioncraftonline.service.CompileService;
import com.yutian4060.avevasioncraftonline.utils.FileUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
public class CompilerController {

    @Resource
    CompileService compileService;

    @Value("${bypassav.storage-directory}")
    String downloadDirector;

    private static final Logger logger = LoggerFactory.getLogger(CompilerController.class);

    @PostMapping("/api/compiler")
    public Result shellcodeUpload(@ModelAttribute ShellcodeUploadDTO shellcodeUploadDTO) throws IOException {
        CompilationResponseDTO result = null;

        logger.info("Received shellcode upload:");
        logger.info("Template Language: {}", shellcodeUploadDTO.getTemplateLanguage());
        logger.info("Shellcode length: {}", shellcodeUploadDTO.getShellcode().getBytes().length);
        logger.info("Template Name: {}", shellcodeUploadDTO.getTemplateName());
        logger.info("Transformation: {}", shellcodeUploadDTO.getTransformation());
        logger.info("Storage Type: {}", shellcodeUploadDTO.getStorageType());
        logger.info("Additional Parameter: {}", shellcodeUploadDTO.getAdditionalParameter());

        ShellcodeUploadDTO.StorageType storageType = shellcodeUploadDTO.getStorageType();

        if (shellcodeUploadDTO.getShellcode().getBytes().length > 5200000 || shellcodeUploadDTO.getShellcode().getBytes().length < 200) {
            logger.warn("File Size: {}", shellcodeUploadDTO.getShellcode().getBytes().length);
            return Result.error();
        }

        if (storageType != ShellcodeUploadDTO.StorageType.REMOTE &&
                storageType != ShellcodeUploadDTO.StorageType.EMBEDDED &&
                storageType != ShellcodeUploadDTO.StorageType.LOCAL) {
            logger.warn("storageType: {}", storageType);
            return Result.error();
        }

        String templateLanguage = shellcodeUploadDTO.getTemplateLanguage();
        switch (templateLanguage) {
            case "c" -> result = compileService.compileCodeC(shellcodeUploadDTO);
            case "nim" -> result =  compileService.compileCodeNim(shellcodeUploadDTO);
            case "go" -> result =  compileService.compileCodeGo(shellcodeUploadDTO);
            default -> Result.error();
        }

        if (result == null) {
            return Result.error();
        }

        return Result.success(result);
    }

    @GetMapping("/api/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) {
        byte[] fileBytes = FileUtils.readFileBytes(downloadDirector + File.separator + filename.substring(0, filename.lastIndexOf(".")) + File.separator + filename);

        String contentType = "application/octet-stream";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
