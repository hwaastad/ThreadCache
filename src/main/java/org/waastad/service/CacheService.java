package org.waastad.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.waastad.model.User;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
@Path("cache")
@Log4j2
public class CacheService {

   @Inject
   private Cache<String, User> cache;

   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   public Response putCache(String key) {
      String value=UUID.randomUUID().toString();
      User user = User.builder().name(value).age(RandomUtils.nextInt()).build();
      log.info("Cache Store: {} =>{}",key,user.toString());
      cache.put(key,user);
      return Response.ok(value).build();
   }

   @GET
   @Path("{key}")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCacheEntry(@PathParam("key") String key) {
      log.info("Cache Lookup: {} = {}",key,cache.get(key));
      return Response.ok(cache.get(key)).build();
   }
}
