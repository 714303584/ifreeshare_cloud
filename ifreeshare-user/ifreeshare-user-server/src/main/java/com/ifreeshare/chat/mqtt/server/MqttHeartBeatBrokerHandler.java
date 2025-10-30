package com.ifreeshare.chat.mqtt.server;
/*
 * Copyright 2019 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import com.ifreeshare.tools.JwtUtils;
import com.ifreeshare.tools.finals.TokenPayloadKeys;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Sharable
public final class MqttHeartBeatBrokerHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(MqttHeartBeatBrokerHandler.class);


    public static Map<String,ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<>();

    public static final MqttHeartBeatBrokerHandler INSTANCE = new MqttHeartBeatBrokerHandler();

    private MqttHeartBeatBrokerHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MqttMessage mqttMessage = (MqttMessage) msg;
        logger.info("Received MQTT message: " + mqttMessage);
        logger.info("ChannelHandlerContext:"+ctx.name());
        switch (mqttMessage.fixedHeader().messageType()) {
            case CONNECT:
                MqttConnectVariableHeader mqttConnectVariableHeader =  (MqttConnectVariableHeader)mqttMessage.variableHeader();
                MqttProperties mqttProperties =  mqttConnectVariableHeader.properties();
                MqttConnectPayload mqttConnectPayload = (MqttConnectPayload)mqttMessage.payload();
                String token = mqttConnectPayload.userName();
                //
                if(StringUtils.isEmpty(token)){
                   ctx.close();
                   return;
                }
                Map<String, Object> objectMap = JwtUtils.getTokenData(token);
                String userId = (String) objectMap.get(TokenPayloadKeys.USER_ID);
                channelHandlerContextMap.put(userId,ctx);

                MqttFixedHeader connackFixedHeader =
                        new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttConnAckVariableHeader mqttConnAckVariableHeader =
                        new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
                MqttConnAckMessage connack = new MqttConnAckMessage(connackFixedHeader, mqttConnAckVariableHeader);
                ctx.writeAndFlush(connack);
                break;
            case PINGREQ:
                MqttFixedHeader pingreqFixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                        MqttQoS.AT_MOST_ONCE, false, 0);
                MqttMessage pingResp = new MqttMessage(pingreqFixedHeader);
                ctx.writeAndFlush(pingResp);
                break;
            case PUBLISH:
                MqttPublishMessage mqttPublishMessage = (MqttPublishMessage)msg;
                ByteBuf byteBuf = mqttPublishMessage.payload();
                byte[] bytes = byteBuf.array();
                String string = new String(bytes, "UTF-8");
                logger.info("PUBLISH msg:"+string);

                break;
            case DISCONNECT:
                ctx.close();
                break;
                //进行消息订阅
            case SUBSCRIBE:
//                MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage();
                break;
            default:
                System.out.println("Unexpected message type: " + mqttMessage.fixedHeader().messageType());
                ReferenceCountUtil.release(msg);
                ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("Channel heartBeat lost");
        if (evt instanceof IdleStateEvent && IdleState.READER_IDLE == ((IdleStateEvent) evt).state()) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}