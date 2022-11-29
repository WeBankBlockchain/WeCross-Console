// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.0 <0.8.20;
pragma experimental ABIEncoderV2;

contract Evidence {
    mapping(string => string) infos;

    function newEvidence(string memory id, string memory evidenceInfo)
        public
        returns (bool)
    {
        infos[id] = evidenceInfo;
        return true;
    }

    function newEvidence_revert(string memory id, string memory evidenceInfo)
        public
        returns (bool)
    {
        delete infos[id];
        return true;
    }

    function queryEvidence(string memory id)
        public
        view
        returns (string memory)
    {
        return infos[id];
    }

    function queryEvidence_revert(string memory id)
        public
        view
        returns (string memory)
    {
        return infos[id];
    }
}