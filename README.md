# Spring Boot Demo
Spring Boot demo using jpa, kafka, and javaxt-express. The app is
used to render a real-time dashboard with telemetry data from multiple
web servers. Web requests from remote web servers are published
to a Kafka server that is then consumed by this application. 

## Key Features
- Load external config file from Main app
- Serve up web pages outside of the static Java source tree
- REST API used to server up data from a database via a repository layer
- ETL data from a remote Kafka service

## Spring Boot Anti-Patterns
There are several unorthodox techniques used in this project stemming
from my personal preference/belief that configuration files should
not reside within a project source tree (e.g. `application.properties` file).
Also, I believe that the web front-end should be separate from
the Java backend (i.e. separate from the static `resources` folder). 
I don't know what the correct approach is to support these two 
requirements, so I improvised. As a result, we have several unusual features:
- There's a main app (command line app) that is used to start the web server.
Before starting the server it parses an external config file (json) and 
initializes the Config class.
- In the Config class we set up a javax.sql.DataSource (HikariDataSource) 
along with other settings using properties from the config file. 
The DataSource is instantiated, somewhat magically, at runtime and does 
not rely on the `application.properties` file.
- As mentioned before, the main app starts the web server. On 
initialization, the web server starts a Kafka consumer in a separate 
thread using properties from the config file (e.g. host, port, topic)
along with an instance of a Repository`*` 
- The web front end is loaded dynamically at runtime. Source files 
(e.g. js, css, html) can be placed anywhere on the file system 
and can be updated without having to redeploy or restart the web server. 

`*` There should be a way to start the Kafka consumer in the main app 
(vs the web server) but I haven't been able to inject the Repository 
dependency outside the of the web server listener/thread just yet...