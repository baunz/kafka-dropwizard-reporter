package io.github.baunz.metrics.kafka.app;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class DropwizardReporterExampleApplication extends Application<DropwizardReporterExampleConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DropwizardReporterExampleApplication.class);

    public static void main(final String[] args) throws Exception {
        new DropwizardReporterExampleApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropwizardReporterExample";
    }

    @Override
    public void initialize(final Bootstrap<DropwizardReporterExampleConfiguration> bootstrap) {
    }

    @Override
    public void run(final DropwizardReporterExampleConfiguration configuration,
                    final Environment environment) {

        KafkaResource kafkaResource = new KafkaResource(configuration);
        environment.jersey().register(kafkaResource);

        environment.lifecycle().addLifeCycleListener(new LifeCycle.Listener() {
            @Override
            public void lifeCycleStopping(LifeCycle event) {
                kafkaResource.close();
            }
        });
    }

    @Path("/")
    public static class KafkaResource {

        private KafkaProducer<String, String> producer;

        private final DropwizardReporterExampleConfiguration configuration;

        public KafkaResource(DropwizardReporterExampleConfiguration configuration) {
            this.configuration = configuration;
        }

        @POST
        public void produce(String booststrapServers) {
            configuration.getProducer().put("bootstrap.servers", booststrapServers);
            this.producer = new KafkaProducer<>(configuration.getProducer());
            producer.send(new ProducerRecord<>("my-topic", "key", "value"));
        }

        public void close() {
            if (producer != null) {
                producer.close();
            }
        }
    }
}
