package com.epam.demo;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAkvMysqlDemoApplication {

   public static void main(String[] args) throws SQLException {
      SpringApplication.run(SpringAkvMysqlDemoApplication.class, args)
            .getBean(MySqlConnectionCheck.class).verifyConnectivity();
   }
}
