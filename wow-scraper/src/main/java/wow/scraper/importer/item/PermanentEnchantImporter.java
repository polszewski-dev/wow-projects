package wow.scraper.importer.item;

import static wow.scraper.model.WowheadItemCategory.ENCHANTS_PERMANENT;

/**
 * User: POlszewski
 * Date: 2023-06-24
 */
public class PermanentEnchantImporter extends ItemImporter {
	public PermanentEnchantImporter() {
		super(ENCHANTS_PERMANENT);
	}
}
