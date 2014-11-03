package com.github.dandelion.core.asset;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import com.github.dandelion.core.utils.AssetUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetUtilsTest {

	@Test
	public void should_return_a_filtered_set_when_filtering_by_type() {

		Set<Asset> assets = new LinkedHashSet<Asset>();
		assets.add(new Asset("name1", "1.0.0", AssetType.js));
		assets.add(new Asset("name2", "1.0.0", AssetType.js));
		assets.add(new Asset("name3", "1.0.0", AssetType.css));

		assertThat(AssetUtils.filtersByType(assets, AssetType.css)).extracting("name").contains("name3");
	}

	@Test
	public void should_return_a_filtered_set_when_filtering_by_asset_name() {

		Set<Asset> assets = new LinkedHashSet<Asset>();
		assets.add(new Asset("name1", "1.0.0", AssetType.js));
		assets.add(new Asset("name2", "1.0.0", AssetType.js));
		assets.add(new Asset("name3", "1.0.0", AssetType.js));

		assertThat(AssetUtils.filtersByName(assets, new String[] { "name2" })).extracting("name").contains("name1",
				"name3");
	}

	@Test
	public void should_return_the_same_set_when_filtering_by_asset_name() {

		Set<Asset> assets = new LinkedHashSet<Asset>();
		assets.add(new Asset("name1", "1.0.0", AssetType.js));
		assets.add(new Asset("name2", "1.0.0", AssetType.js));
		assets.add(new Asset("name3", "1.0.0", AssetType.js));

		assertThat(AssetUtils.filtersByName(assets, new String[] { "name5" })).extracting("name").contains("name1",
				"name2", "name3");
	}

	@Test
	public void should_return_a_filtered_set_when_filtering_by_position() {

		Set<Asset> assets = new LinkedHashSet<Asset>();
		assets.add(new Asset("name1", "1.0.0", AssetType.js, AssetDomPosition.body));
		assets.add(new Asset("name2", "1.0.0", AssetType.js, AssetDomPosition.body));
		assets.add(new Asset("name3", "1.0.0", AssetType.js, AssetDomPosition.head));

		assertThat(AssetUtils.filtersByDomPosition(assets, AssetDomPosition.head)).extracting("name").contains("name3");
	}

	@Test
	public void should_return_a_filtered_set_when_filtering_by_position_with_default_dom() {

		Set<Asset> assets = new LinkedHashSet<Asset>();
		assets.add(new Asset("name1", "1.0.0", AssetType.js));
		assets.add(new Asset("name2", "1.0.0", AssetType.js));
		assets.add(new Asset("name3", "1.0.0", AssetType.css));

		assertThat(AssetUtils.filtersByDomPosition(assets, AssetDomPosition.body)).extracting("name").contains("name1",
				"name2");
	}
}
