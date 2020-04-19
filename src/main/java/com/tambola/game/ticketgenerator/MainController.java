package com.tambola.game.ticketgenerator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

  @RequestMapping(value = "/ping", method = RequestMethod.GET, headers = "Accept=application/json")
  @ResponseStatus(value = HttpStatus.OK)
  @ResponseBody
  public String ping() {
    return "It works! ";
  }

}
