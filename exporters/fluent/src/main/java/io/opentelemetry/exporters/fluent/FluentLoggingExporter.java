/*
 * Copyright 2020, OpenTelemetry Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opentelemetry.exporters.fluent;

import io.opentelemetry.logging.LogEntry;
import io.opentelemetry.sdk.logging.SdkLogChannelProvider;
import io.opentelemetry.sdk.logging.export.LoggingExporter;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.fluentd.FluencyBuilderForFluentd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FluentLoggingExporter implements LoggingExporter {
    private final Fluency fluency;

    public FluentLoggingExporter(Fluency fluency) {
        this.fluency = fluency;
    }

    public static class Builder {
        private static final int DEFAULT_FLUENTD_PORT = 24224;
        private boolean sslEnabled;
        private int port;
        private String host;

        /**
         * Creates a new Builder using environment variables for configuring fluent daemon host/port
         * address.
         */
        public static Builder fromEnv() {
            Builder builder = new Builder();
            builder.port = DEFAULT_FLUENTD_PORT;
            String portString = System.getenv("FLUENT_PORT");
            if (portString != null && !portString.equals("")) {
                builder.port = Integer.parseInt(portString);
            }
            builder.host = System.getenv("FLUENT_HOST");
            if (builder.host == null || builder.host.equals("")) {
                // FIXME: Not really a great default, but good for a prototype.
                builder.host = "localhost";
            }
            builder.sslEnabled = Boolean.parseBoolean(System.getenv("FLUENT_SSL_ENABLED"));
            return builder;
        }

        /**
         * Initializes the builder and returns a FluentLoggingExporter.
         *
         * @return the FluentLoggingExporter
         */
        public FluentLoggingExporter build() {
            FluencyBuilderForFluentd builder = new FluencyBuilderForFluentd();
            builder.setSslEnabled(sslEnabled);
            return new FluentLoggingExporter(builder.build(host, port));
        }

        /**
         * Builds and sets a FluentLoggingExporter into the given log channel provider.
         * @param provider the log channel provider.
         */
        public void install(SdkLogChannelProvider provider) {
            provider.addLoggingExporter(build());
        }
    }

    @Override
    public ResultCode export(Collection<LogEntry> logs) {
        try {
            for (LogEntry entry : logs) {
                fluency.emit("", entry.getTime(), entryToMap(entry));
            }
            fluency.flush();
        } catch (Throwable t) {
            // FIXME: Prototype error handling.
            t.printStackTrace(System.err);
            return ResultCode.FAILURE;
        }
        return ResultCode.SUCCESS;
    }

    private static Map<String, Object> entryToMap(LogEntry entry) {
        Map<String, Object> fluentOut = new HashMap<>();
        Map<String, Object> severity = new HashMap<>();
        severity.put("number", entry.getSeverityNumber());
        severity.put("text", entry.getSeverityText());
        fluentOut.put("severity", severity);
        if (entry.getTraceId() != null) {
            fluentOut.put("trace_id", entry.getTraceId());
        }
        if (entry.getSpanId() != null) {
            fluentOut.put("span_id", entry.getSpanId());
        }
        fluentOut.put("attributes", entry.getAttributes());
        fluentOut.put("msg", entry.getMessage());
        return fluentOut;
    }

    @Override
    public ResultCode flush() {
        return ResultCode.SUCCESS;
    }

    @Override
    public void shutdown() {

    }
}
