pragma solidity >=0.4.22 <0.6.0;
pragma experimental ABIEncoderV2;

import "./WeCrossHub.sol";


contract InterchainSample {
    WeCrossHub hub;

    string[] data = ["Talk is cheap, show me the code."];

    function init(address _hub) public
    {
        hub = WeCrossHub(_hub);
    }

    function interchain(string memory _path, string memory _method, string memory _args, string memory _callbackPath, string memory _callbackMethod) public
    returns(string memory)
    {
        string[] memory args = new string[](1);
        args[0] = _args;

        return hub.interchainInvoke(_path, _method, args, _callbackPath, _callbackMethod);
    }

    function callback(bool state, string[] memory _result) public
    returns(string[] memory)
    {
        if(state) {
            data = _result;
        }

        return _result;
    }

    function get() public view
    returns(string[] memory)
    {
        return data;
    }

    function set(string[] memory _data) public
    returns(string[] memory)
    {
        data = _data;
        return data;
    }

}
