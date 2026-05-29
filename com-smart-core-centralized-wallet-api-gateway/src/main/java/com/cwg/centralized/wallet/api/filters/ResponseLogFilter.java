package com.cwg.centralized.wallet.api.filters;

import com.cwg.centralized.wallet.api.config.LogFilterConfiguration;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;


@Component
public class ResponseLogFilter extends ZuulFilter {

    private LogFilterConfiguration logFilterConfiguration;
    private Logger logger = LoggerFactory.getLogger(ResponseLogFilter.class);

    public ResponseLogFilter(LogFilterConfiguration logFilterConfiguration) {
        this.logFilterConfiguration = logFilterConfiguration;
    }

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return logFilterConfiguration.getLogResponse();
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
       
        try (final InputStream responseDataStream = context.getResponseDataStream()) {
            if(responseDataStream == null) {
                return null;
            }

            String responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));

            if(logFilterConfiguration.getLogResponseBody()) {
            }
            context.setResponseBody(responseData);
        }
        catch (Throwable e) {
        }
        return null;

    }
}
