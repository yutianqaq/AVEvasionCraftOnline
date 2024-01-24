package com.yutian4060.avevasioncraftonline.service;

import com.yutian4060.avevasioncraftonline.dto.CompilationResponseDTO;
import com.yutian4060.avevasioncraftonline.dto.ShellcodeUploadDTO;

import java.io.IOException;

public interface CompileService {

    CompilationResponseDTO compileCodeC(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException;
    CompilationResponseDTO compileCodeNim(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException;
    CompilationResponseDTO compileCodeGo(ShellcodeUploadDTO shellcodeUploadDTO) throws IOException;
}
