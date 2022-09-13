package io.github.baunz.metrics.kafka;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.github.baunz.metrics.kafka.app.DropwizardReporterExampleApplication;
import io.github.baunz.metrics.kafka.app.DropwizardReporterExampleConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(DropwizardExtensionsSupport.class)
@Testcontainers
public class DropwizardReporterIntegrationTest {

    private static DropwizardAppExtension<DropwizardReporterExampleConfiguration> EXT = new DropwizardAppExtension<>(
            DropwizardReporterExampleApplication.class,
            ResourceHelpers.resourceFilePath("conf.yml")
    );

    @Container
    public KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    @Test
    @SuppressWarnings("unchecked")
    public void test() {

        Client client = EXT.client();

        Response response = client.target(
                        String.format("http://localhost:%d/", EXT.getLocalPort()))
                .request()
                .post(Entity.text(kafka.getBootstrapServers()));

        assertEquals(204, response.getStatus());
        MetricRegistry metricRegistry = EXT.getEnvironment().metrics();

        Gauge gauge = metricRegistry.getGauges().get("org.apache.kafka.common.metrics.producer-metrics.my-producer-id.record-send-total");
        assertEquals(1.0, gauge.getValue());

    }
}
