pragma solidity >=0.4.22 <0.6.0;
pragma experimental ABIEncoderV2;

import "./WeCrossHub.sol";


contract InterchainGetBlockSample {
    WeCrossHub hub;

    string[] blockStr = [""];

    function init(address _hub) public
    {
        hub = WeCrossHub(_hub);
    }

    function interchainGetBlock(string memory _path, string memory _method, string memory _args, string memory _callbackPath, string memory _callbackMethod) public
    returns(string memory)
    {
        string[] memory args = new string[](1);
        args[0] = _args;

        return hub.interchainGetBlock(_path, _method, args, _callbackPath, _callbackMethod);
    }

    function callback(bool state, string[] memory _result) public
    returns(string[] memory)
    {
        if(state) {
            blockStr = _result;
        }

        return _result;
    }

    function get() public view
    returns(string[] memory)
    {
        return blockStr;
    }

    function set(string[] memory _data) public
    returns(string[] memory)
    {
        blockStr = _data;
        return blockStr;
    }

}