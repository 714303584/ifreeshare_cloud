package com.ifreeshare.chat.netty.server.process;

import com.ifreeshare.chat.client.Message;
import com.ifreeshare.chat.netty.server.Clientor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface MessageProcessor {

    void process(Message message, Clientor clientor);


}
