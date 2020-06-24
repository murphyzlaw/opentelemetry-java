/*
 * Copyright 2019, OpenTelemetry Authors
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

package io.opentelemetry;

import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.DefaultContextPropagators;
import io.opentelemetry.correlationcontext.CorrelationContextManager;
import io.opentelemetry.correlationcontext.DefaultCorrelationContextManager;
import io.opentelemetry.correlationcontext.spi.CorrelationContextManagerFactory;
import io.opentelemetry.internal.Obfuscated;
import io.opentelemetry.internal.Utils;
import io.opentelemetry.logging.DefaultLogProvider;
import io.opentelemetry.logging.LogChannelProvider;
import io.opentelemetry.logging.spi.LoggingProvider;
import io.opentelemetry.metrics.DefaultMeterProvider;
import io.opentelemetry.metrics.Meter;
import io.opentelemetry.metrics.MeterProvider;
import io.opentelemetry.metrics.spi.MeterProviderFactory;
import io.opentelemetry.trace.DefaultTracerProvider;
import io.opentelemetry.trace.Tracer;
import io.opentelemetry.trace.TracerProvider;
import io.opentelemetry.trace.propagation.HttpTraceContext;
import io.opentelemetry.trace.spi.TracerProviderFactory;
import java.util.ServiceLoader;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This class provides a static global accessor for telemetry objects {@link Tracer}, {@link Meter}
 * and {@link CorrelationContextManager}.
 *
 * <p>The telemetry objects are lazy-loaded singletons resolved via {@link ServiceLoader} mechanism.
 *
 * @see TracerProvider
 * @see MeterProviderFactory
 * @see CorrelationContextManagerFactory
 */
@ThreadSafe
public final class OpenTelemetry {
  private static final Object mutex = new Object();

  @Nullable private static volatile OpenTelemetry instance;

  private final TracerProvider tracerProvider;
  private final MeterProvider meterProvider;
  private final CorrelationContextManager contextManager;
  private final LogChannelProvider loggerProvider;

  private volatile ContextPropagators propagators =
      DefaultContextPropagators.builder().addHttpTextFormat(new HttpTraceContext()).build();

  /**
   * Returns a singleton {@link TracerProvider}.
   *
   * @return registered TracerProvider or default via {@link DefaultTracerProvider#getInstance()}.
   * @throws IllegalStateException if a specified TracerProvider (via system properties) could not
   *     be found.
   * @since 0.1.0
   */
  public static TracerProvider getTracerProvider() {
    return getInstance().tracerProvider;
  }

  /**
   * Gets or creates a named tracer instance.
   *
   * <p>This is a shortcut method for <code>getTracerProvider().get(instrumentationName)</code>.
   *
   * @param instrumentationName The name of the instrumentation library, not the name of the
   *     instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
   * @return a tracer instance.
   * @since 0.5.0
   */
  public static Tracer getTracer(String instrumentationName) {
    return getTracerProvider().get(instrumentationName);
  }

  /**
   * Gets or creates a named and versioned tracer instance.
   *
   * <p>This is a shortcut method for <code>
   * getTracerProvider().get(instrumentationName, instrumentationVersion)</code>.
   *
   * @param instrumentationName The name of the instrumentation library, not the name of the
   *     instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
   * @param instrumentationVersion The version of the instrumentation library (e.g.,
   *     "semver:1.0.0").
   * @return a tracer instance.
   * @since 0.5.0
   */
  public static Tracer getTracer(String instrumentationName, String instrumentationVersion) {
    return getTracerProvider().get(instrumentationName, instrumentationVersion);
  }

  /**
   * Returns a singleton {@link MeterProvider}.
   *
   * @return registered MeterProvider or default via {@link DefaultMeterProvider#getInstance()}.
   * @throws IllegalStateException if a specified MeterProvider (via system properties) could not be
   *     found.
   * @since 0.1.0
   */
  public static MeterProvider getMeterProvider() {
    return getInstance().meterProvider;
  }

  /**
   * Gets or creates a named meter instance.
   *
   * <p>This is a shortcut method for <code>getMeterProvider().get(instrumentationName)</code>.
   *
   * @param instrumentationName The name of the instrumentation library, not the name of the
   *     instrument*ed* library.
   * @return a tracer instance.
   * @since 0.5.0
   */
  public static Meter getMeter(String instrumentationName) {
    return getMeterProvider().get(instrumentationName);
  }

  /**
   * Gets or creates a named and versioned meter instance.
   *
   * <p>This is a shortcut method for <code>
   * getMeterProvider().get(instrumentationName, instrumentationVersion)</code>.
   *
   * @param instrumentationName The name of the instrumentation library, not the name of the
   *     instrument*ed* library.
   * @param instrumentationVersion The version of the instrumentation library.
   * @return a tracer instance.
   * @since 0.5.0
   */
  public static Meter getMeter(String instrumentationName, String instrumentationVersion) {
    return getMeterProvider().get(instrumentationName, instrumentationVersion);
  }

