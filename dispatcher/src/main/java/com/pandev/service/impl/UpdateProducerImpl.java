package com.pandev.service.impl;

import com.pandev.service.UpdateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateProducerImpl implements UpdateProducer {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String rabbitQueue, Update update) {
        log.debug("Sending message to queue: {}", update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
