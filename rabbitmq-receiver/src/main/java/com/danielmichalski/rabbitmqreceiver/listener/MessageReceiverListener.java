package com.vadymdev716.rabbitmqreceiver.listener;

import com.vadymdev716.rabbitmqreceiver.model.TeamsMessageDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Component
@Slf4j
@NoArgsConstructor
public class MessageReceiverListener {

    public static final String ROUTING_KEY = "queue";

    @Value("${chat.teams.webhook.url}")
    private String teamsWebhookUrl;

    @RabbitListener(queues = ROUTING_KEY)
    public void listenerMessage(Map<String,Object> message) {
        log.info("Received message from the RabbitMQ ROUTING_KEY: [{}], message: {}", ROUTING_KEY, message);
        sendMessageToTeams(message);
        log.info("POSTED message to teams channel: {}, message: {}", teamsWebhookUrl, message);
    }

    private void sendMessageToTeams(Map<String,Object> message) {
        TeamsMessageDto teamsMsg = new TeamsMessageDto();
        teamsMsg.setMessage(message);
        //String msgAsStr = "";
        //try {
        //    ObjectMapper mapper = new ObjectMapper();
        //    msgAsStr = mapper.writeValueAsString(message);
        //} catch (Exception ex) {
        //    log.error("FAILED to convert message object to string: {}", message, ex);
        //}
        //teamsMsg.setMessageStr(msgAsStr);
        sendMessageToTeams(teamsMsg);
   }

    private void sendMessageToTeams(TeamsMessageDto teamsMsg) {
        log.info("SENDING message to MS Teams channel: {}, msg: {}", teamsWebhookUrl, teamsMsg);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "{\"text\":\"" + teamsMsg + "\"}";
        try {
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            restTemplate.postForObject(teamsWebhookUrl, entity, String.class);
        } catch (Exception ex) {
            log.error("FAILED to send message to MS Teams channel: {}, requestBody: {}", teamsWebhookUrl, requestBody, ex);
        }
    }
}
