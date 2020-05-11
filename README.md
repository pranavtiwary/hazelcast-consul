# CONSUL-HAZELCAST

hazelcast integration with consule for service discovery

## Setup steps

	1. Clone the project
	2. run mvn eclipse:eclipse
	3. import project into eclipse for exisitng maven projects.
	4. run mvn clean install
	5. run application as spring boot , (this will create required db schema)

**Health and Actuator**

http://localhost:8080



http://docs.hazelcast.org/docs/latest-dev/manual/html-single/index.html#discovery-mechanisms

https://github.com/bitsofinfo/hazelcast-consul-discovery-spi

https://github.com/bitsofinfo/hazelcast-consul-discovery-spi/blob/master/src/main/java/org/bitsofinfo/hazelcast/discovery/consul/ConsulDiscoveryStrategy.java

https://github.com/bitsofinfo/hazelcast-consul-discovery-spi/blob/master/src/main/java/org/bitsofinfo/hazelcast/discovery/consul/ConsulDiscoveryStrategy.java

https://github.com/hazelcast/hazelcast-gcp

https://github.com/noctarius/hazelcast-discovery-spi-demonstration/blob/master/src/main/java/com/hazelcast/example/SomeRestServiceDiscoveryStrategyFactory.java


https://blog.hazelcast.com/hazelcast-discovery-spi/


https://www.consul.io/intro/getting-started/services.html


**consul**
https://www.consul.io/intro/getting-started/services.html

Download and start sever

start server :  ./consul agent -dev

Hit below url to see service regsitered

curl http://localhost:8500/v1/catalog/service/hazelcast-cosul-ubs

