package org.thivernale.springawspractice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    private static final String LOCALSTACK_VERSION = "localstack/localstack:4.6.0";

    @Bean
    @ServiceConnection
    public LocalStackContainer localStackContainer() {

        return new LocalStackContainer(DockerImageName.parse(LOCALSTACK_VERSION))
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init-localstack.sh", 0744),
                "/etc/localstack/init/ready.d/init-aws.sh.sh")
            .withEnv("DEFAULT_REGION", "eu-central-1"); // enforce region where resources are initialized in
    }

    @Bean
    DynamicPropertyRegistrar apiPropertiesRegistrar(LocalStackContainer localStackContainer) {
        return registry -> {
            registry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
            registry.add("spring.cloud.aws.region.static", localStackContainer::getRegion);
            registry.add("spring.cloud.aws.credentials.access-key", localStackContainer::getAccessKey);
            registry.add("spring.cloud.aws.credentials.secret-key", localStackContainer::getSecretKey);
        };
    }
}
