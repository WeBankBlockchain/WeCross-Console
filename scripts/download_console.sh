#!/bin/bash
set -e

compatibility_version=
enable_build_from_resource=0

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

help()
{
    echo "$1"
    cat << EOF
Usage:
    -s                              [Optional] Get wecross console by: gradle build from github Source Code.
    -h  call for help
e.g
    bash $0 
    bash $0 -s 
EOF
exit 0
}


parse_command()
{
while getopts "sh" option;do
    # shellcheck disable=SC2220
    case ${option} in
    s)
        enable_build_from_resource=1
    ;;
    h)  help;;
    esac
done

}

download_wecross_console_pkg()
{
    if [ -d WeCross-Console/apps ];then
        LOG_INFO "./WeCross-Console/ exists"
        exit 0
    fi

    LOG_INFO "Checking latest release"
    if [ -z "${compatibility_version}" ];then
        compatibility_version=$(curl -s https://api.github.com/repos/WeBankFinTech/WeCross-Console/releases | grep "tag_name"|awk -F '\"' '{print $4}')
    fi

    latest_wecross_console=WeCross-Console.tar.gz
    latest_wecross_console_checksum_file=WeCross-Console.tar.gz.md5
    LOG_INFO "Latest release: ${compatibility_version}"


    # in case network is broken
    #if [ -z "${compatibility_version}" ];then
    #    compatibility_version="${default_version}"
    #fi
    curl -LO https://github.com/WeBankFinTech/WeCross-Console/releases/download/${compatibility_version}/${latest_wecross_console_checksum_file}

    if [ ! -e ${latest_wecross_console} ] || [ -z "$(md5sum -c ${latest_wecross_console_checksum_file}|grep OK)" ];then
        LOG_INFO "Download from: https://github.com/WeBankFinTech/WeCross-Console/releases/download/${compatibility_version}/${latest_wecross_console}"
        curl -C - -LO https://github.com/WeBankFinTech/WeCross-Console/releases/download/${compatibility_version}/${latest_wecross_console}


        if [ -z "$(md5sum -c ${latest_wecross_console_checksum_file}|grep OK)" ];then
            LOG_ERROR "Download WeCross Console package failed! URL: https://github.com/WeBankFinTech/WeCross-Console/releases/download/${compatibility_version}/${latest_wecross_console}"
            rm -f ${latest_wecross_console}
            exit 1
        fi

    else
        LOG_INFO "Latest release ${latest_wecross_console} exists."
    fi

    tar -zxf ${latest_wecross_console}
}

build_from_source()
{
    if [ -d WeCross-Console/apps ];then
        LOG_INFO "./WeCross-Console/ exists"
        exit 0
        return
    fi
    git clone https://github.com/WeBankFinTech/WeCross-Console.git
    cd WeCross-Console
    ./gradlew assemble 2>&1 | tee output.log
    # shellcheck disable=SC2046
    # shellcheck disable=SC2006
    if [ `grep -c "BUILD SUCCESSFUL" output.log` -eq '0' ]; then
        LOG_ERROR "Build Wecross Console project failed"
        LOG_INFO "See output.log for details"
        mv output.log ../output.log
        exit 1
    fi
    echo "================================================================"
    cd ..
    mv WeCross-Console WeCross-Console-Source
    mv WeCross-Console-Source/dist WeCross-Console
    rm -rf WeCross-Console-Source

    LOG_INFO "Build WeCross Console successfully"
}

main()
{
    if [ 1 -eq ${enable_build_from_resource} ];then
        build_from_source
    else
        download_wecross_console_pkg
    fi
}

print_result()
{
LOG_INFO "Download completed. WeCross Console is in: ./WeCross-Console/"
LOG_INFO "Please configure \"./WeCross-Console/conf/console.xml\" according with \"console-sample.xml\" "
}

parse_command $@
main
print_result