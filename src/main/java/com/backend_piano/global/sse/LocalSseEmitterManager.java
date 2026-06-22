package com.backend_piano.global.sse;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class LocalSseEmitterManager implements SseEmitterManager {

    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(Long studentId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        emitter.onCompletion(() -> emitters.remove(studentId));
        emitter.onTimeout(() -> emitters.remove(studentId));
        emitter.onError(e -> emitters.remove(studentId));

        emitters.put(studentId, emitter);

        try {
            emitter.send(SseEmitter.event().name(SseEventName.CONNECT).data("connected"));
        } catch (IOException e) {
            emitters.remove(studentId);
        }

        return emitter;
    }

    @Override
    public void send(Long studentId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(studentId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException e) {
            emitters.remove(studentId);
        }
    }
}
