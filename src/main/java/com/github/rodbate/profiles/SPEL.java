package com.github.rodbate.profiles;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SPEL {

    @Value("#{systemProperties['java.version']}")
    public String time;


}
