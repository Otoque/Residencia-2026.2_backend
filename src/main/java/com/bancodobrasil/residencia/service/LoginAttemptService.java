package com.bancodobrasil.residencia.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proteção contra força bruta: após N falhas seguidas, bloqueia a chave (IP + usuário)
 * por alguns minutos. Em memória (some ao reiniciar); se rodar em várias instâncias,
 * trocar por Redis/banco.
 */
@Service
public class LoginAttemptService {

    private record Attempt(int failures, Instant lastFailure, Instant lockedUntil) {}

    private static final int MAX_TRACKED_KEYS = 10_000;

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final Duration lockDuration;

    public LoginAttemptService(@Value("${security.auth.max-attempts}") int maxAttempts,
                               @Value("${security.auth.lock-minutes}") long lockMinutes) {
        this.maxAttempts = maxAttempts;
        this.lockDuration = Duration.ofMinutes(lockMinutes);
    }

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        return attempt != null
                && attempt.lockedUntil() != null
                && Instant.now().isBefore(attempt.lockedUntil());
    }

    public void recordFailure(String key) {
        Instant now = Instant.now();

        if (attempts.size() > MAX_TRACKED_KEYS) {
            attempts.entrySet().removeIf(e -> isExpired(e.getValue(), now));
        }

        attempts.compute(key, (k, current) -> {
            int failures = (current == null || isExpired(current, now)) ? 1 : current.failures() + 1;
            Instant lockedUntil = failures >= maxAttempts ? now.plus(lockDuration) : null;
            return new Attempt(failures, now, lockedUntil);
        });
    }

    public void reset(String key) {
        attempts.remove(key);
    }

    private boolean isExpired(Attempt attempt, Instant now) {
        return now.isAfter(attempt.lastFailure().plus(lockDuration));
    }
}
