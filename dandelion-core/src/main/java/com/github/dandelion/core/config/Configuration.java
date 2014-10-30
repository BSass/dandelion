/*
 * [The "BSD licence"]
 * Copyright (c) 2013-2014 Dandelion
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of Dandelion nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.dandelion.core.config;

import java.util.List;
import java.util.Properties;

import javax.servlet.FilterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.utils.PropertiesUtils;
import com.github.dandelion.core.utils.StringUtils;

/**
 * <p>
 * Holds the Dandelion raw configuration initialized at server startup and must
 * be accessed through the Dandelion {@link Context}.
 * </p>
 * <p>
 * All configuration present in the {@link DandelionConfig} enum are read using
 * a particular strategy. See {@link #readConfig(DandelionConfig)}.
 * </p>
 * <p>
 * There should be only one instance of this class in the application.
 * </p>
 * 
 * @author Thibault Duchateau
 * @since 0.10.0
 */
public class Configuration {

	private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

	private FilterConfig filterConfig;
	private Properties userProperties;
	
	// Profile configurations
	private String activeProfile;
	private String activeRawProfile;

	// Asset-related configurations
	private boolean assetMinificationEnabled;
	private List<String> assetLocationsResolutionStrategy;
	private List<String> assetProcessors;
	private String assetProcessorEncoding;
	private List<String> assetJsExcludes;
	private List<String> assetCssExcludes;
	
	// Asset versioning configurations
	private boolean assetVersioning;
	private String assetVersioningStrategy;
	private String assetFixedVersionType;
	private String assetFixedVersionValue;
	private String assetFixedVersionDateFormat;

	// Caching-related configurations
	private boolean assetCachingEnabled;
	private String cacheName;
	private int cacheAssetMaxSize;
	private int cacheRequestMaxSize;
	private String cacheManagerName;
	private String cacheConfigurationLocation;

	// Bundle-related configurations
	private String bundleLocation;
	private List<String> bundleIncludes;
	private List<String> bundleExcludes;

	// Tooling-related configurations
	private boolean toolAssetPrettyPrintingEnabled;
	private boolean toolBundleGraphEnabled;
	private boolean toolBundleReloadingEnabled;

	// Monitoring configuration
	private boolean monitoringJmxEnabled;

	// Misc configurations
	private boolean servlet3Enabled;

