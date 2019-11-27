package com.iot_rdss_gateway.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author ：Mr-Candy
 * @date ：Created in 2019-11-26 9:04
 * @description：接口访问类
 * @modified By：
 * @version:
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
