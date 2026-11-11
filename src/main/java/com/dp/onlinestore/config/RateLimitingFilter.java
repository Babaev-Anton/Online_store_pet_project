package com.dp.onlinestore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long ONE_MINUTE_MS = 60_000;

    // Читаем настройку из application.yml. По умолчанию true (включено)
    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    // Храним IP и список временных меток запросов
    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ВРЕМЕННО ОТКЛЮЧЕНО ДЛЯ ТЕСТОВ (вернуть перед финальным коммитом)
        filterChain.doFilter(request, response);

        /*
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        // ... остальная логика
        */
    }
}