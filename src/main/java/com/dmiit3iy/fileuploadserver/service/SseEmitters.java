package com.dmiit3iy.fileuploadserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitters {

    private static final Logger logger = LoggerFactory.getLogger(SseEmitters.class);

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter add(SseEmitter emitter) {
        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            logger.info("Emitter completed: {}", emitter);
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            logger.info("Emitter timed out: {}", emitter);
            emitter.complete();
            this.emitters.remove(emitter);
        });

        return emitter;
    }

    public void send(Object obj) {
        logger.info("Emitters current before deleting: {}", this.emitters);
        List<SseEmitter> failedEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            Thread thread = new Thread(()-> {
                try {
                    emitter.send(obj);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                    failedEmitters.add(emitter);
                    logger.error("Emitter failed: {}", emitter, e);
                }
            });
            thread.start();
        });


        this.emitters.removeAll(failedEmitters);
        logger.info("Emitters current: {}", this.emitters);
        logger.info("Removed Emitters current: {}", failedEmitters);
    }
}
