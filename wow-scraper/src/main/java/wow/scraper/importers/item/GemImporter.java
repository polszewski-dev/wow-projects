package wow.scraper.importers.item;

import wow.commons.model.pve.GameVersionId;

import java.io.IOException;

import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class GemImporter extends ItemBaseImporter {
	@Override
	public void importAll() throws IOException {
		if (gameVersion != GameVersionId.VANILLA) {
			fetch("items/gems/type:0:1:2:3:4:5:6:8?filter=81;7;0", GEMS);
		}
	}
}
