package com.okowu.practice;

import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class Register {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @GetMapping("register")
  public ModelAndView registerform() {
    return new ModelAndView("register");
  }

  @PostMapping("register")
  public ModelAndView register(
      String firstName, String lastName, String email, String userName, String password)
      throws ClassNotFoundException {
    Class.forName("com.mysql.jdbc.Driver");
    // Add employee here
    try (var connection = DriverManager.getConnection(url, username, password);
        var statement = connection.createStatement()) {

      var sql =
          "insert into Employee (first_name,last_name,email,username,password,regdate) values('"
              + firstName
              + "','"
              + lastName
              + "','"
              + email
              + "','"
              + userName
              + "','"
              + password
              + "',CURDATE());";
      statement.execute(sql);

    } catch (SQLException ex) {

      ex.printStackTrace();
    }

    ModelAndView mv = new ModelAndView("register");
    mv.addObject("message", "user account has been added for " + userName);
    return mv;
  }
}
