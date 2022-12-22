pragma solidity >=0.4.22 <0.6.0;

contract Asset {
    mapping(string => uint256) ledger;

    string constant SUCCESS_FLAG = "Success";
    string constant FAIL_FLAG = "Fail";

    constructor() public {
        ledger["Alice"] = 1000;
        ledger["Oscar"] = 100;
    }

    function transfer(string memory _sender, string memory _receiver, uint256 _amount) public
    returns (string memory)
    {
        uint256 senderBalance = ledger[_sender];
        if(senderBalance < _amount) {
            revert(FAIL_FLAG);
        }

        uint256 receiverBalance = ledger[_receiver];
        if((receiverBalance + _amount) < receiverBalance){
            revert(FAIL_FLAG);
        }

        ledger[_sender] = senderBalance - _amount;
        ledger[_receiver] = receiverBalance + _amount;
        return SUCCESS_FLAG;
    }

    function transfer_revert(string memory _sender, string memory _receiver, uint256 _amount) public
    returns (string memory)
    {
        return transfer(_receiver, _sender, _amount);
    }

    function balanceOf(string memory _account) public view
    returns (uint256)
    {
        return ledger[_account];
    }

    function balanceOf_revert(string memory _account) public view
    returns (uint256)
    {
        return ledger[_account];
    }
}