package main

import (
	"encoding/json"
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

const (
	ChannelKey = "channel"
	HubNameKey = "hub_name"
	DataKey    = "data"
)

type Interchain struct {
}

func (i *Interchain) Init(stub shim.ChaincodeStubInterface) (res peer.Response) {
	defer func() {
		if r := recover(); r != nil {
			res = shim.Error(fmt.Sprintf("%v", r))
		}
	}()

	data := []string{"Talk is cheap, show me the code."}
	dataBytes, err := json.Marshal(data)
	checkError(err)

	err = stub.PutState(DataKey, dataBytes)
	checkError(err)

	return shim.Success(nil)
}

func (i *Interchain) Invoke(stub shim.ChaincodeStubInterface) (res peer.Response) {
	defer func() {
		if r := recover(); r != nil {
			res = shim.Error(fmt.Sprintf("%v", r))
		}
	}()

	fcn, args := stub.GetFunctionAndParameters()

	switch fcn {
	case "init":
		res = i.init(stub, args)
	case "interchain":
		res = i.interchain(stub, args)
	case "callback":
		res = i.callback(stub, args)
	case "get":
		res = i.get(stub)
	case "set":
		res = i.set(stub, args)
	default:
		res = shim.Error("invalid function name")
	}

	return
}

/*
 * @args channel || hub
 */
func (i *Interchain) init(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("incorrect number of arguments, expecting 2")
	}

	err := stub.PutState(ChannelKey, []byte(args[0]))
	checkError(err)

	err = stub.PutState(HubNameKey, []byte(args[1]))
	checkError(err)

	return shim.Success(nil)
}

/*
 * invoke other chain
 * @args path || method || args || callbackPath || callbackMethod
 */
func (i *Interchain) interchain(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 5 {
		return shim.Error("incorrect number of arguments, expecting 5")
	}

	channel, err := stub.GetState(ChannelKey)
	checkError(err)

	hub, err := stub.GetState(HubNameKey)
	checkError(err)

	var trans [][]byte
	trans = append(trans, []byte("interchainInvoke"))
	trans = append(trans, []byte(args[0]))
	trans = append(trans, []byte(args[1]))

	input := []string{args[2]}
	inputData, err := json.Marshal(input)
	checkError(err)
	trans = append(trans, inputData)

	trans = append(trans, []byte(args[3]))
	trans = append(trans, []byte(args[4]))

	return stub.InvokeChaincode(string(hub), trans, string(channel))
}

/*
 * @args state || result
 * result is json form of string array
 */
func (i *Interchain) callback(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("incorrect number of arguments, expecting 2")
	}

	if "true" == args[0] {
		err := stub.PutState(DataKey, []byte(args[1]))
		checkError(err)
	}
	return shim.Success([]byte(args[1]))
}

func (i *Interchain) get(stub shim.ChaincodeStubInterface) peer.Response {
	data, err := stub.GetState(DataKey)
	checkError(err)
	return shim.Success(data)
}

func (i *Interchain) set(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 1 {
		return shim.Error("incorrect number of arguments, expecting 1")
	}

	err := stub.PutState(DataKey, []byte(args[0]))
	checkError(err)

	return shim.Success([]byte(args[0]))
}

func checkError(err error) {
	if err != nil {
		panic(err)
	}
}

func main() {
	err := shim.Start(new(Interchain))
	if err != nil {
		fmt.Printf("Error: %s", err)
	}
}
