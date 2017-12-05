package com.github.rodbate.application;


import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TriggerController {



    @RequestMapping("/get")
    public String get(HttpServletRequest request){

        request.getParameter("");

        return "get-----";
    }

}
