package com.pranav.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

/**
 * 
 * @author pranav.tiwary@vuclip.com
 *
 */
public class HazelcastConsulDiscoveryFactory implements DiscoveryStrategyFactory {
	
	public static final PropertyDefinition CONSUL_HOST = 
			new SimplePropertyDefinition("consul-host", PropertyTypeConverter.STRING);
	public static final PropertyDefinition CONSUL_PORT = 
			new SimplePropertyDefinition("consul-port", PropertyTypeConverter.INTEGER);
	public static final PropertyDefinition CONSUL_SERVICE_NAME = 
			new SimplePropertyDefinition("consul-service-name", PropertyTypeConverter.STRING);
	public static final PropertyDefinition CONSUL_SERVICE_TAGS = 
			new SimplePropertyDefinition("consul-service-tags", PropertyTypeConverter.STRING);
	public static final PropertyDefinition HAZELCAST_DELAY_DISCOVER = 
			new SimplePropertyDefinition("hazelcast-delay-discover", PropertyTypeConverter.LONG);
	public static final PropertyDefinition HAZELCAST_PORT = 
			new SimplePropertyDefinition("hazelcast-port", PropertyTypeConverter.INTEGER);

	private static final Collection<PropertyDefinition> PROPERTIES =Arrays.asList(new PropertyDefinition[]{
			CONSUL_HOST,CONSUL_PORT,CONSUL_SERVICE_NAME,CONSUL_SERVICE_TAGS,HAZELCAST_DELAY_DISCOVER,HAZELCAST_PORT
	});

	@Override
	public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
		return HazelcastConsuleDiscovery.class;
	}

	@Override
	public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger,
			@SuppressWarnings("rawtypes") Map<String, Comparable> properties) {
		return new HazelcastConsuleDiscovery(discoveryNode, logger, properties);
	}

	@Override
	public Collection<PropertyDefinition> getConfigurationProperties() {
		return PROPERTIES;
	}

}
