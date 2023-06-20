package wow.scraper.importers.item;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.importers.WowheadImporter;
import wow.scraper.importers.parsers.EnchantIdParser;
import wow.scraper.importers.parsers.SpellIdParser;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.model.WowheadSpellCategory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static wow.scraper.model.WowheadItemCategory.ENCHANTS_PERMANENT;
import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public class EnchantImporter extends WowheadImporter {
	@Override
	public void importAll() throws IOException {
		fetch("spells/professions/enchanting", ENCHANTS);
		fetch("items/consumables/item-enhancements-permanent", ENCHANTS_PERMANENT);
	}

	@Override
	protected void fetch(String url, WowheadItemCategory category) throws IOException {
		new PermanentEnchantFetcher().fetch(url, category);
	}

	@Override
	protected void fetch(String url, WowheadSpellCategory category) throws IOException {
		new SpellEnchantFetcher().fetch(url, category);
	}

	private class PermanentEnchantFetcher extends ItemFetcher {
		@Override
		protected void afterSave(JsonItemDetails details) throws IOException {
			super.afterSave(details);

			Integer spellId = getSpellId(details);

			if (spellId == null) {
				return;
			}

			saveAsSpellDetail(spellId, details);
		}

		private Integer getSpellId(JsonItemDetails details) {
			Integer spellId = new SpellIdParser(details.getHtmlTooltip()).parse();

			if (spellId == null) {
				log.error("Unable to fetch spellId for: {} [{}]", details.getId(), details.getName());
				return null;
			}

			return spellId;
		}

		private void saveAsSpellDetail(Integer spellId, JsonItemDetails details) throws IOException {
			var existingSpellDetails = getSpellDetailRepository().getDetail(getGameVersion(), ENCHANTS, spellId);
			JsonSpellDetails spellDetails;

			if (existingSpellDetails.isPresent()) {
				spellDetails = existingSpellDetails.get();
				mergeSpellDetail(spellDetails, details);
			} else {
				spellDetails = newSpellDetails(spellId, details);
				fetchEnchantId(spellId, spellDetails);
			}

			getSpellDetailRepository().saveDetail(getGameVersion(), ENCHANTS, spellId, spellDetails);
		}

		private void mergeSpellDetail(JsonSpellDetails existing, JsonItemDetails itemDetails) {
			if (!existing.getName().equals(itemDetails.getName())) {
				throw new IllegalArgumentException("Different names for %s and %s".formatted(existing.getEnchantId(), itemDetails.getName()));
			}

			var sourceItemIds = new HashSet<>(existing.getSourceItemIds());
			sourceItemIds.add(itemDetails.getId());
			existing.setSourceItemIds(sourceItemIds);
		}

		private JsonSpellDetails newSpellDetails(Integer spellId, JsonItemDetails itemDetails) {
			JsonSpellDetails spellDetails = new JsonSpellDetails();

			spellDetails.setId(spellId);
			spellDetails.setName(itemDetails.getName());
			spellDetails.setLevel(itemDetails.getLevel());
			spellDetails.setQuality(itemDetails.getQuality());
			spellDetails.setHtmlTooltip(itemDetails.getHtmlTooltip());
			spellDetails.setIcon(itemDetails.getIcon());
			spellDetails.setSourceItemIds(Set.of(itemDetails.getId()));

			return spellDetails;
		}
	}

	private class SpellEnchantFetcher extends SpellFetcher {
		@Override
		protected boolean isToBeSaved(WowheadSpellCategory category, JsonSpellDetails details) {
			return super.isToBeSaved(category, details) && details.getName().startsWith("Enchant ");
		}

		@Override
		protected void beforeSave(JsonSpellDetails details) throws IOException {
			super.beforeSave(details);

			Integer spellId = details.getId();

			fetchEnchantId(spellId, details);
		}
	}

	private void fetchEnchantId(Integer spellId, JsonSpellDetails spellDetails) throws IOException {
		String spellHtml = getWowheadFetcher().fetchRaw(getGameVersion(), "spell=" + spellId);

		Integer enchantId = new EnchantIdParser(spellHtml).parse();

		if (enchantId == null) {
			log.error("Unable to fetch enchantId for: {} [{}]", spellDetails.getId(), spellDetails.getName());
			return;
		}

		spellDetails.setEnchantId(enchantId);
	}
}