	public Configuration(FilterConfig filterConfig, Properties userProperties, Context context) {
		this.filterConfig = filterConfig;
		this.userProperties = userProperties;

		// Dandelion profile
		this.activeProfile = Profile.getActiveProfile();
		this.activeRawProfile = Profile.getActiveRawProfile();

		if (Profile.DEFAULT_DEV_PROFILE.equals(this.activeProfile)) {
			LOG.info("===========================================");
			LOG.info("");
			LOG.info("Dandelion \"dev\" profile activated.");
			LOG.info("");
			LOG.info("===========================================");
		}

		// Bundles-related configurations
		this.bundleLocation = readConfig(DandelionConfig.BUNDLE_LOCATION);
		this.bundleIncludes = PropertiesUtils.propertyAsList(readConfig(DandelionConfig.BUNDLE_INCLUDES));
		this.bundleExcludes = PropertiesUtils.propertyAsList(readConfig(DandelionConfig.BUNDLE_EXCLUDES));

		// Assets-related configurations
		this.assetMinificationEnabled = Boolean.parseBoolean(readConfig(DandelionConfig.ASSET_MINIFICATION));
		this.assetLocationsResolutionStrategy = PropertiesUtils
				.propertyAsList(readConfig(DandelionConfig.ASSET_LOCATIONS_RESOLUTION_STRATEGY));
		this.assetProcessors = PropertiesUtils.propertyAsList(readConfig(DandelionConfig.ASSET_PROCESSORS));
		this.assetProcessorEncoding = readConfig(DandelionConfig.ASSET_PROCESSORS_ENCODING);
		this.assetJsExcludes = PropertiesUtils.propertyAsList(readConfig(DandelionConfig.ASSET_JS_EXCLUDES));
		this.assetCssExcludes = PropertiesUtils.propertyAsList(readConfig(DandelionConfig.ASSET_CSS_EXCLUDES));
		
		// Asset versioning
		this.assetVersioning = Boolean.parseBoolean(readConfig(DandelionConfig.ASSET_VERSIONING));
		this.assetVersioningStrategy = readConfig(DandelionConfig.ASSET_VERSIONING_STRATEGY);
		this.assetFixedVersionType = readConfig(DandelionConfig.ASSET_FIXED_VERSION_TYPE);
		this.assetFixedVersionValue = readConfig(DandelionConfig.ASSET_FIXED_VERSION_VALUE);
		this.assetFixedVersionDateFormat = readConfig(DandelionConfig.ASSET_FIXED_VERSION_DATEFORMAT);

		// Caching-related configurations
		this.assetCachingEnabled = Boolean.parseBoolean(readConfig(DandelionConfig.ASSET_CACHING));

		this.cacheName = readConfig(DandelionConfig.CACHE_NAME);
		try {
			this.cacheAssetMaxSize = Integer.parseInt(readConfig(DandelionConfig.CACHE_ASSET_MAX_SIZE));
		}
		catch (NumberFormatException e) {
			LOG.warn("The '{}' property is incorrectly configured. Falling back to the default value ({})",
					DandelionConfig.CACHE_ASSET_MAX_SIZE.getName(),
					DandelionConfig.CACHE_ASSET_MAX_SIZE.defaultDevValue());
			this.cacheAssetMaxSize = Integer.parseInt(DandelionConfig.CACHE_ASSET_MAX_SIZE.defaultDevValue());
		}
		try {
			this.cacheRequestMaxSize = Integer.parseInt(readConfig(DandelionConfig.CACHE_REQUEST_MAX_SIZE));
		}
		catch (NumberFormatException e) {
			LOG.warn("The '{}' property is incorrectly configured. Falling back to the default value ({})",
					DandelionConfig.CACHE_REQUEST_MAX_SIZE.getName(),
					DandelionConfig.CACHE_REQUEST_MAX_SIZE.defaultDevValue());
			this.cacheRequestMaxSize = Integer.parseInt(DandelionConfig.CACHE_REQUEST_MAX_SIZE.defaultDevValue());
		}
		this.cacheManagerName = readConfig(DandelionConfig.CACHE_MANAGER_NAME);
		this.cacheConfigurationLocation = readConfig(DandelionConfig.CACHE_CONFIGURATION_LOCATION);

		// Tooling-related configurations
		this.toolAssetPrettyPrintingEnabled = Boolean
				.parseBoolean(readConfig(DandelionConfig.TOOL_ASSET_PRETTY_PRINTING));
		this.toolBundleGraphEnabled = Boolean.parseBoolean(readConfig(DandelionConfig.TOOL_BUNDLE_GRAPH));
		this.toolBundleReloadingEnabled = Boolean.parseBoolean(readConfig(DandelionConfig.TOOL_BUNDLE_RELOADING));

		// Monitoring configurations
		this.monitoringJmxEnabled = Boolean.parseBoolean(readConfig(DandelionConfig.MONITORING_JMX));

		// Misc configuration
		String overrideServlet3 = readConfig(DandelionConfig.OVERRIDE_SERVLET3);
		if (StringUtils.isBlank(overrideServlet3) && filterConfig != null) {
			this.servlet3Enabled = filterConfig.getServletContext().getMajorVersion() == 3;
		}
		else {
			this.servlet3Enabled = Boolean.parseBoolean(overrideServlet3);
		}
	}

