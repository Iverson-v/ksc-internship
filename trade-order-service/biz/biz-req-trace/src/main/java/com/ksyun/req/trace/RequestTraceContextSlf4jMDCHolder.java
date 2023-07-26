package com.ksyun.req.trace;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ksc
 */
public class RequestTraceContextSlf4jMDCHolder {

    public static String getRequestId() {
        String currThreadLocalReqIdStr = MDC.get(ReqTraceConsts.TRACE_KEY);
        return StringUtils.isBlank(currThreadLocalReqIdStr) ? StringUtils.EMPTY : currThreadLocalReqIdStr;
    }

    /**
     * 封装http请求头信息，用于消息ID跟踪传递
     */
    public static Map<String, String> getTraceHeaders() {
        Map<String, String> traceMap = new HashMap<>();
        String requestId = getRequestId();
        if (StringUtils.isNotBlank(requestId)) {
            traceMap.put(ReqTraceConsts.REQUEST_ID, requestId);
        }
        return traceMap;
    }

}
