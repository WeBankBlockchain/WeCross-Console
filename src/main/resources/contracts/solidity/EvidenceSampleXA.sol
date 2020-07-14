pragma solidity >=0.5.0 <0.6.0;
pragma experimental ABIEncoderV2;

contract Evidence {
    struct EvidenceInfo {
        string id;
        string name;
        string content;
        bytes32[] sigs;
    }
    
    mapping(string => EvidenceInfo) infos;
    
    function() external {
        
    }
    
    function newEvidence(EvidenceInfo memory evidenceInfo) public returns(bool) {
        infos[evidenceInfo.id] = evidenceInfo;
    }
    
    function newEvidence_revert(EvidenceInfo memory evidenceInfo) public returns(bool) {
        delete infos[evidenceInfo.id];
    }
    
    function queryEvidence(string memory id) public view returns(EvidenceInfo memory evidenceInfo) {
        return infos[id];
    }
}