package com.example.interopdemo.platform;

import com.example.interopdemo.Constants;

import java.util.ServiceLoader;

// Service loaders are a built-in Java feature that allow us to locate implementations
// of an interface that vary from one environment to another. In the context of
// MultiLoader we use this feature to access an abstraction in the common code that is
// swapped out for the platform specific implementation at runtime. Each loader project
// registers its implementation in src/main/resources/META-INF/services/.
public class Services {

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No platform implementation registered for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    private Services() {
    }
}
