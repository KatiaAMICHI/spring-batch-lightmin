package org.tuxdevelop.spring.batch.lightmin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.tuxdevelop.spring.batch.lightmin.configuration.AbstractSpringBatchLightminConfiguration;

@Configuration
@ComponentScan(basePackages = "org.tuxdevelop.spring.batch.lightmin")
public class ITConfiguration extends AbstractSpringBatchLightminConfiguration {


}