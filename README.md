# kafka-dropwizard-reporter ![Build Status](https://github.com/baunz/kafka-dropwizard-reporter/actions/workflows/build.yaml/badge.svg)

This package provides a `DropwizardReporter` class that connects the
built-in metrics maintained by Kafka's client libraries with
Dropwizard Metrics 4.0+.
The Kafka metrics are added as `Gauge` instances to a Dropwizard
`MetricRegistry` instance.

If you're already using Dropwizard Metrics in your application
to serve metrics via HTTP, Graphite, StatsD, etc.,
this reporter provides an easy bridge to pass Kafka consumer,
producer, and streams metrics to those same outputs.

## Compatibility

`dropwizard-metrics` 4.0 and above.

`kafka-clients` 1.0.0 and above.

Also functions with Kafka Streams and Kafka Connect.


## Usage

### Dropwizard

First, declare a dependency on this package and on the explicit versions
of the dependencies that you want:
```xml
      <dependency>
          <groupId>io.github.baunz</groupId>
          <artifactId>kafka-dropwizard-reporter</artifactId>
          <version>2.0.0</version>
      </dependency>

      <!-- Required; you must provide metrics-core and kafka-clients;
           versions can be more recent than those shown below -->
      <dependency>
          <groupId>io.dropwizard.metrics</groupId>
          <artifactId>metrics-core</artifactId>
          <version>4.1.12.1</version>
      </dependency>
      <dependency>
          <groupId>org.apache.kafka</groupId>
          <artifactId>kafka-clients</artifactId>
          <version>3.2.1</version>
          <scope>provided</scope>
      </dependency>

      <!-- Optional; the user is expected to specify this dependency
           explicitly if DropwizardReporterGraphite is to be used -->
      <dependency>
          <groupId>io.dropwizard.metrics</groupId>
          <artifactId>metrics-graphite</artifactId>
          <version>4.1.12.1</version>
          <scope>provided</scope>
      </dependency>
```

Then, include the `DropwizardReporter` class in the properties you pass
to producers, consumers, and `KafkaStreams` applications:
```
metric.reporters=io.github.baunz.metrics.kafka.DropwizardReporter
```

That client will now automatically register all of its built-in
metrics with a Dropwizard `MetricRegistry` when it's initialized.
The registry is discovered by calling
`SharedMetricRegistries.getOrCreate("default")`,
so to direct `DropwizardReporter` to a particular registry, make
sure to call `SharedMetricRegistries.add("default", myRegistry)`
before instantiating Kafka clients if you want metrics to belong
to `myRegistry`.

For a full example of integrating Kafka client metrics in a Dropwizard
application, see the [integration test](src/test/java/io/github/baunz/metrics/kafka/DropwizardReporterIntegrationTest.java).

## Reporting to Graphite

If your application is not already handling reporting of metrics to an external
source, you can use `io.github.baunz.metrics.kafka.DropwizardReporterGraphite`
which adds instantiation of a `GraphiteReporter`.
Make sure you've declared a dependency on `metrics-graphite`, then
see <a href="configuration">Configuration</a> below.

## Configuration

The following configuration options are available:

<table class="data-table"><tbody>
<tr>
<th>Name</th>
<th>Description</th>
<th>Type</th>
<th>Default</th>
<th>Valid Values</th>
<th>Importance</th>
</tr>
<tr>
<td>metric.dropwizard.graphite.host</td><td>Destination host for the GraphiteReporter (default: localhost); only relevant for DropwizardReporterGraphite</td><td>string</td><td>localhost</td><td></td><td>low</td></tr>
<tr>
<td>metric.dropwizard.graphite.port</td><td>Destination port for the GraphiteReporter (default: 2003); only relevant for DropwizardReporterGraphite</td><td>int</td><td>2003</td><td></td><td>low</td></tr>
<tr>
<td>metric.dropwizard.graphite.prefix</td><td>Metric prefix for metrics published by the GraphiteReporter; only relevant for DropwizardReporterGraphite</td><td>string</td><td>""</td><td></td><td>low</td></tr>
<tr>
<td>metric.dropwizard.registry</td><td>Name of the dropwizard-metrics registry to use; passed to SharedMetricRegistries.getOrCreate</td><td>string</td><td>default</td><td></td><td>low</td></tr>
</tbody></table>

If you'd like to send Kafka metrics to a separate `MetricRegistry` instance,
you can pass a name as `metric.dropwizard.registry` when configuring the client.
For example, the following would end up calling
`SharedMetricRegistries.getOrCreate("kafka-metrics")`:
```
metric.reporters=io.github.baunz.metrics.kafka.DropwizardReporter
metric.dropwizard.registry=kafka-metrics
```

A configuration using Graphite reporting could be:
```
metric.reporters=io.github.baunz.metrics.kafka.DropwizardGraphiteReporter
metric.dropwizard.graphite.host=localhost
metric.dropwizard.graphite.port=2003
metric.dropwizard.graphite.prefix=mycompany.myproject
```

## Building

To build the project, you'll need to
[install Apache Maven 3](https://maven.apache.org/install.html).

Once that's installed, run the following from main directory
(where `pom.xml` lives):
```
mvn clean install
```

## Contributing

Contributions, feature requests, and bug reports are all welcome.
Feel free to [submit an issue](issues/new)
or submit a pull request to this repo.

## Changelog

Check the [releases](https://github.com/baunz/kafka-dropwizard-reporter/releases)
page for summaries of what has changed in each release.

## Also See

This project has been forked from [SimpleFinance/kafka-dropwizard-reporter](https://github.com/SimpleFinance/kafka-dropwizard-reporter).
