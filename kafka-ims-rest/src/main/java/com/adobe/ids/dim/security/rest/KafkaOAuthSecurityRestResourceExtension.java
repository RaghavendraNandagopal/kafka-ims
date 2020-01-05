package com.adobe.ids.dim.security.rest;

import com.adobe.ids.dim.security.metrics.OAuthMetrics;
import com.adobe.ids.dim.security.rest.config.KafkaOAuthSecurityRestConfig;
import com.adobe.ids.dim.security.rest.context.KafkaOAuthRestContextFactory;
import com.adobe.ids.dim.security.rest.filter.OAuthCleanerFilter;
import com.adobe.ids.dim.security.rest.filter.OAuthFilter;
import com.adobe.ids.dim.security.rest.filter.OAuthResponseFilter;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.extension.RestResourceExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.core.Configurable;
import java.lang.management.ManagementFactory;

public class KafkaOAuthSecurityRestResourceExtension implements RestResourceExtension {

    private static final Logger log = LoggerFactory.getLogger(KafkaOAuthSecurityRestResourceExtension.class);

    public void register(Configurable<?> config, KafkaRestConfig restConfig) {
        try{
            log.debug("KafkaOAuthSecurityRestResourceExtension -- register");
            final KafkaOAuthSecurityRestConfig secureKafkaRestConfig = new KafkaOAuthSecurityRestConfig(restConfig.getOriginalProperties(), null);
            log.debug("KafkaOAuthSecurityRestResourceExtension -- registering OAuthfilter");
            config.register(new OAuthFilter(secureKafkaRestConfig));
            config.register(OAuthCleanerFilter.class);
            config.register(new OAuthResponseFilter());

            //Register JMX Metrics
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("kafka.rest:name=ims-metrics");
            platformMBeanServer.registerMBean(OAuthMetrics.getInstance(), objectName);
        }catch (Exception e){
            log.error("KafkaOAuthSecurityRestResourceExtension -- exception: ", e);
        }
    }

    public void clean(){
        KafkaOAuthRestContextFactory.getInstance().clean();
    }

}
