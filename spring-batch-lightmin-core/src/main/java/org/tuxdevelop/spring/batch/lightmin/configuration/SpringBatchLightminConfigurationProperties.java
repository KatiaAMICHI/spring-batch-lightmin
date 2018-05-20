package org.tuxdevelop.spring.batch.lightmin.configuration;

import lombok.Data;
import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.tuxdevelop.spring.batch.lightmin.exception.SpringBatchLightminConfigurationException;

import javax.annotation.PostConstruct;

@Data
@ConfigurationProperties(prefix = "spring.batch.lightmin")
public class SpringBatchLightminConfigurationProperties {

    private static final String DEFAULT_DATA_SOURCE_NAME = "dataSource";

    private final Environment environment;

    @Autowired
    public SpringBatchLightminConfigurationProperties(final Environment environment) {
        this.environment = environment;
    }

    //Table Prefix
    private String repositoryTablePrefix = AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX;
    //Lightmin Tables
    private String jobConfigurationTableName = "BATCH_JOB_CONFIGURATION";
    private String jobConfigurationValueTableName = "BATCH_JOB_CONFIGURATION_VALUE";
    private String jobConfigurationParameterTableName = "BATCH_JOB_CONFIGURATION_PARAMETERS";
    //Repository
    private LightminRepositoryType lightminRepositoryType = LightminRepositoryType.JDBC;
    private BatchRepositoryType batchRepositoryType = BatchRepositoryType.JDBC;
    //Remote Repository
    private String remoteRepositoryServerUrl;
    private String remoteRepositoryUsername;
    private String remoteRepositoryPassword;
    private Boolean discoverRemoteRepository = Boolean.FALSE;
    private String remoteRepositoryServerDiscoveryName = "spring-batch-lightmin-repository-server";
    private Integer remoteRepositoryServerStartupDiscoveryRetry = 30;
    private Long remoteRepositoryServerStartupDiscoveryRetryWaitTime = 500L;
    //Spring Batch Datasource
    private String batchDataSourceName = DEFAULT_DATA_SOURCE_NAME;
    private String dataSourceName = DEFAULT_DATA_SOURCE_NAME;
    private String configurationDatabaseSchema;
    //Lightmin Application name
    private String applicationName;

    public void setConfigurationDatabaseSchema(final String configurationDatabaseSchema) {
        if (configurationDatabaseSchema != null) {
            if (StringUtils.isEmpty(configurationDatabaseSchema)) {
                throw new SpringBatchLightminConfigurationException("configurationDatabaseSchema must not be empty!");
            }
        }
        this.configurationDatabaseSchema = configurationDatabaseSchema;
    }

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(this.applicationName)) {
            this.applicationName = this.environment.getProperty("spring.application.name");
        }
        if (this.applicationName == null || this.applicationName.isEmpty()) {
            throw new SpringBatchLightminConfigurationException("The property spring.batch.lightmin.application-name " +
                    "must not be null or empty. The value has to be set or spring.application.name has to be present!");
        }
    }
}
