package org.tuxdevelop.spring.batch.lightmin.client.configuration;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistrationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.lifecycle.LightminEurekaClientConfigBean;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.listener.DiscoveryListener;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.metadata.ConsulMetaDataExtender;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.metadata.EurekaMetaDataExtender;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.metadata.MetaDataExtender;
import org.tuxdevelop.spring.batch.lightmin.client.discovery.metadata.NoOperationMetaDataExtender;

import java.util.List;

/**
 * @author Marcel Becker
 * @since 0.5
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.lightmin.client", value = "discovery-enabled", havingValue = "true")
@AutoConfigureAfter({SimpleDiscoveryClientAutoConfiguration.class})
public class LightminClientDiscoveryConfiguration {

    @Configuration
    @ConditionalOnClass(EurekaClient.class)
    @ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
    public static class EurekaLightminClientDiscoveryConfiguration {

        @Bean
        public MetaDataExtender metaDataExtender(final ApplicationInfoManager applicationInfoManager) {
            return new EurekaMetaDataExtender(applicationInfoManager);
        }

        @Bean
        public EurekaClientConfig eurekaClientConfig(final MetaDataExtender metaDataExtender) {
            return new LightminEurekaClientConfigBean(metaDataExtender);
        }
    }

    @Configuration
    @ConditionalOnConsulEnabled
    @ConditionalOnClass(ConsulDiscoveryClient.class)
    public static class ConsulLightminClientDiscoveryConfiguration {

        @Bean
        public MetaDataExtender metaDataExtender(final ConsulDiscoveryProperties consulDiscoveryProperties) {
            return new ConsulMetaDataExtender(consulDiscoveryProperties);
        }

        @Bean
        @ConditionalOnMissingBean(ConsulAutoRegistration.class)
        public ConsulAutoRegistration consulAutoRegistration(final MetaDataExtender metaDataExtender,
                                                             final AutoServiceRegistrationProperties autoServiceRegistrationProperties,
                                                             final ConsulDiscoveryProperties consulDiscoveryProperties,
                                                             final ApplicationContext applicationContext,
                                                             final List<ConsulRegistrationCustomizer> consulRegistrationCustomizers,
                                                             final HeartbeatProperties heartbeatProperties) {
            metaDataExtender.extendMetaData();
            return ConsulAutoRegistration.registration(
                    autoServiceRegistrationProperties,
                    consulDiscoveryProperties,
                    applicationContext,
                    consulRegistrationCustomizers,
                    heartbeatProperties);
        }

    }

    @Bean
    public DiscoveryListener discoveryListener(final LightminClientProperties lightminClientProperties) {
        return new DiscoveryListener(lightminClientProperties);
    }

    @Bean
    @ConditionalOnMissingBean(MetaDataExtender.class)
    public MetaDataExtender metaDataExtender() {
        return new NoOperationMetaDataExtender();
    }


}
