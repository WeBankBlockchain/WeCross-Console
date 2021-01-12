#!/bin/bash

SECURIY_FILE='./.wecross.security'

create_jvm_security()
{
  if [[ ! -f ${SECURIY_FILE} ]];then
    echo "jdk.disabled.namedCurves = " > ${SECURIY_FILE}
    # LOG_INFO "create new file ${SECURIY_FILE}"
  fi
}

function run_console() {
    if [ "$(uname)" == "Darwin" ]; then
        # Mac
        java -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:lib/*:conf' com.webank.wecross.console.Shell
    elif [ "$(uname -s | grep MINGW | wc -l)" != "0" ]; then
        # Windows
        java -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*;lib/*;conf' com.webank.wecross.console.Shell
    else
        # GNU/Linux
        java -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:lib/*:conf' -Djava.security.egd=file:/dev/./urandom com.webank.wecross.console.Shell
    fi
}

create_jvm_security
run_console
