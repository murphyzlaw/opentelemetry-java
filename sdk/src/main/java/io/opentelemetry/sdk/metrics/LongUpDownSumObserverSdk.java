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

package io.opentelemetry.sdk.metrics;

import io.opentelemetry.metrics.LongUpDownSumObserver;
import io.opentelemetry.sdk.metrics.AbstractAsynchronousInstrument.AbstractLongAsynchronousInstrument;
import io.opentelemetry.sdk.metrics.common.InstrumentType;
import io.opentelemetry.sdk.metrics.common.InstrumentValueType;
import io.opentelemetry.sdk.metrics.view.Aggregations;

final class LongUpDownSumObserverSdk extends AbstractLongAsynchronousInstrument
    implements LongUpDownSumObserver {
  LongUpDownSumObserverSdk(
      InstrumentDescriptor descriptor,
      MeterProviderSharedState meterProviderSharedState,
      MeterSharedState meterSharedState) {
    super(
        descriptor,
        meterProviderSharedState,
        meterSharedState,
        new ActiveBatcher(
            Batchers.getCumulativeAllLabels(
                descriptor, meterProviderSharedState, meterSharedState, Aggregations.lastValue())));
  }

  static final class Builder
      extends AbstractAsynchronousInstrument.Builder<LongUpDownSumObserverSdk.Builder>
      implements LongUpDownSumObserver.Builder {

    Builder(
        String name,
        MeterProviderSharedState meterProviderSharedState,
        MeterSharedState meterSharedState) {
      super(name, meterProviderSharedState, meterSharedState);
    }

    @Override
    Builder getThis() {
      return this;
    }

    @Override
    public LongUpDownSumObserverSdk build() {
      return register(
          new LongUpDownSumObserverSdk(
              getInstrumentDescriptor(
                  InstrumentType.UP_DOWN_SUM_OBSERVER, InstrumentValueType.LONG),
              getMeterProviderSharedState(),
              getMeterSharedState()));
    }
  }
}
