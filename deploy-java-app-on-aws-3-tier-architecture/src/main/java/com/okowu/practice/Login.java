package com.okowu.practice;

import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class Login {

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  private String userId = "";

  private String errorMessage = "";

  @PostMapping("login")
  public ModelAndView login(String userName, String password) throws ClassNotFoundException {

    Class.forName("com.mysql.jdbc.Driver");
    // validate user credentials
    String query =
        "select * from Employee where username='" + userName + "' and password='" + password + "'";
    try (var connection = DriverManager.getConnection(url, userName, password);
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(query)) {
      if (resultSet.next()) {
        log.debug(
            "{} {} {} {}",
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getString(4));
        userId = resultSet.getString(4);
      }
    } catch (SQLException ex) {
      log.error(ex.getMessage());
      errorMessage = ex.getMessage();
    }

    ModelAndView mv;

    if (StringUtils.hasText(userId)) {
      mv = new ModelAndView("user");
      mv.addObject("username", userId);
    } else {
      mv = new ModelAndView("login");
      mv.addObject("errorMessage", errorMessage);
    }

    return mv;
  }

  @GetMapping("login")
  public ModelAndView registerForm() {
    return new ModelAndView("login");
  }
}
