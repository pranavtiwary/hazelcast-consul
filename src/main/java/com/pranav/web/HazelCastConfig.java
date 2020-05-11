package com.pranav.web;

import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;
/**
 * Hazel cast config
 * 
 * @author pranav.tiwary@vuclip.com
 *
 */
@ConditionalOnProperty(name="hazelcast.enabled", havingValue="true")
@Configuration
@EnableCaching
public class HazelCastConfig {

	private static final Logger logger = LoggerFactory.getLogger(HazelCastConfig.class);

	@Value("${consul.host:localhost}")
	private String consulHost = null;
	@Value("${consul.port:8500}")
	private String consulPort;
	@Value("${consule.service.tags:ubsclient}")
	private String consulTags;
	@Value("${consul.servicename}")
	private String consulServiceName = null;
	@Value("${hazelcast.port:5701}")
	private String hazelcastPort;
	@Value("${hazelcast.delay.discover:10000}")
	private String delayDiscover;
	
	

	@Bean
	public Config config() throws UnknownHostException{
		logger.info("Configurtion for hazelcast Started");
		Config config = new Config();
		config.setProperty("hazelcast.initial.min.cluster.size","1");
		config.setProperty("hazelcast.discovery.enabled", "true");
		config.setProperty("hazelcast.shutdownhook.enabled", "false");

		NetworkConfig network = config.getNetworkConfig();
		JoinConfig join = network.getJoin();
		join.getTcpIpConfig().setEnabled(false);
		join.getMulticastConfig().setEnabled(false);
		join.getAwsConfig().setEnabled(false);

		DiscoveryConfig discoveryConfig = join.getDiscoveryConfig();
		DiscoveryStrategyFactory factory = new HazelcastConsulDiscoveryFactory();
		DiscoveryStrategyConfig strategyConfig = new DiscoveryStrategyConfig(factory);
		strategyConfig.addProperty("consul-host",consulHost);
		strategyConfig.addProperty("consul-port",consulPort);
		strategyConfig.addProperty("consul-service-name",consulServiceName);
		strategyConfig.addProperty("consul-service-tags",consulTags);
		strategyConfig.addProperty("hazelcast-delay-discover", delayDiscover);
		strategyConfig.addProperty("hazelcast-port", hazelcastPort);
		discoveryConfig.addDiscoveryStrategyConfig(strategyConfig);

		config
		.addMapConfig(new MapConfig().setName("MY-MAP-1").setNearCacheConfig(new NearCacheConfig()))
		.addMapConfig(new MapConfig().setName("MY-MAP-2").setNearCacheConfig(new NearCacheConfig()))
		.setProperty("hazelcast.logging.type","slf4j"); 
		logger.info("Configurtion for hazelcast : {}, is done", config);
		return config;
	}

	@Bean
	public HazelcastInstance hazelcastInstance(Config config) {
		logger.info("Building Hazel cast instance");
		// for client HazelcastInstance LocalMapStatistics will not available
		// return HazelcastClient.newHazelcastClient();
		HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
		logger.info("Hazel cast instance : {}",instance);
		return instance;
	}


	@Bean
	public CacheManager cacheManager(HazelcastInstance instance) {
		return new HazelcastCacheManager(instance);
	}


	@PreDestroy
	public void shutdownHazelCastInstance(){
		Hazelcast.shutdownAll();
	}
}
