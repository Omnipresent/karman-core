package com.bertramlabs.plugins.karman

import com.bertramlabs.plugins.karman.images.VirtualImageProviderInterface
import com.bertramlabs.plugins.karman.network.NetworkProviderInterface
import groovy.util.logging.Commons

@Commons
class KarmanProviders {
	static final String FACTORIES_RESOURCE_LOCATION = "META-INF/karman/providers.properties"
	static final String NETWORK_FACTORIES_RESOURCE_LOCATION = "META-INF/karman/networkProviders.properties"
	static final String IMAGE_FACTORIES_RESOURCE_LOCATION = "META-INF/karman/imageProviders.properties"
    private static Map<String,Class<StorageProviderInterface>> providers = null
	private static Map<String,Class<NetworkProviderInterface>> networkProviders = null
	private static Map<String,Class<VirtualImageProviderInterface>> imageProviders = null
    /**
     * Load the storage provider classes and adds them to the provider factory
     *
     * @param classLoader The classloader
     *
     * @return A list of provider classes
     */
    static synchronized Map<String,Class<StorageProviderInterface>> loadProviders(ClassLoader classLoader = Thread.currentThread().contextClassLoader) {

        if(providers == null) {
            def resources = classLoader.getResources(FACTORIES_RESOURCE_LOCATION)
            providers = [:]

            resources.each { URL res ->
	        	Properties providerProperties = new Properties()
            	providerProperties.load(res.openStream())

            	providerProperties.keySet().each { providerName ->
            		try {
	            		String className = providerProperties.getProperty(providerName)
	            		def cls = classLoader.loadClass(className)
	            		if(StorageProviderInterface.isAssignableFrom(cls)) {
	            			if(!providers[providerName]) {
	            				providers[providerName] = cls
	            			}
	        			} else {
	            			log.warn("Karman Storage Provider $className not registered because it does not implement the StorageProviderInterface")		
	        			}
	        		} catch(Throwable e) {
	        			log.error("Error Loading Karman Storage Provider $className: $e.message",e)
	        		} 
            	}
	        }
	    }
    	return providers
    }


	/**
	 * Load the network provider classes and adds them to the provider factory
	 *
	 * @param classLoader The classloader
	 *
	 * @return A list of network provider classes
	 */
	static synchronized Map<String,Class<NetworkProviderInterface>> loadNetworkProviders(ClassLoader classLoader = Thread.currentThread().contextClassLoader) {

		if(networkProviders == null) {
			def resources = classLoader.getResources(NETWORK_FACTORIES_RESOURCE_LOCATION)
			networkProviders = [:]

			resources.each { URL res ->
				Properties providerProperties = new Properties()
				providerProperties.load(res.openStream())

				providerProperties.keySet().each { providerName ->
					try {
						String className = providerProperties.getProperty(providerName)
						def cls = classLoader.loadClass(className)
						if(NetworkProviderInterface.isAssignableFrom(cls)) {
							if(!networkProviders[providerName]) {
								networkProviders[providerName] = cls
							}
						} else {
							log.warn("Karman Network Provider $className not registered because it does not implement the NetworkProviderInterface")
						}
					} catch(Throwable e) {
						log.error("Error Loading Karman Network Provider $className: $e.message",e)
					}
				}
			}
		}
		return networkProviders
	}

	/**
	 * Load the image provider classes and adds them to the provider factory {@link com.bertramlabs.plugins.karman.images.VirtualImageProvider}
	 *
	 * @param classLoader The classloader
	 *
	 * @return A list of image provider classes
	 */
	static synchronized Map<String,Class<VirtualImageProviderInterface>> loadImageProviders(ClassLoader classLoader = Thread.currentThread().contextClassLoader) {

		if(imageProviders == null) {
			def resources = classLoader.getResources(IMAGE_FACTORIES_RESOURCE_LOCATION)
			imageProviders = [:]

			resources.each { URL res ->
				Properties providerProperties = new Properties()
				providerProperties.load(res.openStream())

				providerProperties.keySet().each { providerName ->
					try {
						String className = providerProperties.getProperty(providerName)
						def cls = classLoader.loadClass(className)
						if(VirtualImageProviderInterface.isAssignableFrom(cls)) {
							if(!imageProviders[providerName]) {
								imageProviders[providerName] = cls
							}
						} else {
							log.warn("Karman Virtual Image Provider $className not registered because it does not implement the VirtualImageProviderInterface")
						}
					} catch(Throwable e) {
						log.error("Error Loading Karman Virtual Image Provider $className: $e.message",e)
					}
				}
			}
		}
		return imageProviders
	}
}