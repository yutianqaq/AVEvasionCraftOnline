package com.yutian4060.avevasioncraftonline.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;



public class ShellcodeProcessor {

    private static final int TEMPLATE_XOR_LEN = 10;
    private static byte[] key;

    public static String transformation(byte[] shellcode, String transformationMethod, Path outputFilename) throws IOException {
        key = generateRandomKey();
        String result = switch (transformationMethod) {
            case "base64Xor" -> base64XorEncrypt(shellcode);
            case "xor" -> xorEncryptAndConvertToHexString(shellcode);
            case "none" -> TextFileProcessor.convertToHexStringWithPrefix(shellcode);
            default -> throw new IllegalStateException("Unexpected value: " + transformationMethod);
        };

        assert result != null;
        Path of = Path.of(outputFilename + ".bin");
        if (transformationMethod.equals("base64Xor")) {
            FileUtils.saveFileBytes(of, result.getBytes());
        } else {
            FileUtils.saveFileBytes(of, TextFileProcessor.convertHexStringToByteArray(result));
        }
        return result;
    }
    public static String getKey() {
        return TextFileProcessor.convertToHexStringWithPrefix(key);
    }
    private static String base64XorEncrypt(byte[] shellcode) {
        byte[] encryptedBytes = xorEncrypt(shellcode);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String xorEncryptAndConvertToHexString(byte[] shellcode) {
        byte[] encryptedBytes = xorEncrypt(shellcode);
        return TextFileProcessor.convertToHexStringWithPrefix(encryptedBytes);
    }

    private static byte[] xorEncrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        int keyLength = key.length;

        for (int i = 0; i < plaintext.length; i++) {
            byte keyByte = key[i % keyLength];
            byte encryptedByte = (byte) (plaintext[i] ^ keyByte);
            ciphertext[i] = encryptedByte;
        }

        return ciphertext;
    }

    private static byte[] generateRandomKey() {
        return TextFileProcessor.generateRandomString(TEMPLATE_XOR_LEN).getBytes();
    }

    private static String generateRandomFilename() {
        return TextFileProcessor.generateRandomString(TEMPLATE_XOR_LEN);
    }
//
//    public static String noneProcess(String shellcode, String templateCode) {
//
//        return templateCode.replace(TEMPLATE_LEN_PLACEHOLDER, String.valueOf(countCommas(shellcode) + 1))
//                .replace(TEMPLATE_SHELLCODE_PLACEHOLDER, shellcode);
//    }

}
