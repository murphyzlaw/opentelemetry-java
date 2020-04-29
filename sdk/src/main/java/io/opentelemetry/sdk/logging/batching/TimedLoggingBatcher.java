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

package io.opentelemetry.sdk.logging.batching;

import io.opentelemetry.logging.LogEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

public class TimedLoggingBatcher implements LoggingBatcher {
    private Collection<LogEntry> entries = new ArrayList<>();
    @Nullable private BatchResponder responder;
    private boolean isStarted = false;
    private final Timer timer = new Timer(true);
    private long interval = 1000L;

    @Override
    public void add(LogEntry entry) {
        entries.add(entry);
    }

    @Override
    public void flush() {
        // TODO: race between here and add
        Collection<LogEntry> toSend = entries;
        entries = new ArrayList<>();
        // TODO: errors, backpressure, queueing
        responder.handleLogBatch(toSend);
    }

    @Override
    public void start(BatchResponder responder) {
        synchronized(this) {
            if (!isStarted) {
                this.responder = responder;
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        flush();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, interval);
            }
            isStarted = true;
        }
    }

    @Override
    public void stop() {
        synchronized(this) {
            this.responder = null;
            timer.purge();
            isStarted = false;
        }
    }

    public static class Builder {
        private final TimedLoggingBatcher batcher;

        public Builder(BatchResponder responder) {
            this.batcher = new TimedLoggingBatcher();
        }

        public Builder withInterval(long millis) {
            this.batcher.interval = millis;
            return this;
        }

        public TimedLoggingBatcher build() {
            return batcher;
        }
    }
}

