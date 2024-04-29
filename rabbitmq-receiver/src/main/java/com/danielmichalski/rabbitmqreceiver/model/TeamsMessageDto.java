package com.vadymdev716.rabbitmqreceiver.model;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TeamsMessageDto implements Serializable {
    private Map<String,Object> message;
    //private String messageStr;
}
