package com.example.demo;

import java.util.*;

import javaxt.json.*;
import javaxt.io.Jar;
import javaxt.express.ConfigFile;
import static javaxt.utils.Console.*;


public class Main {

    public static void main(String[] arguments) throws Exception {

      //Parse args
        var args = parseArgs(arguments);



      //Get jar file
        Jar jar = new Jar(Main.class);
        javaxt.io.File jarFile = new javaxt.io.File(jar.getFile());



      //Get parent dir
        javaxt.io.Directory dir = null;
        if (jarFile.getExtension().equals("class")) { //Spring doesn't create a jar on every run...
            javaxt.io.Directory d = jarFile.getParentDirectory();
            while(d.getParentDirectory() != null) {
                if (d.getName().equals("target")) {
                    dir = d.getParentDirectory();
                    break;
                }
                d = d.getParentDirectory();
            }
        }
        else{
            dir = jarFile.getDirectory();
        }



      //Get config file
        javaxt.io.File configFile = null;
        if (args.containsKey("-config")){
            configFile = ConfigFile.getFile(args.get("-config"), jarFile);
            if (!configFile.exists()) {
                System.out.println("Could not find config file. " +
                "Use the \"-config\" parameter to specify a path to a config");
                return;
            }
        }
        else{
            if (dir!=null){
                configFile = new javaxt.io.File(dir, "config.json");
                if (!configFile.exists() && dir.getName().equals("dist")) {
                    configFile = new javaxt.io.File(dir.getParentDirectory(), "config.json");
                }
            }
        }



      //Load the config file
        Config.load(configFile);


      //Start web server
        {


          //Get port
            Integer port = getValue(args, "-port", "-p").toInteger();
            if (port==null) port = Config.get("webapp").get("port").toInteger();
            if (port==null) port = 8080;


          //Get web root
            String webRoot = getValue(args, "-web", "-dir").toString();
            if (webRoot==null) webRoot = Config.get("webapp").get("dir").toString();
            if (webRoot==null && dir!=null) webRoot = dir + "web/javaxt";
            try{
                if (!new javaxt.io.Directory(webRoot).exists()) throw new Exception();
            }
            catch (Exception e){
                System.out.println("Could not find web directory. " +
                "Use the \"-web\" parameter to specify a path or update the \"webapp\" " +
                "settings in your config file");
            }


          //Update config settings as needed
            if (Config.get("webapp").isNull()) Config.set("webapp", new JSONObject());
            Config.get("webapp").toJSONObject().set("port", port);
            Config.get("webapp").toJSONObject().set("dir", new javaxt.io.Directory(webRoot));


          //Start web server
            WebServer.start();
        }
    }

}