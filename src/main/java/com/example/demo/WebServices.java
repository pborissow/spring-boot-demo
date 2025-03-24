package com.example.demo;

import java.util.*;
import org.springframework.web.bind.annotation.*;



@RestController
public class WebServices {

    @RequestMapping(value = "/date/**", method = RequestMethod.GET)
    public String getDate(){
        return new java.util.Date().toString();
    }


    @RequestMapping("/hello")
    public String hello(@RequestParam Map<String, String> params){

        StringBuilder str = new StringBuilder();
        for (String key : params.keySet()) {
            str.append(key + " = " + params.get(key) + "\n");
        }

        return str.toString();
    }

}