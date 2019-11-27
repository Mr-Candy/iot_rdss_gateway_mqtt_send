package com.iot_rdss_gateway.netty.process;

import com.google.gson.Gson;
import com.iot_rdss_gateway.util.SpringUtil;
import com.iot_rdss_gateway.util.StringByteConvertor;
import com.iot_rdss_gateway.mqtt.MqttGateway;
import com.iot_rdss_gateway.netty.model.MessageModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * @Description:
 * @Author 老薛
 * @Date 2019/6/21 16:18
 * @Version V1.0
 */

@Component
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) {
   //     SpringUtil.getBean(ThreadPoolManager.class).addOrders(msg);
        logger.info("接收数据：" + msg);
        if(msg.startsWith("$BDTXR")/*||msg.startsWith("$BDWAA")*/){
            String[] receives = msg.split(",");
            String type = receives[1];  //信息类别
            String address = receives[2];//发信方地址
            String mode = receives[3];//电文形式 0-汉字；1-代码；2-混合传输
            String content = receives[receives.length-1].split("\\*")[0];//电文内容
            try {
                DataInputStream data = new DataInputStream(new ByteArrayInputStream(StringByteConvertor.hexStringToBytes(content)));
                if (data.available() <= 4) {
                    logger.error("数据内容不全，丢弃该帧数据：" + msg);
                    return;
                }
                byte qiShi = data.readByte();//自协议起始标识
                if (qiShi != 35) {
                    logger.error("起始标识不对，丢弃该帧");
                    return;
                }
                byte shiBieMark = data.readByte();//子协议识别标识
                byte zhenMark = data.readByte();//帧类型标识符
                byte zhenDataMark = data.readByte();//帧数据标识符
                byte zhenNum = (byte) ((zhenDataMark & 0xe0) >> 5);
                boolean buChuanMark = false;
                if ((zhenDataMark & 0x10) >> 4 == 1) {
                    buChuanMark = true;
                }
                byte[] body = new byte[data.available()];
                data.read(body);
                MessageModel message = new MessageModel(shiBieMark, zhenMark, zhenNum, buChuanMark, body);
                Gson gson = new Gson();
                String json = gson.toJson(message);
                SpringUtil.getBean(MqttGateway.class).sendToMqtt(json,"bd_rdss");
            }catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }else{
            logger.error("协议类型不支持，丢弃该帧");
            return;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("channel关闭，原因" + cause.getMessage());
        ctx.close();
    }
}
