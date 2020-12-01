package main

import (
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

const (
	SuccessFlag = "Success"
	FailFlag    = "Fail"
	AccountKey  = "Account-%s"
)

type Asset struct {
}

func (a *Asset) Init(stub shim.ChaincodeStubInterface) peer.Response {
	setBalance(stub, "Alice", 1000)
	setBalance(stub, "Oscar", 100)
	return shim.Success(nil)
}

func (a *Asset) Invoke(stub shim.ChaincodeStubInterface) (res peer.Response) {
	fn, args := stub.GetFunctionAndParameters()
	switch fn {
	case "transfer":
		res = a.transfer(stub, args)
	case "transfer_revert":
		res = a.transfer_revert(stub, args)
	case "balanceOf":
		res = a.balanceOf(stub, args)
	case "balanceOf_revert":
		res = a.balanceOf_revert(stub, args)
	default:
		return shim.Error("invalid function name")
	}
	// Return the result as success payload
	return res
}

// sender, receiver, amount
func (a *Asset) transfer(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 3 {
		return shim.Error("Invalid args length, should be: [sender, receiver, amount]")
	}
	amount := stringToUint64(args[2])
	senderBalance := getBalance(stub, args[0])
	if senderBalance < amount {
		return shim.Error(FailFlag)
	}

	receiverBalance := getBalance(stub, args[1])
	if (receiverBalance + amount) < receiverBalance {
		return shim.Error(FailFlag)
	}

	setBalance(stub, args[0], senderBalance-amount)
	setBalance(stub, args[1], receiverBalance+amount)
	return shim.Success([]byte(SuccessFlag))
}

func (a *Asset) transfer_revert(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	newArgs := []string{args[1], args[0], args[2]}
	return a.transfer(stub, newArgs)
}

func (a *Asset) balanceOf(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Invalid args length, should be: [account]")
	}
	return shim.Success(uint64ToBytes(getBalance(stub, args[0])))
}

func (a *Asset) balanceOf_revert(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("Invalid args length, should be: [account]")
	}
	return shim.Success(uint64ToBytes(getBalance(stub, args[0])))
}

func getBalance(stub shim.ChaincodeStubInterface, account string) uint64 {
	res, err := stub.GetState(getAccountKey(account))
	checkError(err)
	if res == nil {
		return 0
	} else {
		return bytesToUint64(res)
	}
}

func setBalance(stub shim.ChaincodeStubInterface, account string, amount uint64) {
	err := stub.PutState(getAccountKey(account), uint64ToBytes(amount))
	checkError(err)
}

func getAccountKey(account string) string {
	return fmt.Sprintf(AccountKey, account)
}

func bytesToUint64(bts []byte) uint64 {
	u, err := strconv.ParseUint(string(bts), 10, 64)
	checkError(err)
	return u
}

func uint64ToBytes(u uint64) []byte {
	return []byte(uint64ToString(u))
}

func uint64ToString(u uint64) string {
	return strconv.FormatUint(u, 10)
}

func stringToUint64(str string) uint64 {
	i, e := strconv.Atoi(str)
	if e != nil {
		return 0
	}
	return uint64(i)
}

func checkError(err error) {
	if err != nil {
		panic(err)
	}
}

func main() {
	if err := shim.Start(new(Asset)); err != nil {
		fmt.Printf("Error starting Asset chaincode: %s", err)
	}
}
