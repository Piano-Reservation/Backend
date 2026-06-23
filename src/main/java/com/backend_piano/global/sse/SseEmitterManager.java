package com.backend_piano.global.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterManager {
    SseEmitter subscribe(Long studentId);
    void send(Long studentId, String eventName, Object data);
}
