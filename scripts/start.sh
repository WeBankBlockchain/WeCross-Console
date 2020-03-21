#!/bin/bash

function run_console() {
    if [ "$(uname)" == "Darwin" ]; then
        # Mac
        java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:lib/*:conf' com.webank.wecross.console.Shell
    elif [ "$(uname -s | grep MINGW | wc -l)" != "0" ]; then
        # Windows
        java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*;lib/*:conf' com.webank.wecross.console.Shell
    else
        # GNU/Linux
        java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:lib/*:conf' com.webank.wecross.console.Shell
    fi
}

run_console
