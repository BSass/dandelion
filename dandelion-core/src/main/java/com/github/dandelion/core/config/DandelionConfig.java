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

import com.github.dandelion.core.Beta;

/**
 * <p>
 * Enum containing all configuration properties and their associated value both
 * in {@link Profile#DEFAULT_DEV_PROFILE} and
 * {@link Profile#DEFAULT_PROD_PROFILE} mode.
 * </p>
 * 
 * @author Thibault Duchateau
 * @since 0.10.0
 */
public enum DandelionConfig {

	// Asset-related configurations
	ASSET_MINIFICATION("asset.minification", "false", "true"),
	ASSET_LOCATIONS_RESOLUTION_STRATEGY("asset.locations.resolution.strategy", "webapp,webjar,jar,cdn", "webapp,webjar,jar,cdn"), 
	ASSET_PROCESSORS("asset.processors", "cssurlrewriting,jsmin,cssmin", "cssurlrewriting,jsmin,cssmin"), 
	ASSET_PROCESSORS_ENCODING("asset.processors.encoding", "UTF-8", "UTF-8"), 
	ASSET_JS_EXCLUDES("asset.js.excludes", "", ""), 
	ASSET_CSS_EXCLUDES("asset.css.excludes", "", ""),
	
	// Asset versioning
	ASSET_VERSIONING("asset.versioning", "true", "true"),
	ASSET_VERSIONING_STRATEGY("asset.versioning.strategy", "content", "content"),
	ASSET_FIXED_VERSION_TYPE("asset.fixed.version.type", "", ""),
	ASSET_FIXED_VERSION_VALUE("asset.fixed.version.value", "", ""),
	ASSET_FIXED_VERSION_DATEFORMAT("asset.fixed.version.dateformat", "yyyyMMdd", "yyyyMMdd"),

	// Caching-related configurations
	ASSET_CACHING("asset.caching", "false", "true"),
	CACHE_NAME("cache.name", "", ""),
	CACHE_ASSET_MAX_SIZE("cache.asset.max.size", "50", "50"), 
	CACHE_REQUEST_MAX_SIZE("cache.request.max.size", "50", "50"), 
	CACHE_MANAGER_NAME("cache.manager.name", "", ""), 
	CACHE_CONFIGURATION_LOCATION("cache.configuration.location", "", ""),

	// Bundle-related configurations
	BUNDLE_LOCATION("bundle.location", "", ""),
	BUNDLE_INCLUDES("bundle.includes", "", ""), 
	BUNDLE_EXCLUDES("bundle.excludes", "", ""),

	// Tooling-related configurations
	TOOL_ASSET_PRETTY_PRINTING("tool.asset.pretty.printing", "true", "false"), 
	TOOL_BUNDLE_GRAPH("tool.bundle.graph", "true", "false"), 
	TOOL_BUNDLE_RELOADING("tool.bundle.reloading", "true", "false"),
	
	// Moniroting configurations
	@Beta MONITORING_JMX("monitoring.jmx", "false", "false"),
	
	// Misc configurations
	OVERRIDE_SERVLET3("override.servlet3", "", "");
	
	/**
	 * The configuration name.
	 */
	private String propertyName;
	
	/**
	 * The default value to be used if the {@link Profile#DEFAULT_DEV_PROFILE}
	 * is activated or if a custom profile is activated but the corresponding
	 * configuration is not specified.
	 */
	private String defaultDevValue;
	
	/**
	 * The default value to be used if the {@link Profile#DEFAULT_PROD_PROFILE}
	 * is activated.
	 */
	private String defaultProdValue;

	private DandelionConfig(String propertyName, String defaultDevValue, String defaultProdValue) {
		this.propertyName = propertyName;
		this.defaultDevValue = defaultDevValue;
		this.defaultProdValue = defaultProdValue;
	}

	public String getName() {
		return propertyName;
	}

	public String defaultDevValue() {
		return defaultDevValue;
	}

	public String defaultProdValue() {
		return defaultProdValue;
	}
}
