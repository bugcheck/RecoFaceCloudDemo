package com.reconova.facecloud.mqtt.internal;


import com.reconova.facecloud.mqtt.model.ReceivedMessage;

/**
 * Created by james on 12/10/15.
 */
public interface IReceivedMessageListener {

    public void onMessageReceived(ReceivedMessage message);
}