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

import java.io.IOException;
import java.util.Map;

import com.github.dandelion.core.web.handler.RequestHandlerContext;

/**
 * <p>
 * Contract for all debug pages.
 * </p>
 * 
 * @author Thibault Duchateau
 * @since 0.11.0
 */
public interface DebugPage {

	/**
	 * <p>
	 * Loads the template of the debug page and returns it as a String in order
	 * to be processed.
	 * </p>
	 * 
	 * @param context
	 *            The wrapper object holding the context.
	 * @return the template as a String
	 * @throws IOException
	 *             if something goes wrong while loading the template.
	 */
	String getTemplate(RequestHandlerContext context) throws IOException;

	/**
	 * <p>
	 * Populates a {@link Map} of parameters that will be injected into the
	 * template.
	 * </p>
	 * 
	 * @param context
	 *            The wrapper object holding the context.
	 * @return a {@link Map} of parameters.
	 */
	Map<String, String> getParameters(RequestHandlerContext context);
}
