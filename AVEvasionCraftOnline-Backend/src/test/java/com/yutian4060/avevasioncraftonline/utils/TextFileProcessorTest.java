package com.yutian4060.avevasioncraftonline.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.yutian4060.avevasioncraftonline.utils.FileUtils.readFileBytes;
import static com.yutian4060.avevasioncraftonline.utils.TextFileProcessor.*;

class TextFileProcessorTest {

    @Test
    void replaceVariableNamesTest() {
        String code = """
                func fetchShellcode(string url);
                func delayedLoading();
                func checkDomain();
                """;

        List<String> functionNamesToReplace = Arrays.asList(
                // c
                "calc_payload", "payload_len", "calcSt", "calcTH", "oldProtectCalc",
                // nim
                "tId", "tHandle", "pHandle", "rPtr", "bytesWritten",
                // golang
                "fetchShellcode", "delayedLoading", "checkDomain"
        );
        System.out.printf("replaceVariableNamesTest: %s\n", replaceFunctionNames(code, functionNamesToReplace));;
    }

    @Test
    void convertToHexStringWithoutPrefixTest() {

        String filePath = "C:\\1bypassAVOnline\\calc.bin";
        System.out.printf("convertToHexStringWithoutPrefixTest: %s\n", convertToHexStringWithoutPrefix(readFileBytes(filePath)));
    }

}
