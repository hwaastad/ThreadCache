package org.waastad.service;

import lombok.extern.log4j.Log4j2;
import org.apache.meecrowave.Meecrowave;
import org.junit.Test;
import org.waastad.cache.MyCache;
import org.waastad.model.User;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

//@RunWith(MonoMeecrowave.Runner.class)
@Log4j2
public class CacheServiceTest {

   @Inject
   private Cache<String,User> cache;

   @Test
   public void getCaches() throws Exception {
      Meecrowave.Builder builder = new Meecrowave.Builder();
      builder.randomHttpPort();
      try (Meecrowave meecrowave = new Meecrowave(builder).bake()) {
         meecrowave.inject(this);
         log.info("Running tests...");
         WebTarget target = ClientBuilder.newClient().target(String.format("http://%s:%s", meecrowave.getConfiguration().getHost(), meecrowave.getConfiguration().getHttpPort()));
         target.path("cache").request(MediaType.APPLICATION_JSON).post(Entity.text("xxx"), String.class);
         for(int i=1;i<=10;i++) {
            target.path("cache").path("xxx").request(MediaType.APPLICATION_JSON).get();
         }
         Thread.sleep(3000);
         for(int i=1;i<=10;i++) {
            target.path("cache").path("xxx").request(MediaType.APPLICATION_JSON).get();
         }
      }
   }
}