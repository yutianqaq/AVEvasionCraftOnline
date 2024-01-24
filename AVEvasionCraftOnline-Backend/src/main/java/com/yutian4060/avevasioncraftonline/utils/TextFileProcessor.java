package com.yutian4060.avevasioncraftonline.utils;

import java.util.List;
import java.util.Random;

public class TextFileProcessor {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int FUNCTION_NAME_LENGTH = 10;

    public static String replaceFunctionNames(String code, List<String> variableNames) {
        for (String variableName : variableNames) {
            String generatedVariableName = generateRandomString(FUNCTION_NAME_LENGTH);
            code = code.replace(variableName, generatedVariableName);
        }
        return code;
    }

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder randomString = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A'); // 生成随机大写字母
            randomString.append(randomChar);
        }
        return randomString.toString();
    }

    public static String convertToHexStringWithoutPrefix(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static String convertToHexStringWithPrefix(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("0x%02X, ", b));
        }

        // 移除最后一个逗号和空格
        hexString.deleteCharAt(hexString.length() - 2);
        return hexString.toString();
    }

    public static byte[] convertHexStringToByteArray(String hexString) {
        String[] hexValues = hexString.split(",\\s+"); // 按逗号和空格分割字符串
        byte[] byteArray = new byte[hexValues.length];
        for (int i = 0; i < hexValues.length; i++) {
            String hexValue = hexValues[i].trim().substring(2); // 去除前导的 "0x" 或 "0X"
            int decimalValue = Integer.parseInt(hexValue, 16); // 将十六进制值转换为整数
            byteArray[i] = (byte) decimalValue;
        }
        return byteArray;
    }

    public static int countCommas(String text) {
        int count = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ',') {
                count++;
            }
        }

        return count;
    }
}
