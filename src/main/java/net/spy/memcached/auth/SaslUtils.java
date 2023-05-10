package net.spy.memcached.auth;

import net.spy.memcached.compat.log.Logger;
import net.spy.memcached.compat.log.LoggerFactory;

import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslClientFactory;
import javax.security.sasl.SaslException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class SaslUtils {
  static final Logger log = LoggerFactory.getLogger(SaslUtils.class);

  public static SaslClient createSaslClient(
      String[] mechanisms,
      String authorizationId,
      String protocol,
      String serverName,
      Map<String, ?> props,
      CallbackHandler cbh,
      Provider[] providers) throws SaslException {
    Collection<SaslClientFactory> clientFactories = getFactories(SaslClientFactory.class, Thread.currentThread().getContextClassLoader(), providers);
    for (SaslClientFactory saslFactory : clientFactories) {
      try {
        SaslClient saslClient = saslFactory.createSaslClient(mechanisms, authorizationId, protocol, serverName, props, cbh);
        if (saslClient != null) {
          return saslClient;
        }
      } catch (Throwable t) {
        // Catch any errors that can happen when calling to a Sasl mech
        log.trace("Error while trying to obtain mechanism names supported by SaslClientFactory: " + saslFactory.getClass().getName());
      }
    }
    throw new SaslException("SaslClientFactory implementation not found");
  }

  private static <T> Collection<T> getFactories(Class<T> type, ClassLoader classLoader, Provider[] providers) {
    Set<T> factories = new LinkedHashSet<>();
    if (providers != null) {
      findFactories(type, factories, providers);
    }
    final ServiceLoader<T> loader = ServiceLoader.load(type, classLoader);
    for (T factory : loader) {
      factories.add(factory);
    }
    return factories;
  }

  private static <T> void findFactories(Class<T> type, Set<T> factories, Provider[] providers) {
    for (Provider currentProvider : providers) {
      for (Provider.Service service : currentProvider.getServices()) {
        if (type.getSimpleName().equals(service.getType())) {
          try {
            factories.add((T) service.newInstance(null));
          } catch (NoSuchAlgorithmException e) {
            log.debug("Could not add service " + service, e);
          }
        }
      }
    }
  }
}
