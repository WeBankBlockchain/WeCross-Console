// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.4.0 <0.8.20;

contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
