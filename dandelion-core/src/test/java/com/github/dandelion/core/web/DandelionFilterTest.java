package com.github.dandelion.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class DandelionFilterTest {

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private DandelionFilter dandelionFilter;

	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		dandelionFilter = new DandelionFilter();
	}

	@Test
	public void should_only_be_relevant_with_the_right_contentType() {
		response.setContentType("text/css");
		assertThat(dandelionFilter.isRelevant(request, new ByteArrayResponseWrapper(response))).isFalse();

		response.setContentType("application/pdf");
		assertThat(dandelionFilter.isRelevant(request, new ByteArrayResponseWrapper(response))).isFalse();

		response.setContentType("text/html");
		assertThat(dandelionFilter.isRelevant(request, new ByteArrayResponseWrapper(response))).isTrue();
	}

	@Test
	public void should_be_irrevelant_if_explicitely_disabled_by_request_parameter() {
		request.setParameter(WebConstants.DANDELION_ASSET_FILTER_STATE, "false");
		assertThat(dandelionFilter.isRelevant(request, new ByteArrayResponseWrapper(response))).isFalse();
	}

	@Test
	public void should_be_irrevelant_if_explicitely_disabled_by_request_attribute() {
		request.setAttribute(WebConstants.DANDELION_ASSET_FILTER_STATE, "false");
		assertThat(dandelionFilter.isRelevant(request, new ByteArrayResponseWrapper(response))).isFalse();
	}
}
