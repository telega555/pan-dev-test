package com.pandev.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@RestController
public class WebhookController {
    private final UpdateProcessor updateProcessor;

    @PostMapping("${webhook-url}")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