	/**
	 * <p>
	 * Reads the given {@link DandelionConfig} in order of priority:
	 * </p>
	 * <ol>
	 * <li>System properties have the highest precedence and thus override
	 * everything else</li>
	 * <li>Then the filter initialization parameters, coming from the
	 * {@code web.xml} file</li>
	 * <li>Then the user-defined properties, coming from the
	 * {@code dandelion_[activeProfile].properties} file, if it exists</li>
	 * <li>Finally the default "dev" value of the given {@link DandelionConfig}
	 * if the {@link Profile#DEFAULT_DEV_PROFILE} is enabled, otherwise the
	 * default "prod" value.</li>
	 * </ol>
	 * 
	 * @param config
	 *            The config to read.
	 * @return the value of the given {@link DandelionConfig}.
	 */
	public String readConfig(DandelionConfig config) {

		String retval = null;
		if (System.getProperty(config.getName()) != null) {
			retval = System.getProperty(config.getName());
		}

		if (retval == null && this.filterConfig != null) {
			retval = this.filterConfig.getInitParameter(config.getName());
		}

		if (retval == null && this.userProperties != null) {
			retval = this.userProperties.getProperty(config.getName());
		}

		if (retval == null) {
			if (Profile.DEFAULT_DEV_PROFILE.equals(this.activeProfile)) {
				retval = config.defaultDevValue();
			}
			else if (Profile.DEFAULT_PROD_PROFILE.equals(this.activeProfile)) {
				retval = config.defaultProdValue();
			}
			// Always read default dev values if not explicitely declared in the
			// configuration file
			else {
				retval = config.defaultDevValue();
			}
		}

		return StringUtils.isNotBlank(retval) ? retval.trim() : retval;
	}

	public String getActiveProfile() {
		return this.activeProfile;
	}

	public String getActiveRawProfile() {
		return this.activeRawProfile;
	}

	public void setActiveRawProfile(String activeProfile) {
		this.activeRawProfile = activeProfile;
	}

	public boolean isAssetMinificationEnabled() {
		return this.assetMinificationEnabled;
	}

	public boolean isToolAssetPrettyPrintingEnabled() {
		return this.toolAssetPrettyPrintingEnabled;
	}

	public boolean isToolBundleGraphEnabled() {
		return this.toolBundleGraphEnabled;
	}

	public boolean isToolBundleReloadingEnabled() {
		return this.toolBundleReloadingEnabled;
	}

	public void setToolAssetPrettyPrintingEnabled(boolean toolAssetPrettyPrinting) {
		this.toolAssetPrettyPrintingEnabled = toolAssetPrettyPrinting;
	}

	public void setToolBundleGraphEnabled(boolean toolBundleGraph) {
		this.toolBundleGraphEnabled = toolBundleGraph;
	}

	public void setToolBundleEnabledReloading(boolean toolBundleReloading) {
		this.toolBundleReloadingEnabled = toolBundleReloading;
	}

	public boolean isAssetCachingEnabled() {
		return this.assetCachingEnabled;
	}

	public List<String> getAssetLocationsResolutionStrategy() {
		return this.assetLocationsResolutionStrategy;
	}

	public List<String> getAssetProcessors() {
		return this.assetProcessors;
	}

	public String getAssetProcessorEncoding() {
		return this.assetProcessorEncoding;
	}

	public List<String> getAssetJsExcludes() {
		return this.assetJsExcludes;
	}

	public List<String> getAssetCssExcludes() {
		return this.assetCssExcludes;
	}

	public int getCacheAssetMaxSize() {
		return this.cacheAssetMaxSize;
	}

	public int getCacheRequestMaxSize() {
		return this.cacheRequestMaxSize;
	}

	public String getCacheManagerName() {
		return this.cacheManagerName;
	}

	public String getCacheConfigurationLocation() {
		return this.cacheConfigurationLocation;
	}

	public String getBundleLocation() {
		return this.bundleLocation;
	}

