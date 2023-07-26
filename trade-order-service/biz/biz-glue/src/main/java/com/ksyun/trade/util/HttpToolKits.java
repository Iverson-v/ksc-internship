package com.ksyun.trade.util;

import com.ksyun.common.util.base.ExceptionUtil;
import com.ksyun.common.util.collection.CollectionUtil;
import com.ksyun.common.util.id.IdGeneratorUtils;
import com.ksyun.req.trace.RequestTraceContextSlf4jMDCHolder;
import com.ksyun.trade.bo.InnerHttpResp;
import com.ksyun.trade.constant.Constant;
import com.ksyun.trade.constant.LoggerName;
import jodd.http.HttpException;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author ksc
 */
public final class HttpToolKits {

    private static final Logger httpLogger = LoggerFactory.getLogger(LoggerName.HTTP_LOGGER_NAME);

    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 30 * 1000;

    public static Map<String, String> mewKscRequestId() {
        String xKscRequestId = RequestTraceContextSlf4jMDCHolder.getRequestId();
        if(StringUtils.isBlank(xKscRequestId)){
            xKscRequestId = IdGeneratorUtils.uuid();
        }
        return Collections.singletonMap(Constant.X_KSC_REQUEST_ID, xKscRequestId);
    }


    public static InnerHttpResp postRawJsonBody(String reqUrl, Map<String, String> headers, String jsonBody) {
        HttpRequest httpRequest = HttpRequest.post(reqUrl).bodyText(jsonBody, Constant.JSON);

        if (CollectionUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequest.header(entry.getKey(), entry.getValue());
            }
        }

        InnerHttpResp innerHttpResp = sendRequest(httpRequest, reqUrl);
        httpLogger.info("postRawJsonBody.reqUrl:{},headers:{},jsonBody:{},resp:{}", reqUrl, headers, jsonBody, innerHttpResp);
        return innerHttpResp;
    }

    private static InnerHttpResp sendRequest(HttpRequest httpRequest, String reqUrl) {
        HttpResponse response = null;
        try {
            httpRequest.connectionTimeout(DEFAULT_TIMEOUT_MILLISECONDS).timeout(DEFAULT_TIMEOUT_MILLISECONDS);
            response = httpRequest.send();
            return new InnerHttpResp(response.statusCode(), response.bodyText());
        } catch (HttpException e) {
            httpLogger.error("http call occur error. reqUrl:{} request:{}", reqUrl, httpRequest, e);
            int statusCode = (response != null) ? response.statusCode() : 500;
            String body = (response != null) ? response.bodyText() : "";
            String errorMsg = ExceptionUtil.getErrorMessageWithRootAndNestedException(e);
            return new InnerHttpResp(statusCode, body, errorMsg, e);
        }
    }


    public static InnerHttpResp get(String reqUrl, Map<String, String> headers, Map<String, String> queryParams) {
        HttpRequest httpRequest = HttpRequest.get(reqUrl);
        if (CollectionUtil.isNotEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequest.header(entry.getKey(), entry.getValue());
            }
        }

        if (CollectionUtil.isNotEmpty(queryParams)) {
            httpRequest.query(queryParams);
        }

        InnerHttpResp innerHttpResp = sendRequest(httpRequest, reqUrl);
        httpLogger.info("get. reqUrl:{},headers:{},queryParams:{},resp:{}", reqUrl, headers,queryParams, innerHttpResp);
        return innerHttpResp;
    }

    private HttpToolKits() {

    }
}
