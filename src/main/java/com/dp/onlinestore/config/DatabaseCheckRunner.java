package com.dp.onlinestore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCheckRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCheckRunner.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheckRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            String result = jdbcTemplate.queryForObject("SELECT version()", String.class);
            log.info("✅ Подключение к PostgreSQL успешно!");
            log.info("Версия PostgreSQL: {}", result);
        } catch (Exception e) {
            log.error("❌ Ошибка подключения к PostgreSQL: {}", e.getMessage());
        }
    }
}