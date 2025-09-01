package com.example.appb.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class MetricsConfig {

    @Bean
    public HttpSizeMetricsFilter httpSizeMetricsFilter(MeterRegistry meterRegistry) {
        return new HttpSizeMetricsFilter(meterRegistry);
    }

    public static class HttpSizeMetricsFilter extends OncePerRequestFilter {
        private final MeterRegistry meterRegistry;

        public HttpSizeMetricsFilter(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain filterChain) throws ServletException, IOException {

            String requestUri = request.getRequestURI();

            if (shouldSkipMetrics(requestUri)) {
                filterChain.doFilter(request, response);
                return;
            }

            long requestSize = getRequestSize(request);
            String uri = simplifyUri(requestUri);
            String method = request.getMethod();

            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

            filterChain.doFilter(request, responseWrapper);

            long responseSize = getResponseSize(responseWrapper);
            String status = String.valueOf(responseWrapper.getStatus());

            responseWrapper.copyBodyToResponse();

            meterRegistry.counter("http_request_size_bytes_total",
                    "method", method,
                    "uri", uri,
                    "status", status)
                    .increment(requestSize);

            meterRegistry.counter("http_response_size_bytes_total",
                    "method", method,
                    "uri", uri,
                    "status", status)
                    .increment(responseSize);
        }

        private long getRequestSize(HttpServletRequest request) {
            int contentLength = request.getContentLength();
            return contentLength > 0 ? contentLength : 0;
        }

        private long getResponseSize(ContentCachingResponseWrapper response) {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                return content.length;
            }
            
            String contentLength = response.getHeader("Content-Length");
            if (contentLength != null) {
                try {
                    return Long.parseLong(contentLength);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return 0;
        }

        private String simplifyUri(String uri) {
            if (uri.startsWith("/api/validate-product")) {
                return "/api/validate-product";
            }
            return uri;
        }

        private boolean shouldSkipMetrics(String uri) {
            if (uri.startsWith("/actuator/")) {
                return true;
            }
            
            return false;
        }
    }
}
