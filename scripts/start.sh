#!/bin/bash
dirpath="$(cd "$(dirname "$0")" && pwd)"
cd ${dirpath}

function run_console() {
    if [ "$(uname)" == "Darwin" ]; then
        # Mac
        java -cp 'apps/*:lib/*:conf' com.webank.wecross.console.Shell
    elif [ "$(uname -s | grep MINGW | wc -l)" != "0" ]; then
        # Windows
        java -cp 'apps/*;lib/*;conf' com.webank.wecross.console.Shell
    else
        # GNU/Linux
        java -cp 'apps/*:lib/*:conf' -Djava.security.egd=file:/dev/./urandom com.webank.wecross.console.Shell
    fi
}

create_jvm_security
run_console
