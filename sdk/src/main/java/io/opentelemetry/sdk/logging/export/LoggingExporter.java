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

package io.opentelemetry.sdk.logging.export;

import io.opentelemetry.logging.LogEntry;

import java.util.Collection;

public interface LoggingExporter {
    enum ResultCode {
        /** The export operation finished successfully. */
        SUCCESS,

        /** The export operation finished with an error. */
        FAILURE
    }

    ResultCode export(Collection<LogEntry> logs);

    ResultCode flush();

    /** Called when the associated IntervalMetricReader is shutdown. */
    void shutdown();
}
