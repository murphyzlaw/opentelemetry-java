# OpenTelemetry
[![Gitter chat][gitter-image]][gitter-url]
[![Build Status][circleci-image]][circleci-url]
[![Coverage Status][codecov-image]][codecov-url]
[![Maven Central][maven-image]][maven-url]

We hold regular meetings. See details at [community page](https://github.com/open-telemetry/community#java-sdk).

## RON'S NOTES ON BUILDING LOGGING POC
Ensure you are on branch: PROTO_ot_logging_2020_04

Run the following:

```
make init-git-submodules
./gradlew goJF
make test verify-format
./gradlew publishToMavenLocal
```

Note: when doing the main build (`make test verify-format`), you must be disconnected from VPN in order to get the remote Artifactory Maven artifacts; or you must have Walmart proxy set up to access the Artifactory Maven repo from inside Walmart.

## Overview

OpenTelemetry is a working name of a combined OpenCensus and OpenTracing
project.

This project contains the following top level components:

* [api](api/): The OpenTelemetry API.
  * [trace](api/src/main/java/io/opentelemetry/trace/): The tracing api. Includes `Tracer`, `Span` and `SpanContext`.
  * [correlationcontext](/api/src/main/java/io/opentelemetry/correlationcontext): Collection of entries in the form of key-value pairs of data that can be propagated to provide contextual information.
  * [context](api/src/main/java/io/opentelemetry/context/): In-process and inter-process propagation layer.
  * [metrics](api/src/main/java/io/opentelemetry/metrics/): Metrics layer.
* [extensions](extensions/): Additional extensions API, which are not part of the core API.
* [sdk](sdk/): The reference implementation complying to the OpenTelemetry API.
* [sdk_extensions](sdk_extensions/): Additional extensions to SDK.
* [OpenTracing shim](opentracing_shim/): A bridge layer from OpenTelemetry to the OpenTracing API.

We would love to hear from the larger community: please provide feedback proactively.

### Note about extensions

Both API and SDK extensions consist of various additional components which are excluded from the core artifacts
to keep them from growing too large.
We still aim to provide the same level of quality and guarantee for them as for the core components.
Please don't hesitate to use them if you find them useful. 

## Project setup and contribute

Please refer to the [contribution guide](CONTRIBUTING.md)
on how to setup and contribute!

## Quick Start

Please refer to the [quick start guide](QUICKSTART.md) on how use the OpenTelemetry API.

## Snapshots

Snapshots based out the `master` branch are available for `opentelemetry-api`, `opentelemetry-sdk` and the rest of the artifacts:

### Maven

```xml
  <repositories>
    <repository>
      <id>oss.sonatype.org-snapshot</id>
      <url>https://oss.jfrog.org/artifactory/oss-snapshot-local</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>io.opentelemetry</groupId>
      <artifactId>opentelemetry-api</artifactId>
      <version>0.4.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
```

### Gradle

```groovy
repositories {
	maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local' }
}

dependencies {
	compile('io.opentelemetry:opentelemetry-api:0.2.0-SNAPSHOT')
}
```

Libraries will usually only need `opentelemetry-api`, while applications
may want to use `opentelemetry-sdk`.

## Releases

OpenTelemetry Java is under active development. Releases aren't guaranteed
to conform to a specific version of the specification. Future releases will
not attempt to maintain backwards compatibility with previous releases.

The latest version of the libraries were released on March 27th, 2020.
See the [v0.3.0 alpha release](https://github.com/open-telemetry/opentelemetry-java/releases/tag/v0.3.0).

This is a **current** feature status list:

| Component                   | Version |
| --------------------------- | ------- |
| Tracing API                 | v0.3.0  |
| Tracing SDK                 | v0.3.0  |
| Metrics API                 | v0.3.0  |
| Metrics SDK                 | v0.3.0  |
| OTLP Exporter               | v0.3.0  |
| Jaeger Trace Exporter       | v0.3.0  |
| Zipkin Trace Exporter       | dev     |
| Prometheus Metrics Exporter | dev     |
| Context Propagation         | v0.3.0  |
| OpenTracing Bridge          | v0.3.0  |
| OpenCensus Bridge           | N/A     |

See the project [milestones](https://github.com/open-telemetry/opentelemetry-java/milestones)
for details on upcoming releases. The dates and features described in issues
and milestones are estimates, and subject to change.

### Summary

We plan to merge projects and pave the path for future improvements as a unified
community of tracing vendors, users and library authors who wants apps be
managed better. We are open to feedback and suggestions from all of you!

## Owners

Approvers ([@open-telemetry/java-approvers](https://github.com/orgs/open-telemetry/teams/java-approvers)):

- [Pavol Loffay](https://github.com/pavolloffay), RedHat
- [Tyler Benson](https://github.com/tylerbenson), DataDog
- [Armin Ruech](https://github.com/arminru), Dynatrace
- [Giovanni Liva](https://github.com/thisthat), Dynatrace

*Find more about the approver role in [community repository](https://github.com/open-telemetry/community/blob/master/community-membership.md#approver).*

Maintainers ([@open-telemetry/java-maintainers](https://github.com/orgs/open-telemetry/teams/java-maintainers)):

- [Bogdan Drutu](https://github.com/BogdanDrutu), Splunk
- [Carlos Alberto](https://github.com/carlosalberto), LightStep
- [John Watson](https://github.com/jkwatson), New Relic

*Find more about the maintainer role in [community repository](https://github.com/open-telemetry/community/blob/master/community-membership.md#maintainer).*

[circleci-image]: https://circleci.com/gh/open-telemetry/opentelemetry-java.svg?style=svg 
[circleci-url]: https://circleci.com/gh/open-telemetry/opentelemetry-java
[gitter-image]: https://badges.gitter.im/open-telemetry/opentelemetry-java.svg 
[gitter-url]: https://gitter.im/open-telemetry/opentelemetry-java?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[codecov-image]: https://codecov.io/gh/open-telemetry/opentelemetry-java/branch/master/graph/badge.svg
[codecov-url]: https://codecov.io/gh/open-telemetry/opentelemetry-java/branch/master/
[maven-image]: https://maven-badges.herokuapp.com/maven-central/io.opentelemetry/opentelemetry-api/badge.svg
[maven-url]: https://maven-badges.herokuapp.com/maven-central/io.opentelemetry/opentelemetry-api
