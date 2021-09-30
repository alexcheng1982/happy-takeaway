package io.vividcode.happytakeaway.order;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import java.util.List;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class MetricsConfiguration {

  @Produces
  @Singleton
  public MeterFilter configureCommonTags() {
    return MeterFilter.commonTags(List.of(Tag.of("service", "order")));
  }
}