	public List<String> getBundleIncludes() {
		return this.bundleIncludes;
	}

	public List<String> getBundleExcludes() {
		return this.bundleExcludes;
	}

	public Properties getProperties() {
		return this.userProperties;
	}

	public void setProperties(Properties properties) {
		this.userProperties = properties;
	}

	public String get(String key) {
		return this.userProperties.getProperty(key);
	}

	public String get(String key, String defaultValue) {
		return this.userProperties.getProperty(key, defaultValue);
	}

	public boolean isServlet3Enabled() {
		return this.servlet3Enabled;
	}

	public void setServlet3Enabled(boolean servlet3Enabled) {
		this.servlet3Enabled = servlet3Enabled;
	}

	public boolean isMonitoringJmxEnabled() {
		return this.monitoringJmxEnabled;
	}

	public void setMonitoringJmxEnabled(boolean jmxEnabled) {
		this.monitoringJmxEnabled = jmxEnabled;
	}

	public void setAssetMinificationEnabled(boolean assetMinificationEnabled) {
		this.assetMinificationEnabled = assetMinificationEnabled;
	}

	public void setAssetLocationsResolutionStrategy(List<String> assetLocationsResolutionStrategy) {
		this.assetLocationsResolutionStrategy = assetLocationsResolutionStrategy;
	}

	public void setAssetProcessors(List<String> assetProcessors) {
		this.assetProcessors = assetProcessors;
	}

	public void setAssetProcessorEncoding(String assetProcessorEncoding) {
		this.assetProcessorEncoding = assetProcessorEncoding;
	}

	public void setAssetJsExcludes(List<String> assetJsExcludes) {
		this.assetJsExcludes = assetJsExcludes;
	}

	public void setAssetCssExcludes(List<String> assetCssExcludes) {
		this.assetCssExcludes = assetCssExcludes;
	}

	public String getAssetVersioningStrategy() {
		return assetVersioningStrategy;
	}

	public void setAssetVersioningStrategy(String assetVersioningStrategy) {
		this.assetVersioningStrategy = assetVersioningStrategy;
	}

	public String getCacheName() {
		return this.cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public void setCacheAssetMaxSize(int cacheAssetMaxSize) {
		this.cacheAssetMaxSize = cacheAssetMaxSize;
	}

	public void setCacheRequestMaxSize(int cacheRequestMaxSize) {
		this.cacheRequestMaxSize = cacheRequestMaxSize;
	}

	public void setCacheManagerName(String cacheManagerName) {
		this.cacheManagerName = cacheManagerName;
	}

	public void setCacheConfigurationLocation(String cacheConfigurationLocation) {
		this.cacheConfigurationLocation = cacheConfigurationLocation;
	}

	public void setBundleIncludes(List<String> bundleIncludes) {
		this.bundleIncludes = bundleIncludes;
	}

	public void setBundleExcludes(List<String> bundleExcludes) {
		this.bundleExcludes = bundleExcludes;
	}

	public String getAssetFixedVersionType() {
		return assetFixedVersionType;
	}

	public void setAssetFixedVersionType(String assetFixedVersionType) {
		this.assetFixedVersionType = assetFixedVersionType;
	}

	public String getAssetFixedVersionValue() {
		return assetFixedVersionValue;
	}

	public void setAssetFixedVersionValue(String assetFixedVersionValue) {
		this.assetFixedVersionValue = assetFixedVersionValue;
	}

	public String getAssetFixedVersionDateFormat() {
		return assetFixedVersionDateFormat;
	}

	public void setAssetFixedVersionDateFormat(String assetFixedVersionDateFormat) {
		this.assetFixedVersionDateFormat = assetFixedVersionDateFormat;
	}

	public boolean isAssetVersioningEnabled() {
		return assetVersioning;
	}

	public void setAssetVersioning(boolean assetVersioning) {
		this.assetVersioning = assetVersioning;
	}
	
	
}