package com.yutian4060.avevasioncraftonline.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void saveFileZIP() throws IOException {

        String filePath = "C:\\1bypassAVOnline\\calc.exe";
        String outputZipFilePath = "C:\\1bypassAVOnline\\download\\Test\\";
//        System.out.println(FileUtils.saveFileZIP(filePath,
//                "C:\\1bypassAVOnline\\calc.bin", outputZipFilePath));
    }

    @Test
    void readFileBytes() {
        System.out.println(FileUtils.readFileBytes("C:\\1bypassAVOnline\\calc.bin"));

    }

    @Test
    void copyFile() {
    }

    @Test
    void saveFileBytes() {
    }
}
