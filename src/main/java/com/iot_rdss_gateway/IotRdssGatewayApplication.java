package com.iot_rdss_gateway;

import com.iot_rdss_gateway.netty.server.NettyServer;
import com.iot_rdss_gateway.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SpringUtil.class)
public class IotRdssGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotRdssGatewayApplication.class, args);
        SpringUtil.getBean(NettyServer.class).run();
    }

}