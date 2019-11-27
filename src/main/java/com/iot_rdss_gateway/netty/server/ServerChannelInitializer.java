package com.iot_rdss_gateway.netty.server;

import com.iot_rdss_gateway.netty.process.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Description:
 * @Author 老薛
 * @Date 2019/6/21 15:33
 * @Version V1.0
 */
public class ServerChannelInitializer extends ChannelInitializer<RxtxChannel> {
    @Override
    protected void initChannel(RxtxChannel rxtxChannel) {
        //消息分隔
        rxtxChannel.pipeline().addLast(new LineBasedFrameDecoder(2048));
        rxtxChannel.pipeline().addLast(new StringDecoder());
        rxtxChannel.pipeline().addLast("handler", new ServerHandler());
    }
}
