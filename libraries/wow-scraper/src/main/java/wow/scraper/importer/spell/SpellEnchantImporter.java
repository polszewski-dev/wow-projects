package wow.scraper.importer.spell;

import wow.scraper.model.JsonSpellDetails;

import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2023-06-24
 */
public class SpellEnchantImporter extends SpellImporter {
	public SpellEnchantImporter() {
		super(ENCHANTS);
	}

	@Override
	protected boolean isToBeSaved(JsonSpellDetails details) {
		return super.isToBeSaved(details) && details.getName().startsWith("Enchant ");
	}
}
