package org.nolat.scrolls;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class TestMQ {

    private static final Logger log = Logger.getLogger(TestMQ.class);

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            Connection con = factory.newConnection();
            Channel channel = con.createChannel();
            System.out.println("Created connection and channel");
            for (int i = 0; i < 100; i++) {
                channel.basicPublish("", "java2node", false, null, ("Hello Rabbit " + i).getBytes());
                System.out.println("Sent message " + i);
                ScrollsAssistant.pause(500);
            }
            channel.close();
            con.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println("Done");
    }

}
