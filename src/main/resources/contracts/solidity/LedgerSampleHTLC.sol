pragma solidity >=0.4.22 <0.6.0;

import "./LedgerSample.sol";
import "./HTLC.sol";

contract LedgerSampleHTLC is HTLC, LedgerSampleHolder {

    // asset contract address
    address assetContract;

    /*
        @param: assetContract
    */
    function init(string memory _assetContract) public
    returns (string memory result)
    {
        assetContract = stringToAddress(_assetContract);
        result = successFlag;
    }

    /*
        @param: hash
    */
    function lock(string memory _hash) public
    returns (string memory result)
    {
        result = super.lock(_hash);
        if(sameString(result, "done")) {
            result = successFlag;
            return result;
        } else if (!sameString(result, "continue")) {
            return result;
        }

        address sender = getSender(_hash);
        uint amount = getAmount(_hash);
        if (LedgerSample(assetContract).allowance(sender, address(this)) < uint(amount)) {
            result = "insufficient authorized assets";
            return result;
        }

        // This htlc contract becomes the temporary owner of the assets
        LedgerSample(assetContract).sendFrom(sender, address(this), uint(amount),"");

        setLockState(_hash);
        result = successFlag;
    }

    /*
        @param: hash | secret
    */
    function unlock(string memory _hash, string memory _secret) public
    returns (string memory result)
    {
        result = super.unlock(_hash, _secret);
        if(sameString(result, "done")) {
            result = successFlag;
            return result;
        } else if (!sameString(result, "continue")) {
            return result;
        }

        // transfer from htlc contract to receiver
        address receiver = getReceiver(_hash);
        uint amount = getAmount(_hash);
        LedgerSample(assetContract).send(receiver, uint(amount),"");

        setUnlockState(_hash);
        setSecret(_hash, _secret);
        result = successFlag;
    }

    /*
        @param: hash
    */
    function rollback(string memory _hash) public
    returns (string memory result)
    {
        result = super.rollback(_hash);
        if(sameString(result, "done")) {
            result = successFlag;
            return result;
        } else if (!sameString(result, "continue")) {
            return result;
        }

        // transfer from htlc contract to sender
        address sender = getSender(_hash);
        uint amount = getAmount(_hash);
        LedgerSample(assetContract).send(sender, uint(amount),"");

        setRollbackState(_hash);
        result = successFlag;
    }

    /*
        @param: address
    */
    function balanceOf(string memory account) public view
    returns(uint256)
    {
        return LedgerSample(assetContract).balance(stringToAddress(account));
    }

    function queryAddress() public view
    returns(address)
    {
        return tx.origin;
    }
}