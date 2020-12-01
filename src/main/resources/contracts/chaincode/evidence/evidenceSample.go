package main

import (
	"fmt"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

type Evidence struct {
}

func (t *Evidence) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}


func (t *Evidence) Invoke(stub shim.ChaincodeStubInterface)  (res peer.Response) {
	fn, args := stub.GetFunctionAndParameters()
	switch fn {
		case "newEvidence":
			res = t.newEvidence(stub, args) 
		case "newEvidence_revert":
			res = t.newEvidence_revert(stub, args) 
		case "queryEvidence":
			res = t.queryEvidence(stub, args)
		case "queryEvidence_revert":
			res = t.queryEvidence_revert(stub, args)
		default:
			return shim.Error("invalid function name")
	}
	// Return the result as success payload
	return res
}

// id, evidenceInfo
func (t *Evidence) newEvidence(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Invalid args length, should be: [id, evidenceInfo]")
	}

	err := stub.PutState(args[0], []byte(args[1]))
	if err != nil {
		return shim.Error(fmt.Errorf("Failed to new evidence: %s", args[0]).Error())
	}
	return shim.Success([]byte("Success"))
}

// id, evidenceInfo
func (t *Evidence) newEvidence_revert(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Invalid args length, should be: [id, evidenceInfo]")
	}

	err := stub.PutState(args[0], []byte(""))
	if err != nil {
		return shim.Error(fmt.Errorf("Failed to revert evidence: %s", args[0]).Error())
	}
	return shim.Success([]byte("Success"))
}

// id
func (t *Evidence) queryEvidence(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error(fmt.Errorf("Invalid args length. should be [id]").Error())
	}

	value, err := stub.GetState(args[0])
	if err != nil {
		return shim.Error(fmt.Errorf("Failed to query evidence: %s with error: %s", args[0], err).Error())
	}

	return shim.Success([]byte(value)) 
}

func (t *Evidence) queryEvidence_revert(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	return shim.Success([]byte("Success"))
}

func main() {
	if err := shim.Start(new(Evidence)); err != nil {
		fmt.Printf("Error starting Evidence chaincode: %s", err)
	}
}
