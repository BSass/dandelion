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
package com.github.dandelion.core.web.handler.debug;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.dandelion.core.config.DandelionConfig;
import com.github.dandelion.core.utils.UrlUtils;
import com.github.dandelion.core.web.WebConstants;
import com.github.dandelion.core.web.handler.HandlerContext;

/**
 * <p>
 * Abstract base class for all debug pages.
 * </p>
 * <p>
 * Also provides some utilities to help building template parameters.
 * </p>
 * 
 * @author Thibault Duchateau
 * @since 0.11.0
 */
public abstract class AbstractDebugPage implements DebugPage {

	protected HandlerContext context;

	public void initWith(HandlerContext context) {
		this.context = context;
	}

	@Override
	public Map<String, String> getParameters(HandlerContext context) {
		Map<String, String> params = new HashMap<String, String>();

		// Default variables, available in all debug pages
		params.put("%DEBUGGER_PARAM%", "?" + WebConstants.DANDELION_DEBUGGER);
		params.put("%CONTEXT%", UrlUtils.getContext(context.getRequest()).toString());

		String currentUri = UrlUtils.getCurrentUri(context.getRequest()).toString();
		String appUri = currentUri.substring(0, currentUri.indexOf(WebConstants.DANDELION_DEBUGGER) - 1);
		params.put("%CURRENT_URI%", appUri);

		StringBuilder componentMenus = new StringBuilder();
		for (DebugMenu debugMenu : context.getContext().getDebugMenuMap().values()) {
			componentMenus.append(getComponentMenu(debugMenu, appUri));
		}
		params.put("%COMPONENTS%", componentMenus.toString());

		StringBuilder actionLinks = new StringBuilder();
		actionLinks.append(li(appUri, "<span class='glyphicon glyphicon-backward'></span> Back to application"));
		params.put("%ACTION_LINKS%", actionLinks.toString());

		// Add custom parameters
		params.putAll(getCustomParameters(context));

		return params;
	}

	private StringBuilder getComponentMenu(DebugMenu debugMenu, String appUri) {
		StringBuilder menu = new StringBuilder("<ul class=\"nav nav-sidebar\">");
		menu.append("<li class=\"nav-header disabled\"><a>").append(debugMenu.getDisplayName()).append("</a></li>");
		for (DebugPage page : debugMenu.getPages()) {
			String href = getComponentDebugPageUrl(appUri, page);
			menu.append("<li");
			if (this.getClass().getSimpleName().equals(page.getClass().getSimpleName())) {
				menu.append(" class=\"active\"");
			}
			menu.append(">");
			menu.append("<a href=\"");
			menu.append(href);
			menu.append("\">");
			menu.append(page.getName());
			menu.append("</a></li>");
		}
		menu.append("</ul>");
		return menu;
	}

	private String getComponentDebugPageUrl(String appUri, DebugPage debugPage) {
		StringBuilder componentPage = new StringBuilder(appUri);
		if (componentPage.indexOf("?") == -1) {
			componentPage.append("?");
		}
		else {
			componentPage.append("&");
		}
		componentPage.append(WebConstants.DANDELION_DEBUGGER);
		componentPage.append("&ddl-debug-page=");
		componentPage.append(debugPage.getId());
		return componentPage.toString();
	}

	/**
	 * <p>
	 * Returns a {@link Map} of parameters that will be merged will the default
	 * ones coming from {@link #getParameters(HandlerContext)} and used inside
	 * the template for variables substitution.
	 * </p>
	 * 
	 * @param context
	 *            The wrapper object holding the context.
	 * 
	 * @return a {@link Map} of custom parameters.
	 */
	protected abstract Map<String, String> getCustomParameters(HandlerContext context);

	protected StringBuilder tr(Object... cols) {
		StringBuilder line = new StringBuilder();
		line.append("<tr>");
		for (Object col : cols) {
			line.append("<td>").append(col).append("</td>");
		}
		line.append("</tr>");
		return line;
	}

	protected StringBuilder tr(DandelionConfig option, Object value) {

		StringBuilder line = new StringBuilder();
		line.append("<tr>");
		line.append("<td>").append(option.getName()).append("</td>");
		line.append("<td>").append(format(value)).append("</td>");
		line.append("</tr>");
		return line;
	}

	protected StringBuilder tr(DandelionConfig option, Object value, String description) {
		StringBuilder line = new StringBuilder();
		line.append("<tr>");
		line.append("<td>").append(option.getName()).append("</td>");
		line.append("<td>").append(value).append("</td>");
		line.append("<td>").append(description).append("</td>");
		line.append("</tr>");
		return line;
	}

	protected StringBuilder li(String href, String text) {
		StringBuilder li = new StringBuilder("<li>");
		li.append("<a href=\"").append(href).append("\">");
		li.append(text);
		li.append("</a></li>");
		return li;
	}

	@SuppressWarnings("unchecked")
	private StringBuilder format(Object value) {
		StringBuilder retval = new StringBuilder();

		if (value instanceof Collection) {
			Collection<String> collectionValue = (Collection<String>) value;
			for (Iterator<String> iterator = collectionValue.iterator(); iterator.hasNext();) {
				retval.append(iterator.next());
				if (iterator.hasNext()) {
					retval.append(", ");
				}
			}
		}
		else {
			retval.append(value);
		}
		return retval;
	}
}
