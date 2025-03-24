package com.example.demo;

import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.*;

import java.util.*;
import static javaxt.utils.Console.*;

@SpringBootApplication
@RestController
public class WebServer {

    @Autowired
    private HttpServlet servlet;

    private View defaultView;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    WebServer(){
        console.log("Starting Web Server...");

      //Create default "view"
        WebServer me = this;
        defaultView = new View(){
            public void render(@Nullable Map<String, ?> model,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
                me.processRequest(request, response);
            }
        };
    }


  //**************************************************************************
  //** start
  //**************************************************************************
  /** Used to start the web server
   */
    public static void start(int port){

        var args = new String[]{"--server.port="+port};

        SpringApplication.run(WebServer.class, args);
    }


  //**************************************************************************
  //** processRequest
  //**************************************************************************
  /** Default endpoint for all HTTP requests. HTTP requests can be routed to a
   *  different method using the <code>@RequestMapping</code> annotation.
   */
    @RequestMapping("/**")
    public ModelAndView processRequest(NativeWebRequest request) {
        return new ModelAndView(defaultView);
    }

//  //**************************************************************************
//  //** processRequest
//  //**************************************************************************
//  /** Used to process HTTP requests
//   */
//    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        var req = new javaxt.http.servlet.HttpServletRequest(request, servlet);
//        processRequest(req, new javaxt.http.servlet.HttpServletResponse(req, response));
//    }


  //**************************************************************************
  //** processRequest
  //**************************************************************************
  /** Used to process HTTP requests
   */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{


//      //Get path from url, excluding servlet path and leading "/" character
//        String path = request.getPathInfo();
//        if (path==null) path = request.getServletPath(); //Spring-specific oddity...
//        if (path!=null) path = path.substring(1);
//
//
//
//      //Get first "directory" in the path
//        String service = path==null ? "" : path.toLowerCase();
//        if (service.contains("/")) service = service.substring(0, service.indexOf("/"));
//
//
//        console.log(service, servlet.toString());
//        //response.getWriter().write(request.getRequestURL() + " " + new Date());


      //Create a ServiceRequest from the HttpServletRequest
        var req = new javaxt.express.ServiceRequest(request, servlet);

      //Update path and get the request method
        req.setPath(request.getServletPath()); //Spring-specific oddity...
        String method = req.getMethod();
        console.log(request.getPathInfo(), request.getServletPath(), method, req.getPath(0));

      //Generate ServiceResponse
        javaxt.express.ServiceResponse rsp;
        if (method.equals("getFile")){
            rsp = new javaxt.express.ServiceResponse(new javaxt.io.File("/temp/"+req.getPath(1)));
        }
        else{
            rsp = new javaxt.express.ServiceResponse("service: " + req.getService() + " method: " + method);
        }


      //Write ServiceResponse to the HttpServletResponse
        rsp.send(response, req);

    }



}