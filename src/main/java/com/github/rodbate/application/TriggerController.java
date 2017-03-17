package com.github.rodbate.application;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class TriggerController {



    @RequestMapping("/get")
    public String get(){
        return "get-----";
    }

}
