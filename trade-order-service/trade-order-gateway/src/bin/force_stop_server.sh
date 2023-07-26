#!/bin/bash

HERE=$(dirname "$0")
BASE=$(dirname "$HERE")

source "$HERE/common.sh"

PID=$(jps -l | grep "${MAIN_CLASS}"| awk '{print $1}')

if [ -n ${PID} ]; then
    echo kill -9 ${PID}
    kill -9 ${PID}
fi
