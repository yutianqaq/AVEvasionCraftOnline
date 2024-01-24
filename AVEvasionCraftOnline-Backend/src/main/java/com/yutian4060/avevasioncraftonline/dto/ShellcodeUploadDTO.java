package com.yutian4060.avevasioncraftonline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShellcodeUploadDTO {
    private MultipartFile shellcode;

    private String templateLanguage;
    private String templateName;
    private String transformation;
    private StorageType storageType;
    private String additionalParameter;  // 文件名或者url

    public enum StorageType {
        EMBEDDED,
        LOCAL,
        REMOTE
    }
}
