vertx-murmur
============



sudo docker run -v ~/World/git/vertx-murmur:/usr/local/var/lib/vertx:rw --net host --name "murmur.gateway" -d jansolo/vertx run -cluster src/com/murmur/gateway/RestGatewayServer.java src/com/murmur/gateway/HttpUtil.java 


sudo docker run -v ~/World/git/vertx-murmur:/usr/local/var/lib/vertx:rw --net host --name "murmur.persist" -d jansolo/vertx run -
cluster src/com/murmur/persist/MessagePersistor.java src/com/murmur/persist/DateParser.java


sudo docker run -v ~/World/git/vertx-murmur:/usr/local/var/lib/vertx:rw --net host --name "murmur.webserver" -p 80:80 -d jansolo/vertx run -cluster src/com/murmur/webserver/MurmurWebServer.java 





dd6cad81d21d        jansolo/vertx:latest        /usr/local/bin/vertx   40 hours ago        Up 7 hours                                                               murmur.webserver    
5c22b25e20db        jansolo/vertx:latest        /usr/local/bin/vertx   40 hours ago        Up 6 hours                                                               murmur.gateway      
f29568af51bf        jansolo/vertx:latest        /usr/local/bin/vertx   42 hours ago        Up 7 hours                                                               murmur.persist      
b22456481b9d        dockerfile/mongodb:latest   mongod --rest --http   2 days ago          Up 2 days           0.0.0.0:27017->27017/tcp, 0.0.0.0:28017->28017/tcp   mongodb             
rromano@ubuntu:~/World/git/vertx-murmur$ 



This is a test

