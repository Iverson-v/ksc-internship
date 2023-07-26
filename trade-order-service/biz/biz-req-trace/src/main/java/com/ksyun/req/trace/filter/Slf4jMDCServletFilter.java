package com.ksyun.req.trace.filter;

import com.ksyun.req.trace.ReqTraceConsts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author ksc
 */
public class Slf4jMDCServletFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(Slf4jMDCServletFilter.class);

    private static final List<String> FILTER_URLS = new ArrayList<>();

    @Override
    protected void initFilterBean() throws ServletException {
        String filterUrlStr = this.getFilterConfig().getInitParameter("filterUrl");
        if (StringUtils.isNotBlank(filterUrlStr)) {
            log.debug("发现配置过滤url参数:{}" , filterUrlStr);
            String[] urlArray = filterUrlStr.split(";");
            FILTER_URLS.addAll(Arrays.asList(urlArray));
        }
    }

    /**
     * 异步处理可能会丢失数据
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!FILTER_URLS.contains(request.getRequestURI())) {
            initContextHolders(request);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            resetContextHolders();
        }
    }

    /**
     * 获取或构造请求参数，默认是请求id和父类名称以及时间
     */
    private void initContextHolders(HttpServletRequest request) {
        String requestId = request.getHeader(ReqTraceConsts.REQUEST_ID);//获取请求头"X-KSY-REQUEST-ID"字段，最终放到MDC中
        if (StringUtils.isBlank(requestId)) {//如果请求头没有"X-KSY-REQUEST-ID"就自己生成。作业中会提供一个"X-KSY-REQUEST-ID"，所有这里不执行。
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(ReqTraceConsts.TRACE_KEY, requestId);//修改了ReqTraceConsts.TRACE_KEY值从TT为traceId，这样可以从日志中取出。
    }

    private void resetContextHolders() {
        MDC.remove(ReqTraceConsts.TRACE_KEY);
    }

}
