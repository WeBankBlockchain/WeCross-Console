pragma solidity >=0.4.0 <0.6.0;

contract SimpleAsset {
    mapping(string => string) ledger;
    constructor(string memory key,string memory value) public{
        ledger[key] = value;
    }

    function set(string memory key,string memory value) public returns (string memory){
        ledger[key] = value;
        return value;
    }

    function get(string memory key) public view returns(string memory){
        return ledger[key];
    }
}