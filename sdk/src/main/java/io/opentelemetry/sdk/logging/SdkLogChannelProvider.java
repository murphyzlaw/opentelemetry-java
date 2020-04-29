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

package io.opentelemetry.sdk.logging;

import io.opentelemetry.logging.LogEntry;
import io.opentelemetry.logging.LogChannelProvider;
import io.opentelemetry.logging.LogChannel;
import io.opentelemetry.sdk.logging.batching.BatchResponder;
import io.opentelemetry.sdk.logging.batching.LoggingBatcher;
import io.opentelemetry.sdk.logging.batching.TimedLoggingBatcher;
import io.opentelemetry.sdk.logging.export.LoggingExporter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SdkLogChannelProvider implements LogChannelProvider, BatchResponder {
    private final Set<LoggingExporter> exporters = new HashSet<>();
    private LoggingBatcher batcher = new TimedLoggingBatcher();
    private boolean isStarted = false;

    @Override
    public LogChannel getLogChannel() {
        // FIXME: No stop!
        if (!isStarted) {
            batcher.start(this);
            isStarted = true;
        }
        return new SdkLogger();
    }

    public void setBatcher(LoggingBatcher batcher) {
        LoggingBatcher oldBatcher = this.batcher;
        this.batcher = batcher;
        oldBatcher.flush();
        oldBatcher.stop();
        this.batcher.start(this);
    }

    public void addLoggingExporter(LoggingExporter exporter) {
        exporters.add(exporter);
    }

    @Override
    public void handleLogBatch(Collection<LogEntry> entries) {
        for (LoggingExporter exporter : exporters) {
            exporter.export(entries);
        }
    }

    private class SdkLogger implements LogChannel {
        @Override
        public void send(LogEntry entry) {
            batcher.add(entry);
        }
    }
}
