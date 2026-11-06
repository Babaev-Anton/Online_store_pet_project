package com.dp.onlinestore.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    // Храним IP и список временных меток запросов
    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Применяем лимит только к эндпоинту логина
        if (!request.getRequestURI().equals("/api/auth/login") || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // Очищаем старые записи и добавляем текущую
        requestCounts.putIfAbsent(clientIp, new CopyOnWriteArrayList<>());
        List<Long> timestamps = requestCounts.get(clientIp);
        timestamps.removeIf(time -> currentTime - time > ONE_MINUTE_MS);

        if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"TOO_MANY_REQUESTS\", \"message\": \"Слишком много попыток входа. Попробуйте позже.\"}");
            return;
        }

        timestamps.add(currentTime);
        filterChain.doFilter(request, response);
    }
}