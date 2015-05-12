package com.github.dandelion.core.bundle.loader.support;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockFilterConfig;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.DandelionException;
import com.github.dandelion.core.config.DandelionConfig;

// TODO move to loading strategy since required configuration is checked during the loading/parsing
@Ignore
public class BundleConsistencyCheckTest {

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Before
   public void setup() {
      System.setProperty(DandelionConfig.BUNDLE_LOCATION.getName(), "bundle-loading/json/consistency-check");
   }

   @After
   public void teardown() {
      System.clearProperty(DandelionConfig.BUNDLE_LOCATION.getName());
   }
   
   @Test
   public void should_throw_an_exception_because_of_bundle_consistency() {

      exception.expect(DandelionException.class);

      // Check that the empty bundle is logged
      exception.expectMessage(CoreMatchers.containsString("Empty bundle"));
      exception
            .expectMessage("[bundle-with-no-asset] The bundle \"bundle-with-no-asset\" is empty. You would better remove it.");

      // Check that the bundle with missing asset location(s) is logged
      exception.expectMessage(CoreMatchers.containsString("Missing asset location(s)"));
      exception.expectMessage("[bundle-with-error] The bundle contain asset with no location whereas it is required.");

      // Check that the bundle with a missing location key is logged
      exception.expectMessage(CoreMatchers.containsString("Missing location key"));
      exception
            .expectMessage("[bundle-with-error] One of the assets contained in this bundle has a location with no location key. Please correct it before continuing.");

      // Check that the bundle with an empty asset location is logged
      exception.expectMessage(CoreMatchers.containsString("Missing asset location"));
      exception
            .expectMessage("[bundle-with-error] One of the assets contained in the bundle \"bundle-with-error\" has an empty location. Please correct it before continuing.");

      exception.expectMessage(CoreMatchers.containsString("Missing extension"));
      exception.expectMessage("[missing-extension] The extension is required in all locations.");

      new Context(new MockFilterConfig());
   }
}