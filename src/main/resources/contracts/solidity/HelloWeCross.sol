// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.4.24 <0.8.20;
pragma experimental ABIEncoderV2;

contract HelloWeCross {
    string[] ss;

    function set(string[] memory _ss) public returns (string[] memory) {
        ss = _ss;
        return ss;
    }

    function get() public view returns (string[] memory) {
        return ss;
    }
}
