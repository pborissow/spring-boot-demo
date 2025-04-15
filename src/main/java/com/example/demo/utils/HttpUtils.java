package com.example.demo.utils;

import org.springframework.http.*;
import org.springframework.data.domain.*;

import java.util.*;
import static javaxt.utils.Console.console;

//******************************************************************************
//**  HttpUtils
//******************************************************************************
/**
 *   Provides static methods used to help process HTTP requests and generate
 *   responses.
 *
 ******************************************************************************/

public class HttpUtils {

    public static JSONMapper jsonMapper = new JSONMapper();


  //**************************************************************************
  //** getPageRequest
  //**************************************************************************
    public static Pageable getPageRequest(Map<String, String> params){

        int offset = 0;
        try{ offset = Integer.parseInt(params.get("offset"));}catch(Exception e){}

        int limit = 50;
        try{ limit = Integer.parseInt(params.get("limit"));}catch(Exception e){}

        String orderBy = params.get("orderBy");
        if (orderBy==null || orderBy.isEmpty()){
            return PageRequest.of(offset, limit);
        }
        else{
            return PageRequest.of(offset, limit, Sort.by(orderBy).ascending());
        }
    }


  //**************************************************************************
  //** getResponse
  //**************************************************************************
    public static ResponseEntity getResponse(Page page) throws Exception {
        return new ResponseEntity(jsonMapper.writeValueAsString(page.stream().toList()), HttpStatus.OK);
    }


  //**************************************************************************
  //** getResponse
  //**************************************************************************
    public static ResponseEntity getResponse(Exception ex){
        return new ResponseEntity(ex.getMessage(), HttpStatusCode.valueOf(500));
    }

}
