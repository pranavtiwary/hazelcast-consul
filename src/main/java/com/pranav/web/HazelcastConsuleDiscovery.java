package com.pranav.web;

import static com.pranav.web.HazelcastConsulDiscoveryFactory.CONSUL_HOST;
import static com.pranav.web.HazelcastConsulDiscoveryFactory.CONSUL_PORT;
import static com.pranav.web.HazelcastConsulDiscoveryFactory.CONSUL_SERVICE_NAME;
import static com.pranav.web.HazelcastConsulDiscoveryFactory.CONSUL_SERVICE_TAGS;
import static com.pranav.web.HazelcastConsulDiscoveryFactory.HAZELCAST_DELAY_DISCOVER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;

public class HazelcastConsuleDiscovery extends AbstractDiscoveryStrategy {

	private static final Logger log= LoggerFactory.getLogger(HazelcastConsuleDiscovery.class);

	private String consulServiceName = null;
	private String consulHost = null;
	private Integer consulPort = null;
	private long hazelcastDelayDiscovery=0;
	private Integer hazelcastPort = null;
	private String serviceId=null;
	private DiscoveryNode discoveryNode=null;
	private List<String> tags=null;


	public HazelcastConsuleDiscovery(DiscoveryNode discoveryNode, ILogger logger, @SuppressWarnings("rawtypes") Map<String, Comparable> properties) {
		super(logger, properties);
		this.discoveryNode = discoveryNode;
		this.consulHost=getOrDefault("consul-host", CONSUL_HOST ,"localhost");
		this.consulPort=getOrDefault("consul-port", CONSUL_PORT ,8500);
		this.consulServiceName=getOrDefault("consul-service-name", CONSUL_SERVICE_NAME ,"localhost");
		this.tags= Arrays.asList(getOrDefault("consul-host", CONSUL_SERVICE_TAGS ,"").split(","));	
		this.hazelcastDelayDiscovery=getOrDefault("hazelcast-delay-discover", HAZELCAST_DELAY_DISCOVER ,10000l).longValue();
		this.hazelcastPort=getOrDefault("hazelcast-port", CONSUL_PORT ,5701);
		Address myLocalAddress = this.discoveryNode.getPrivateAddress()==null?this.discoveryNode.getPrivateAddress():this.discoveryNode.getPublicAddress();this.serviceId= consulServiceName + "-" + myLocalAddress;
		logger.info("Discovery strategy started at {host=" //
				+ consulHost + ", port=" + consulPort + ", for service name="+ consulServiceName +"}");
		try {
			logger.info("Delaying Hazelcast discovery, sleeping: " + hazelcastDelayDiscovery + "ms");
			Thread.sleep(hazelcastDelayDiscovery);
		} catch(Exception e) {
			logger.severe("Unexpected error sleeping prior to discovery: " + e.getMessage(),e);
		}
	}

	@Override
	public void start() {
		Consul consul = Consul.builder().withHostAndPort(HostAndPort
				.fromParts(consulHost, consulPort)).build();
		AgentClient agentClient = consul.agentClient();
		agentClient.register(hazelcastPort, 3L, consulServiceName, serviceId,tags,Collections.emptyMap());
		try {
			agentClient.pass(serviceId);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		log.info("Registered with Consul["+this.consulHost+":"+this.consulPort+"] serviceId:"+serviceId);
	}

	@Override
	public void destroy() {
		Consul consul = Consul.builder().build();
		AgentClient agentClient = consul.agentClient();
		agentClient.deregister(serviceId);
	}

	@Override
	public Iterable<DiscoveryNode> discoverNodes() {
		List<DiscoveryNode> discoveredNodes = new ArrayList<DiscoveryNode>();
		try{
			Consul consul = Consul.builder().build();
			HealthClient healthClient = consul.healthClient();
			ConsulResponse<List<ServiceHealth>> healthyServiceInstances = healthClient.getHealthyServiceInstances(serviceId);
			List<ServiceHealth> responses = healthyServiceInstances.getResponse();
			for (ServiceHealth serviceHealth : responses) {
				discoveredNodes.add(new SimpleDiscoveryNode(
						new Address(serviceHealth.getService().getAddress(),serviceHealth.getService().getPort())));
			}
			/*
			CatalogClient catalogClient = consul.catalogClient();
			ConsulResponse<List<CatalogService>> catalogServices = catalogClient.getService(serviceId);
			for (CatalogService catalogNode : catalogServices.getResponse()) {
				String address=StringUtils.isBlank(catalogNode.getServiceAddress())?catalogNode.getAddress():catalogNode.getServiceAddress().trim();
				discoveredNodes.add(new SimpleDiscoveryNode(
						new Address(address,catalogNode.getServicePort())));
			}*/
		}catch(Exception ex){
			log.error("Error : ->",ex);
		}
		return discoveredNodes;
	}

}