#!/bin/bash
dirpath="$(cd "$(dirname "$0")" && pwd)"
cd ${dirpath}
export LANG='zh_CN.utf8'

APPS_FOLDER=$(pwd)/apps
SECURIY_FILE='./.wecross.security'

LOG_INFO() {
    echo -e "\033[32m$@\033[0m"
}

LOG_ERROR() {
    echo -e "\033[31m$@\033[0m"
}

create_jvm_security() {
  if [[ ! -f ${SECURIY_FILE} ]];then
    echo "jdk.disabled.namedCurves = " > ${SECURIY_FILE}
    # LOG_INFO "create new file ${SECURIY_FILE}"
  fi
}

show_version() {
  LOG_INFO "WeCross-Console version: [" $(ls ${APPS_FOLDER} |awk '{gsub(/.jar$/,""); print}') "]"
}

run_console() {
    if [ "$(uname)" == "Darwin" ]; then
        # Mac
        java -Dfile.encoding=UTF-8 -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1,x25519,secp256r1,secp384r1,secp521r1,x448,ffdhe2048,ffdhe3072,ffdhe4096,ffdhe6144,ffdhe8192" -cp 'apps/*:lib/*:conf' com.webank.wecross.console.Shell
    elif [ "$(uname -s | grep MINGW | wc -l)" != "0" ]; then
        # Windows
        java -Dfile.encoding=UTF-8 -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1,x25519,secp256r1,secp384r1,secp521r1,x448,ffdhe2048,ffdhe3072,ffdhe4096,ffdhe6144,ffdhe8192" -cp 'apps/*;lib/*;conf' com.webank.wecross.console.Shell
    else
        # GNU/Linux
        java -Dfile.encoding=UTF-8 -Djava.security.properties=${SECURIY_FILE} -Djdk.sunec.disableNative="false" -Djdk.tls.namedGroups="secp256k1,x25519,secp256r1,secp384r1,secp521r1,x448,ffdhe2048,ffdhe3072,ffdhe4096,ffdhe6144,ffdhe8192" -cp 'apps/*:lib/*:conf' -Djava.security.egd=file:/dev/./urandom com.webank.wecross.console.Shell
    fi
}

create_jvm_security
show_version
run_console