  /**
   * Returns a singleton {@link CorrelationContextManager}.
   *
   * @return registered manager or default via {@link
   *     DefaultCorrelationContextManager#getInstance()}.
   * @throws IllegalStateException if a specified manager (via system properties) could not be
   *     found.
   * @since 0.1.0
   */
  public static CorrelationContextManager getCorrelationContextManager() {
    return getInstance().contextManager;
  }

  /**
   * Returns a {@link ContextPropagators} object, which can be used to access the set of registered
   * propagators for each supported format.
   *
   * @return registered propagators container, defaulting to a {@link ContextPropagators} object
   *     with {@link HttpTraceContext} registered.
   * @throws IllegalStateException if a specified manager (via system properties) could not be
   *     found.
   * @since 0.3.0
   */
  public static ContextPropagators getPropagators() {
    return getInstance().propagators;
  }

  /**
   * Sets the {@link ContextPropagators} object, which can be used to access the set of registered
   * propagators for each supported format.
   *
   * @param propagators the {@link ContextPropagators} object to be registered.
   * @throws IllegalStateException if a specified manager (via system properties) could not be
   *     found.
   * @throws NullPointerException if {@code propagators} is {@code null}.
   * @since 0.3.0
   */
  public static void setPropagators(ContextPropagators propagators) {
    Utils.checkNotNull(propagators, "propagators");
    getInstance().propagators = propagators;
  }

  /** Lazy loads an instance. */
  private static OpenTelemetry getInstance() {
    if (instance == null) {
      synchronized (mutex) {
        if (instance == null) {
          instance = new OpenTelemetry();
        }
      }
    }
    return instance;
  }

  private OpenTelemetry() {
    TracerProviderFactory tracerProviderFactory = loadSpi(TracerProviderFactory.class);
    this.tracerProvider =
        tracerProviderFactory != null
            ? new ObfuscatedTracerProvider(tracerProviderFactory.create())
            : DefaultTracerProvider.getInstance();

    MeterProviderFactory meterProviderFactory = loadSpi(MeterProviderFactory.class);
    meterProvider =
        meterProviderFactory != null
            ? meterProviderFactory.create()
            : DefaultMeterProvider.getInstance();
    CorrelationContextManagerFactory contextManagerProvider =
        loadSpi(CorrelationContextManagerFactory.class);
    contextManager =
        contextManagerProvider != null
            ? contextManagerProvider.create()
            : DefaultCorrelationContextManager.getInstance();
    LoggingProvider loggingProvider = loadSpi(LoggingProvider.class);
    this.loggerProvider =
        loggingProvider != null ? loggingProvider.create() : DefaultLogProvider.getInstance();
  }

  /**
   * Load provider class via {@link ServiceLoader}. A specific provider class can be requested via
   * setting a system property with FQCN.
   *
   * @param providerClass a provider class
   * @param <T> provider type
   * @return a provider or null if not found
   * @throws IllegalStateException if a specified provider is not found
   */
  @Nullable
  private static <T> T loadSpi(Class<T> providerClass) {
    String specifiedProvider = System.getProperty(providerClass.getName());
    ServiceLoader<T> providers = ServiceLoader.load(providerClass);
    for (T provider : providers) {
      if (specifiedProvider == null || specifiedProvider.equals(provider.getClass().getName())) {
        return provider;
      }
    }
    if (specifiedProvider != null) {
      throw new IllegalStateException(
          String.format("Service provider %s not found", specifiedProvider));
    }
    return null;
  }

  // for testing
  static void reset() {
    instance = null;
  }

  public static LogChannelProvider getLogChannelProvider() {
    return getInstance().loggerProvider;
  }

  /**
   * A {@link TracerProvider} wrapper that forces users to access the SDK specific implementation
   * via the SDK, instead of via the API and casting it to the SDK specific implementation.
   *
   * @see Obfuscated
   */
  @ThreadSafe
  private static class ObfuscatedTracerProvider
      implements TracerProvider, Obfuscated<TracerProvider> {

    private final TracerProvider delegate;

    private ObfuscatedTracerProvider(TracerProvider delegate) {
      this.delegate = delegate;
    }

    @Override
    public Tracer get(String instrumentationName) {
      return delegate.get(instrumentationName);
    }

    @Override
    public Tracer get(String instrumentationName, String instrumentationVersion) {
      return delegate.get(instrumentationName, instrumentationVersion);
    }

    @Override
    public TracerProvider unobfuscate() {
      return delegate;
    }
  }
}
