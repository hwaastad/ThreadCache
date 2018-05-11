package org.waastad.cache;

import lombok.extern.log4j.Log4j2;
import org.waastad.model.User;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Log4j2
public class CacheProducer {

   private Cache cache;

   @Produces
   public Cache<String, User> getCache() {
      log.info("Producing cache");
      return this.cache;
   }

   public void initCache(@Observes @Initialized(ApplicationScoped.class) Object init) throws Exception {
      log.info("Initialize cache");
      CachingProvider cachingProvider = Caching.getCachingProvider();
      URI uri = this.getClass().getClassLoader().getResource("cache.ccf").toURI();
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      Properties cachProperties = cachingProvider.getDefaultProperties();

      this.cache = cachingProvider.getCacheManager(uri,classLoader,cachProperties).createCache(
              "cicache",
              new MutableConfiguration<String, User>()
                      .setStatisticsEnabled(true)
                      .setManagementEnabled(true)
                      .setTypes(String.class, User.class)
                      .setStoreByValue(true)
                      //.addCacheEntryListenerConfiguration(listenerConfig)
                      .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 4)))
                      .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, 60)))
      );

   }

   public void disposeCache(@Observes @Destroyed(ApplicationScoped.class) Object init) {
      log.info("Cache disposed...");
      Caching.getCachingProvider().getCacheManager().close();
   }
}
