# csv-server

### Description
Create a REST service which consumes CSV files (comma separated). Each file represents individual piece of data - i.e. there are no relations between the files. The first line of the file always represents a header with column names. The subsequent rows represent data.
The data should get stored in some persistent storage so they may survive application restart.
In addition to consuming the files the service should provide endpoints to:

>- retrieve header of a particular file
>- retrieve rows in specified interval (e.g. 0 - 10, 3 - 4 etc.)
>- the data should be returned as XML


### Prerequisites
 >- Maven
 >- Java 8 (Jdk)
 >- Git
 >- Redis

Tested on Ubuntu 64bit, OpenJdk 1.8, Maven 3.3.9., Git 1.9.1, Redis 3.2.6

#### Download project

```
git clone https://github.com/kafkapre/csv-server.git
```

#### How to build
Go to project's directory and run command:

```
mvn package -DskipTests
```

#### How to run tests
Command below runs also e2e test. Embedded Redis is started on port 6370 (Can cause problems).
```
mvn test
```

#### How to run application

##### Run Redis
You can use Docker to run Redis.
```
docker run --name myredis -p 6379:6379 -d redis:3.2.6
```

##### Run server
For example go to project's "target" directory and run command:
```
java -jar csvserver-1.0.jar server <paths_to_properties_file>
```

###### Properties file

```
server:
  applicationConnectors:
    - type: http
      port: 8080        # jetty port

csvSeparator: ";"
redisPort: 6379
redisHost: "localhost"
```

###Used technologies

>- Embedded Jetty
>- Embedded Redis (for testing)
>- Java 8
>- Jackson, Jersey, Dropwizard frameworks

### API

 - GET     / 
    - root endpoint
 - GET     /csv 
    - Returns list of all csv documents 
 - POST    /csv 
    - Create csv document. Accepts text plain body.
 - GET     /csv/{id} 
    - Returns particular csv document metadata
 - PUT     /csv/{id} 
    - Update particular csv document
 - GET     /csv/{id}/lines?from=0&to=-1
    - Returns all lines of csv document with {id}
    
    
    