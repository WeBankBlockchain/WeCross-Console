#!/bin/bash

output_dir=console

LOG_INFO()
{
    local content=${1}
    echo -e "\033[32m[INFO] ${content}\033[0m"
}

LOG_ERROR()
{
    local content=${1}
    echo -e "\033[31m[ERROR] ${content}\033[0m"
}

check_dir()
{
    if [ -d ${output_dir} ]; then
        LOG_ERROR "The dir \"${output_dir}/\" exists. Please remove the dir."
        exit 1
    fi
}

build_project()
{
    git clone https://github.com/WeBankFinTech/WeCross-Console.git
    cd WeCross-Console
    ./gradlew assemble 2>&1 | tee output.log
    # shellcheck disable=SC2046
    # shellcheck disable=SC2006
    if [ `grep -c "BUILD SUCCESSFUL" output.log` -eq '0' ]; then
        LOG_ERROR "Build Wecross project failed"
        LOG_INFO "See output.log for details"
        mv output.log ../output.log
        exit 1
    fi
    echo "================================================================"
    cp -r dist/* ../
    cd ..
    rm -rf WeCross-Console
    cp conf/console-sample.xml conf/console.xml
    LOG_INFO "Build WeCross console successfully"
}

main()
{
    check_dir
    mkdir ${output_dir} && cd ${output_dir}
    build_project
}

main