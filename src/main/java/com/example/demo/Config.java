package com.example.demo;

//Java includes
import java.util.*;

//JavaXT includes
import javaxt.json.*;
import static javaxt.utils.Console.console;
import static javaxt.express.ConfigFile.updateDir;
import static javaxt.express.ConfigFile.updateFile;


//Spring stuff
import org.springframework.context.annotation.*;
import org.springframework.boot.jdbc.DataSourceBuilder;

//Hikari connection pool
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@Configuration
public class Config {

    private static javaxt.express.Config config = new javaxt.express.Config();

    //private Config(){}


  //**************************************************************************
  //** load
  //**************************************************************************
  /** Used to load a config file (JSON) and update config settings
   */
    public static void load(javaxt.io.File configFile) throws Exception {

        JSONObject json;
        if (configFile==null || !configFile.exists()) {
            json = new JSONObject();
        }
        else{
            json = new JSONObject(configFile.getText());
        }

        load(json, configFile);
    }


  //**************************************************************************
  //** load
  //**************************************************************************
  /** Used to load a config file (JSON) and update config settings
   *  @param json Config information for the app
   *  @param configFile File used to resolve relative paths found in the json
   */
    public static void load(JSONObject json, javaxt.io.File configFile) throws Exception {


      //Update relative paths in the web config
        JSONObject webConfig = json.get("webapp").toJSONObject();
        if (webConfig!=null){
            updateDir("webDir", webConfig, configFile, false);
            updateDir("logDir", webConfig, configFile, true);
            updateFile("keystore", webConfig, configFile);
        }


      //Get database config
        JSONObject dbConfig = json.get("database").toJSONObject();
        if (dbConfig==null || dbConfig.isEmpty()){
            dbConfig = new JSONObject();
            dbConfig.set("driver", "H2");
            dbConfig.set("maxConnections", "25");
            dbConfig.set("path", "data/database");
            json.set("database", dbConfig);
        }


      //Process path variable in the database config
        if (dbConfig.has("path")){
            updateFile("path", dbConfig, configFile);
            String path = dbConfig.get("path").toString().replace("\\", "/");
            dbConfig.set("host", path);
            dbConfig.remove("path");
        }


      //Load config
        config.init(json);

    }


  //**************************************************************************
  //** get
  //**************************************************************************
  /** Returns the value for a given key.
   */
    public static JSONValue get(String key){
        return config.get(key);
    }


  //**************************************************************************
  //** set
  //**************************************************************************
    public static void set(String key, Object value){
        config.set(key, value);
    }


  //**************************************************************************
  //** dataSource
  //**************************************************************************
    @Bean
    public javax.sql.DataSource dataSource() {


        javaxt.sql.Database database = config.getDatabase();
        String userName = database.getUserName();
        String password = database.getPassword();


        if (database == null){
            return DataSourceBuilder.create()
                .url("jdbc:h2:mem:test")
                //.username("your_username")
                //.password("your_password")
                .driverClassName("org.h2.Driver")
                .build();
        }


        HikariConfig hk = new HikariConfig();
        hk.setJdbcUrl(database.getConnectionString());
        if (userName!=null) hk.setUsername(userName);
        if (password!=null) hk.setPassword(password);
        hk.setDriverClassName(database.getDriver().getClassName());
        hk.setMaximumPoolSize(database.getConnectionPoolSize());
        hk.setConnectionTimeout(60000);

        return new HikariDataSource(hk);
    }

}