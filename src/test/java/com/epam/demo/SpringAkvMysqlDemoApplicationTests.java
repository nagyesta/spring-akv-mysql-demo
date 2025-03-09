package com.epam.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;

@SpringBootTest
class SpringAkvMysqlDemoApplicationTests {

   @Autowired
   MySqlConnectionCheck connectionCheck;

   @Test
   void contextLoads() throws SQLException {
      connectionCheck.verifyConnectivity();
   }

}
