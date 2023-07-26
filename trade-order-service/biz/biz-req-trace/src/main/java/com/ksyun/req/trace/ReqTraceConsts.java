package com.ksyun.req.trace;

/**
 * @author ksc
 */
public final class ReqTraceConsts {

	public static final String TRACE_KEY = "traceId";//这里修改了之前为TT，改成了traceId，因为在日志中在MDC中取的是traceId，不是TT。

	public static final String REQUEST_ID = "X-KSY-REQUEST-ID";

	private ReqTraceConsts(){

	}
}