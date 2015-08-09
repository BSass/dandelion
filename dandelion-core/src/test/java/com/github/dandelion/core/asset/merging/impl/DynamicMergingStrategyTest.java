/*
 * [The "BSD licence"]
 * Copyright (c) 2013-2015 Dandelion
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
package com.github.dandelion.core.asset.merging.impl;

import static org.mockito.Matchers.any;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.github.dandelion.core.Context;
import com.github.dandelion.core.FakeApiLocator;
import com.github.dandelion.core.asset.Asset;
import com.github.dandelion.core.asset.AssetType;
import com.github.dandelion.core.asset.locator.AssetLocator;
import com.github.dandelion.core.asset.locator.impl.ApiLocator;
import com.github.dandelion.core.asset.locator.impl.RemoteLocator;
import com.github.dandelion.core.asset.locator.impl.WebappLocator;
import com.github.dandelion.core.storage.MultiAssetEntry;
import com.github.dandelion.core.storage.SingleAssetEntry;
import com.github.dandelion.core.storage.impl.MemoryAssetStorage;
import com.github.dandelion.core.util.AssetUtils;
import com.github.dandelion.core.web.DandelionServlet;
import com.github.dandelion.core.web.WebConstants;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DynamicMergingStrategyTest {

   private DynamicMergingStrategy strategy;
   private Context context;
   private MockHttpServletRequest request;

   @Before
   @SuppressWarnings("unchecked")
   public void setup() {

      Map<String, AssetLocator> fakeMap = new HashMap<String, AssetLocator>();
      fakeMap.put("api", new FakeApiLocator());
      context = mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
      when(context.getAssetLocatorsMap()).thenReturn(fakeMap);
      when(context.getAssetStorage()).thenReturn(new MemoryAssetStorage());
      when(context.getConfiguration().getAssetUrlPattern()).thenReturn(DandelionServlet.DANDELION_ASSETS_URL);

      request = new MockHttpServletRequest();
      request.setAttribute(WebConstants.DANDELION_CONTEXT_ATTRIBUTE, context);

      strategy = spy(new DynamicMergingStrategy());
      doReturn("v").when(strategy).getVersion(any(Set.class), any(HttpServletRequest.class));
      strategy.init(context);
   }

   @Test
   public void should_prepare_asset_storage_and_return_assets() {

      Set<Asset> source = new LinkedHashSet<Asset>();
      Asset a1 = new Asset("a1", "v", AssetType.js);
      a1.setStorageKey("key1");
      a1.setConfigLocationKey(WebappLocator.LOCATION_KEY);
      Asset a2 = new Asset("a2", "v", AssetType.js);
      a2.setStorageKey("key2");
      a2.setConfigLocationKey(WebappLocator.LOCATION_KEY);
      Asset a3 = new Asset("a3", "v", AssetType.js);
      a3.setStorageKey("key3");
      a3.setConfigLocationKey(RemoteLocator.LOCATION_KEY);
      a3.setVendor(true);
      source.add(a1);
      source.add(a2);
      source.add(a3);

      Set<Asset> result = strategy.prepareStorageAndGet(source, request);
      assertThat(result).hasSize(2);
      assertThat(result).extracting("name").containsExactly("a3", "merged");

      SingleAssetEntry entry = null;
      assertThat(context.getAssetStorage().size()).isEqualTo(3);
      assertThat(context.getAssetStorage().get("key1")).isInstanceOf(SingleAssetEntry.class);
      entry = (SingleAssetEntry) context.getAssetStorage().get("key1");
      assertThat(entry.getAsset()).isEqualTo(a1);
      assertThat(context.getAssetStorage().get("key2")).isInstanceOf(SingleAssetEntry.class);
      entry = (SingleAssetEntry) context.getAssetStorage().get("key2");
      assertThat(entry.getAsset()).isEqualTo(a2);

      Set<Asset> nonVendorAssets = new LinkedHashSet<Asset>(source);
      nonVendorAssets.remove(a3);
      String storageKey = AssetUtils.generateStorageKey(nonVendorAssets, request);
      assertThat(context.getAssetStorage().get(storageKey)).isInstanceOf(MultiAssetEntry.class);
      MultiAssetEntry multi = (MultiAssetEntry) context.getAssetStorage().get(storageKey);
      assertThat(multi.getAssets()).hasSize(2);
      assertThat(multi.getAssets()).extracting("name").contains("a1", "a2");
   }

   @Test
   public void should_prepare_asset_storage_and_return_api_assets() {

      Set<Asset> source = new LinkedHashSet<Asset>();
      Asset a1 = new Asset("a1", "v", AssetType.js);
      a1.setStorageKey("key1");
      a1.setConfigLocationKey(ApiLocator.LOCATION_KEY);
      Asset a2 = new Asset("a2", "v", AssetType.js);
      a2.setStorageKey("key2");
      a2.setConfigLocationKey(WebappLocator.LOCATION_KEY);
      Asset a3 = new Asset("a3", "v", AssetType.js);
      a3.setStorageKey("key3");
      a3.setConfigLocationKey(RemoteLocator.LOCATION_KEY);
      a3.setVendor(true);
      Asset a4 = new Asset("a4", "v", AssetType.js);
      a4.setStorageKey("key4");
      a4.setConfigLocationKey(WebappLocator.LOCATION_KEY);
      source.add(a1);
      source.add(a2);
      source.add(a3);
      source.add(a4);

      Set<Asset> result = strategy.prepareStorageAndGet(source, request);
      assertThat(result).hasSize(2);
      assertThat(result).extracting("name").contains("a3", "merged");

      SingleAssetEntry entry = null;
      assertThat(context.getAssetStorage().size()).isEqualTo(4);
      assertThat(context.getAssetStorage().get("key1")).isInstanceOf(SingleAssetEntry.class);
      entry = (SingleAssetEntry) context.getAssetStorage().get("key1");
      assertThat(entry.getAsset()).isEqualTo(a1);
      assertThat(entry.getContents()).isEqualTo("content");
      assertThat(context.getAssetStorage().get("key2")).isInstanceOf(SingleAssetEntry.class);
      entry = (SingleAssetEntry) context.getAssetStorage().get("key2");
      assertThat(entry.getAsset()).isEqualTo(a2);

      Set<Asset> nonVendorAssets = new LinkedHashSet<Asset>(source);
      nonVendorAssets.remove(a3);
      String storageKey = AssetUtils.generateStorageKey(nonVendorAssets, request);
      assertThat(context.getAssetStorage().get(storageKey)).isInstanceOf(MultiAssetEntry.class);
      MultiAssetEntry multi = (MultiAssetEntry) context.getAssetStorage().get(storageKey);
      assertThat(multi.getAssets()).hasSize(3);
      assertThat(multi.getAssets()).extracting("name").contains("a1", "a2", "a4");
   }
}
