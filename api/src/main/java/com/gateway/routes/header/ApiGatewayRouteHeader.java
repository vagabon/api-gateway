package com.gateway.routes.header;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class ApiGatewayRouteHeader extends HttpServletRequestWrapper {
    private static final String X_USER = "x-user-";

    private final Map<String, List<String>> headers = new TreeMap<>(
        String.CASE_INSENSITIVE_ORDER);

    public ApiGatewayRouteHeader(
        HttpServletRequest req,
        Map<String, String> userHeaders) {
        super(req);
        Collections.list(req.getHeaderNames()).stream()
            .filter(name -> !name.toLowerCase().startsWith(X_USER))
            .forEach(name -> headers.put(name, Collections.list(req.getHeaders(name))));
        userHeaders.forEach((name, value) -> headers.put(name, List.of(value)));
    }

    @Override
    public String getHeader(String name) {
        var values = headers.get(name);
        return values == null ? null : values.get(0);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(headers.getOrDefault(name, List.of()));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }
}
