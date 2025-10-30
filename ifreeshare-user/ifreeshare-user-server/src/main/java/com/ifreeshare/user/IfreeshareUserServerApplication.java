package com.ifreeshare.user;

import com.ifreeshare.chat.netty.server.ChatServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.dao.support.DaoSupport;

@MapperScan(value="com.ifreeshare.user.dao")
@ComponentScan(basePackages = {"com.ifreeshare"})
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication()
public class IfreeshareUserServerApplication {

  public static void main(String[] args) {
//    DaoSupport

    ChatServer chatServer = new ChatServer();
    chatServer.init();

    SpringApplication.run(IfreeshareUserServerApplication.class, args);
  }
}
