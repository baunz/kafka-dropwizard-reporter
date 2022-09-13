package io.github.baunz.metrics.kafka.app;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotEmpty;
import java.util.Properties;

public class DropwizardReporterExampleConfiguration extends Configuration {
    @NotEmpty
    private Properties producer;

    public Properties getProducer() {
        return producer;
    }

    public void setProducer(Properties producer) {
        this.producer = producer;
    }
}
