pragma solidity >=0.4.24 <0.6.0;
pragma experimental ABIEncoderV2;

contract ContractTypeTest
{
    uint256 u;
    bool b;
    address addr;
    bytes32 bs32;
    string s;
    bytes bs;
    
    function setValue(uint256 _u, bool _b, address _addr, bytes32 _bs32, string memory _s, bytes memory _bs) public {
        u = _u;
        b = _b;
        addr = _addr;
        bs32 = _bs32;
        s = _s;
        bs = _bs;
    }
    
    function getValue() view public returns(uint256 _u, bool _b, address _addr, bytes32 _bs32, string memory _s, bytes memory _bs) {
        _u = u;
        _b = b;
        _addr = addr;
        _bs32 = bs32;
        _s = s;
        _bs = bs;
    }

    uint256[] u_dynamic;
    bool[] b_dynamic;
    address[] addr_dynamic;
    bytes32[] bs32_dynamic;
    string[] s_dynamic;
    bytes[] bs_dynamic;
    
    function setDynamicValue(uint256[] memory _u, bool[] memory _b, address[] memory _addr, bytes32[] memory _bs32, string[] memory _s, bytes[] memory _bs) public {
        u_dynamic = _u;
        b_dynamic= _b;
        addr_dynamic = _addr;
        bs32_dynamic = _bs32;
        s_dynamic = _s;
        bs_dynamic = _bs;
    }
    
    function getDynamicValue() view public returns(uint256[] memory _u, bool[] memory _b, address[] memory _addr, bytes32[] memory _bs32, string[] memory _s, bytes[] memory _bs) {
        _u = u_dynamic;
        _b = b_dynamic;
        _addr = addr_dynamic;
        _bs32 = bs32_dynamic;
        _s = s_dynamic;
        _bs = bs_dynamic;
    }
    
    uint256[3] u_fixed;
    bool[3] b_fixed;
    address[3] addr_fixed;
    bytes32[3] bs32_fixed;
    string[3] s_fixed;
    bytes[3] bs_fixed;
    
    function setFixedValue(uint256[3] memory _u, bool[3] memory _b, address[3] memory _addr, bytes32[3] memory _bs32, string[3] memory _s, bytes[3] memory _bs) public {
        u_fixed = _u;
        b_fixed = _b;
        addr_fixed = _addr;
        bs32_fixed = _bs32;
        s_fixed = _s;
        bs_fixed = _bs;
    }
    
    function getFixedValue() view public returns(uint256[3] memory _u, bool[3] memory _b, address[3] memory _addr, bytes32[3] memory _bs32, string[3] memory _s, bytes[3] memory _bs) {
         _u = u_fixed;
        _b = b_fixed;
        _addr = addr_fixed;
        _bs32 = bs32_fixed;
        _s = s_fixed;
        _bs = bs_fixed;
    }
}
