package main

import (
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

const NAME = "name"

type HelloWorld struct {
}

func (hw *HelloWorld) Init(stub shim.ChaincodeStubInterface) peer.Response {
	// init the variable name with "Hello, World!"
	err := stub.PutState(NAME, []byte("Hello, World!"))
	if err != nil {
		return shim.Error("fail in initializing smart contract HelloWorld")
	}
	return shim.Success(nil)
}

// Invoke is called per transaction on the chaincode. Each transaction is
// either a 'get' or a 'set'.

func (hw *HelloWorld) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	// Extract the function and args from the transaction proposal
	fn, args := stub.GetFunctionAndParameters()

	var result string
	var err error
	if fn == "set" {
		result, err = set(stub, args)
	} else if fn == "get" { // assume 'get' even if fn is nil
		result, err = get(stub)
	} else {
		err = fmt.Errorf("invalid call")
	}

	if err != nil {
		return shim.Error(err.Error())
	}

	// Return the result as success payload
	return shim.Success([]byte(result))
}

// Set updates the variable name with a new value. If succeed, the updated value is back.
func set(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("no value is input")
	}

	err := stub.PutState(NAME, []byte(args[0]))
	if err != nil {
		return "", fmt.Errorf("failed in set: %s", err)
	}
	return args[0], nil
}

// Get returns the value of the variable name
func get(stub shim.ChaincodeStubInterface) (string, error) {
	value, err := stub.GetState(NAME)
	if err != nil {
		return "", fmt.Errorf("failed in get: %s", err)
	}
	if value == nil {
		return "", fmt.Errorf("variable name does not exist")
	}
	return string(value), nil
}

// main function starts up the chaincode in the container during instantiate
func main() {
	if err := shim.Start(new(HelloWorld)); err != nil {
		fmt.Printf("Error starting HelloWrold chaincode: %s", err)
	}
}
