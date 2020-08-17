pragma solidity >=0.5.0 <0.6.0;
pragma experimental ABIEncoderV2;

contract Evidence {
    
    mapping(string => string) infos;
    
    function() external {
        
    }
    
    function newEvidence(string memory id, string memory evidenceInfo) public returns(bool) {
        infos[id] = evidenceInfo;
        return true;
    }
    
    function newEvidence_revert(string memory id, string memory evidenceInfo) public returns(bool) {
        delete infos[id];
        return true;
    }
    
    function queryEvidence(string memory id) public view returns(string memory) {
        return infos[id];
    }
}