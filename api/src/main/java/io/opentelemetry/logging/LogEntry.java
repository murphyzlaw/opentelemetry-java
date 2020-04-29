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

package io.opentelemetry.logging;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class LogEntry {
    public enum Level {
        UNSET(0),
        TRACE(1),
        TRACE2(2),
        TRACE3(3),
        TRACE4(4),
        DEBUG(5),
        DEBUG2(6),
        DEBUG3(7),
        DEBUG4(8),
        INFO(9),
        INFO2(10),
        INFO3(11),
        INFO4(12),
        WARN(13),
        WARN2(14),
        WARN3(15),
        WARN4(16),
        ERROR(17),
        ERROR2(18),
        ERROR3(19),
        ERROR4(20),
        FATAL(21),
        FATAL2(22),
        FATAL3(23),
        FATAL4(24);

        private final int numericLevel;
        Level(int numericLevel) {
            this.numericLevel = numericLevel;
        }

        public int asInt() {
            return numericLevel;
        }
    }

    public long getTime() {
        return time;
    }

    public int getSeverityNumber() {
        return severityNumber;
    }

    public String getSeverityText() {
        return severityText;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Nullable
    public String getTraceId() {
        return traceId;
    }

    @Nullable
    public String getSpanId() {
        return spanId;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    private long time = System.currentTimeMillis();
    private int severityNumber = 0;
    private String severityText = "";
    private final Map<String, String> attributes = new HashMap<>();

    public void setSeverityNumber(int severityNumber) {
        this.severityNumber = severityNumber;
    }

    public void setSeverityNumber(Level severity) {
        this.severityNumber = severity.asInt();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message = "no message";

    @Nullable
    private String traceId = null;

    @Nullable
    private String  spanId = null;
}
