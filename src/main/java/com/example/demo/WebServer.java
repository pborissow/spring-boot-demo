package com.example.demo;

import com.example.demo.persistance.RequestRepository;

import jakarta.servlet.http.*;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.*;

import java.util.*;
import javaxt.io.Directory;
import javaxt.express.FileManager;

import static javaxt.utils.Timer.*;
import static javaxt.utils.Console.*;


@SpringBootApplication
@RestController
public class WebServer {

    @Autowired
    private HttpServlet servlet;

    private View defaultView;
    private Directory webRoot;
    private FileManager fileManager;

    @Autowired
    RequestRepository requestRepository;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    WebServer(){
        console.log("Starting Web Server...");


      //Instantiate FileManager
        webRoot = (javaxt.io.Directory) Config.get("webapp").get("dir").toObject();
        fileManager = new FileManager(webRoot);


      //Create default "view"
        WebServer me = this;
        defaultView = new View(){
            public void render(@Nullable Map<String, ?> model,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
                me.processRequest(request, response);
            }
        };



      //Start Kafka
        new Thread(new Runnable() {
            public void run() {
                ArrayList<javaxt.utils.Timer> timers = new ArrayList<>();
                timers.add(setInterval(()->{
                    if (requestRepository!=null) {

                      //Cancel this timer
                        timers.get(0).cancel();

                      //Update config
                        Config.set("requestRepository", requestRepository);

                      //Start kafka listener
                        Kafka.start();
                    }
                }, 400));
            }
        }).start();
    }


  //**************************************************************************
  //** start
  //**************************************************************************
  /** Used to start the web server
   */
    public static void start(){

        Integer port = Config.get("webapp").get("port").toInteger();
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


  //**************************************************************************
  //** processRequest
  //**************************************************************************
  /** Used to process HTTP requests
   */
    private void processRequest(jakarta.servlet.http.HttpServletRequest req,
        jakarta.servlet.http.HttpServletResponse rsp) throws Exception {


      //Convert jakarta request/response to javaxt request/response
        var request = new javaxt.http.servlet.HttpServletRequest(req, servlet);
        var response = new javaxt.http.servlet.HttpServletResponse(request, rsp);


      //Get path from url, excluding servlet path and leading "/" character
        String path = request.getPathInfo();
        if (path==null) path = request.getServletPath(); //Spring-specific oddity...
        if (path!=null) path = path.substring(1);


      //Get first segment in the path
        String service = path==null ? "" : path.toLowerCase();
        if (service.contains("/")) service = service.substring(0, service.indexOf("/"));


      //Send static file if we can
        if (service.length()==0){

          //If the service is empty, send welcome file (e.g. index.html)
            fileManager.sendFile(request, response);
            return;
        }
        else{

          //Check if the service matches a file or folder in the web
          //directory. If so, send the static file as requested.
            for (Object obj : webRoot.getChildren()){
                String name;
                if (obj instanceof javaxt.io.File){
                    name = ((javaxt.io.File) obj).getName();
                }
                else{
                    name = ((javaxt.io.Directory) obj).getName();
                }


                if (service.equalsIgnoreCase(name)){
                    fileManager.sendFile(path, request, response);
                    if (response.getStatus()!=404) return;
                }
            }
        }

        console.log("404", path);
    }
}