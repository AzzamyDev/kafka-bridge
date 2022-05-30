package com.azzmydev.bridge;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;


@Configuration
public class MqttBean {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public final String AIS_KAPAL = "ais_kapal";
    public final String RADAR_DATA = "radar_data";
    public final String RADAR_IMAGE = "radar_image";
    public final String TRACKING_DATA = "tracking_data";
    public final String PERSONIL_IMAGE = "personil_image";
    public final String PERSONIL_VIDEO = "personil_video";
    public final String PERSONIL_GPS = "personil_gps";

    //MQTT ClientFactory
    public MqttPahoClientFactory mqttPahoClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[] {"tcp://localhost"});
        options.setCleanSession(true);
        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound(){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn", mqttPahoClientFactory(), "#");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return  adapter;
    }


//    Handle message from MQTT
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(){
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                System.out.println(topic);

                switch (topic){
                   case AIS_KAPAL :{
                    kafkaTemplate.send(AIS_KAPAL, message.getPayload().toString());
                       break;
                   }
                   case RADAR_DATA :{
                    kafkaTemplate.send(RADAR_DATA, message.getPayload().toString());
                       break;
                   }
                   case RADAR_IMAGE :{
                    kafkaTemplate.send(RADAR_IMAGE, message.getPayload().toString());
                       break;
                   }
                   case TRACKING_DATA :{
                    kafkaTemplate.send(TRACKING_DATA, message.getPayload().toString());
                       break;
                   }
                   case PERSONIL_IMAGE :{
                    kafkaTemplate.send(PERSONIL_IMAGE, message.getPayload().toString());
                       break;
                   }
                   case PERSONIL_VIDEO :{
                    kafkaTemplate.send(PERSONIL_VIDEO, message.getPayload().toString());
                       break;
                   }
                   case PERSONIL_GPS :{
                    kafkaTemplate.send(PERSONIL_GPS, message.getPayload().toString());
                       break;
                   }
                   default:
               }
            }
        };
    }

    @Bean
    public MessageChannel mqttOutboundChannel(){
        return new DirectChannel();
    }

    public MessageHandler mqttOutBoundHandler(){
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttPahoClientFactory());

        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("#");
        return messageHandler;
    }
}
