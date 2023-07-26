#!/bin/bash

MAIN_CLASS=com.ksyun.trade.GateWayApplication
JAVA_HOME=/usr/local/java
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME PATH


echo "JAVA_HOME:"$JAVA_HOME
echo "PATH:"$PATH

function usage() {
    echo "usage:"
    echo "-h --help"
    echo "-e --env=local, uat, or prod"
    echo ""
}

while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`
    case ${PARAM} in
        -h | --help)
            usage
            exit
            ;;
        -e | --env)
            ENVIRONMENT=${VALUE}
            ;;
        *)
            BYPASS_PARAMS="$BYPASS_PARAMS $PARAM"
	    ;;
    esac
    shift
done


