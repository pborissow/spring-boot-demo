package com.example.demo;
import static com.example.demo.utils.HttpUtils.*;
import com.example.demo.persistance.RequestEntity;
import com.example.demo.persistance.RequestRepository;

import java.util.*;


import org.springframework.http.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import static javaxt.utils.Console.console;


@RestController
public class WebServices {

    @Autowired
    RequestRepository requestRepository;



  //**************************************************************************
  //** getDate
  //**************************************************************************
    @RequestMapping(value = "/date/**", method = RequestMethod.GET)
    public String getDate(){
        return new java.util.Date().toString();
    }


  //**************************************************************************
  //** hello
  //**************************************************************************
    @RequestMapping("/hello")
    public String hello(@RequestParam Map<String, String> params){

        StringBuilder str = new StringBuilder();
        for (String key : params.keySet()) {
            str.append(key + " = " + params.get(key) + "\n");
        }

        return str.toString();
    }


  //**************************************************************************
  //** getRequests
  //**************************************************************************
    @RequestMapping("/requests")
    @ResponseBody
    public ResponseEntity getRequests(@RequestParam Map<String, String> params){

        try{
            Page<RequestEntity> page = requestRepository.findAll(getPageRequest(params));
            return getResponse(page);
        }
        catch(Exception ex){
            return getResponse(ex);
        }
    }

}