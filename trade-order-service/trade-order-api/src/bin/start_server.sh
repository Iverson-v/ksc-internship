#!/bin/bash

HERE=$(cd $(dirname "$0"); pwd)
BASE=$(dirname "$HERE")
PROJECT_NAME=$(basename "$BASE")
LOG_BASE_DIR=$(dirname $(dirname "$BASE"))
LOG_BASE="$LOG_BASE_DIR/logs/$PROJECT_NAME"

source "$HERE/common.sh"


echo "HERE:"$HERE
echo "BASE:"$BASE
echo "PROJECT_NAME:"$PROJECT_NAME
echo "LOG_BASE_DIR:"$LOG_BASE_DIR
echo "LOG_BASE:"$LOG_BASE

nowday=`date +%Y%m%d`
GC_LOG_DIR=${LOG_BASE}/gclogs
test -d ${GC_LOG_DIR} || mkdir -p ${GC_LOG_DIR}

cd ${BASE}

#for f in $(find ${BASE}/lib -type f -name "*.jar") $(find ${BASE}/lib -type f -name "*.zip"); do
#  CLASS_PATH=${CLASS_PATH}:${f}
#done
CLASS_PATH=${BASE}/lib/*:${CLASS_PATH}

echo "ENVIRONMENT:"$ENVIRONMENT

CLASS_PATH=${BASE}/resources/:${BASE}/conf/${ENVIRONMENT}:${CLASS_PATH}

echo "CLASS_PATH:"$CLASS_PATH

if [ "${ENVIRONMENT}" = "prod" ]; then
    HEAP_OPTS="-Xmx2g -Xms2g -Xmn1g -Xss256k"
else
    HEAP_OPTS="-Xmx256m -Xms256m"
fi

PERM_OPTS="-XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+AlwaysPreTouch -XX:-UseBiasedLocking -XX:AutoBoxCacheMax=10000 -XX:+ParallelRefProcEnabled -Djava.security.egd=file:/dev/./urandom -Djava.net.preferIPv4Stack=true"

GC_OPTS="-XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:MaxTenuringThreshold=6 -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSWaitDuration=10000 -XX:+ExplicitGCInvokesConcurrent -Xloggc:${GC_LOG_DIR}/gc.log.${nowday} -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails"

MISC_OPTS="-Dlog.home=${LOG_BASE}"

ERROR_OPTS="-XX:ErrorFile=${LOG_BASE}/hs_err_%p.log -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_BASE}/"

JAVA_OPTS="${JAVA_OPTS} ${HEAP_OPTS} ${PERM_OPTS} ${GC_OPTS} ${MISC_OPTS} ${ERROR_OPTS} ${DEBUG_OPTS}"

nohup java ${JAVA_OPTS} -classpath ${CLASS_PATH} ${MAIN_CLASS} > ${LOG_BASE}/console.log 2>&1 &

echo $! > ${LOG_BASE}/app.pid
