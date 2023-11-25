package io.jexxa.common.adapter.messaging.send;



import io.jexxa.common.facade.utils.annotation.CheckReturnValue;

import java.util.Properties;

public abstract class MessageSender
{
    public enum MessageType{TEXT_MESSAGE, BYTE_MESSAGE }

    @CheckReturnValue
    public <T> MessageBuilder send(T message)
    {
        return new MessageBuilder(message, this, MessageType.TEXT_MESSAGE);
    }

    @CheckReturnValue
    public <T> MessageBuilder sendByteMessage(T message)
    {
        return new MessageBuilder(message, this, MessageType.BYTE_MESSAGE);
    }

    /**
     * Sends an asynchronous text message to a queue
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required
     */
    protected abstract void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType);

    /**
     * Sends an asynchronous text message to a topic
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required
     */
    protected abstract void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType);

}
