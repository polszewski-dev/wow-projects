package wow.scraper.exporters.item;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.importers.parsers.EnchantIdParser;
import wow.scraper.importers.parsers.SpellIdParser;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parsers.tooltip.EnchantTooltipParser;

import java.io.IOException;
import java.util.*;

import static wow.scraper.model.WowheadItemCategory.ENCHANTS_PERMANENT;
import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@Slf4j
public class EnchantExporter extends AbstractSpellExporter<EnchantTooltipParser> {
	private final Map<GameVersionId, Map<Integer, JsonSpellDetails>> detailsById = new EnumMap<>(GameVersionId.class);

	@Override
	public void exportAll() throws IOException {
		builder.addEnchantHeader();

		export(ENCHANTS);
	}

	@Override
	protected EnchantTooltipParser createParser(JsonSpellDetails details, GameVersionId gameVersion) {
		return new EnchantTooltipParser(details, gameVersion, getStatPatternRepository());
	}

	@Override
	protected void exportParsedData(EnchantTooltipParser parser) {
		builder.add(parser);
	}

	@Override
	protected List<Integer> getDetailIds(WowheadSpellCategory category, GameVersionId gameVersion) throws IOException {
		assertOnlyEnchants(category);
		mergeSpellAndItemEnchants(gameVersion);

		return detailsById.get(gameVersion).keySet().stream()
				.sorted()
				.toList();
	}

	@Override
	protected Optional<JsonSpellDetails> getDetail(WowheadSpellCategory category, Integer detailId, GameVersionId gameVersion) throws IOException {
		assertOnlyEnchants(category);
		mergeSpellAndItemEnchants(gameVersion);

		return Optional.ofNullable(detailsById.get(gameVersion).get(detailId));
	}

	private void mergeSpellAndItemEnchants(GameVersionId gameVersion) throws IOException {
		if (detailsById.containsKey(gameVersion)) {
			return;
		}

		var map = detailsById.computeIfAbsent(gameVersion, x -> new HashMap<>());

		for (Integer spellId : getSpellDetailRepository().getDetailIds(gameVersion, ENCHANTS)) {
			JsonSpellDetails enchantSpell = getEnchantSpell(gameVersion, spellId);

			map.put(enchantSpell.getId(), enchantSpell);
		}

		for (Integer itemId : getItemDetailRepository().getDetailIds(gameVersion, ENCHANTS_PERMANENT)) {
			JsonItemDetails enchantItem = getEnchantItem(gameVersion, itemId);

			JsonSpellDetails enchantSpell = getCorrespondingEnchantSpell(enchantItem, gameVersion);

			if (enchantSpell != null) {
				mergeIntoEnchantSpell(enchantSpell, enchantItem);
				map.put(enchantSpell.getId(), enchantSpell);
			}
		}

		for (JsonSpellDetails enchantSpell : map.values()) {
			fetchEnchantId(enchantSpell, gameVersion);
		}
	}

	private JsonSpellDetails getEnchantSpell(GameVersionId gameVersion, Integer spellId) throws IOException {
		return getSpellDetailRepository()
				.getDetail(gameVersion, ENCHANTS, spellId)
				.orElseThrow();
	}

	private JsonItemDetails getEnchantItem(GameVersionId gameVersion, Integer itemId) throws IOException {
		return getItemDetailRepository()
				.getDetail(gameVersion, ENCHANTS_PERMANENT, itemId)
				.orElseThrow();
	}

	private JsonSpellDetails getCorrespondingEnchantSpell(JsonItemDetails enchantItem, GameVersionId gameVersion) {
		Integer spellId = getSpellId(enchantItem);

		if (spellId == null) {
			return null;
		}

		var existingSpellEnchant = detailsById.get(gameVersion).get(spellId);

		if (existingSpellEnchant != null) {
			return existingSpellEnchant;
		}

		return newSpellDetails(spellId, enchantItem);
	}

	private Integer getSpellId(JsonItemDetails enchantItem) {
		Integer spellId = new SpellIdParser(enchantItem.getHtmlTooltip()).parse();

		if (spellId == null) {
			log.error("Unable to fetch spellId for: {} [{}]", enchantItem.getId(), enchantItem.getName());
			return null;
		}

		return spellId;
	}

	private JsonSpellDetails newSpellDetails(Integer spellId, JsonItemDetails itemDetails) {
		JsonSpellDetails spellDetails = new JsonSpellDetails();

		spellDetails.setId(spellId);
		spellDetails.setName(itemDetails.getName());
		spellDetails.setLevel(itemDetails.getLevel());
		spellDetails.setQuality(itemDetails.getQuality());
		spellDetails.setHtmlTooltip(itemDetails.getHtmlTooltip());
		spellDetails.setIcon(itemDetails.getIcon());
		spellDetails.setSourceItemIds(new HashSet<>());
		return spellDetails;
	}

	private void mergeIntoEnchantSpell(JsonSpellDetails existing, JsonItemDetails itemDetails) {
		if (!existing.getName().equals(itemDetails.getName())) {
			throw new IllegalArgumentException("Different names for %s and %s".formatted(existing.getEnchantId(), itemDetails.getName()));
		}

		existing.getSourceItemIds().add(itemDetails.getId());
	}

	private void fetchEnchantId(JsonSpellDetails spellDetails, GameVersionId gameVersion) throws IOException {
		String spellHtml = getWowheadFetcher().fetchRaw(gameVersion, "spell=" + spellDetails.getId());

		Integer enchantId = new EnchantIdParser(spellHtml).parse();

		if (enchantId == null) {
			log.error("Unable to fetch enchantId for: {} [{}]", spellDetails.getId(), spellDetails.getName());
			return;
		}

		spellDetails.setEnchantId(enchantId);
	}

	private static void assertOnlyEnchants(WowheadSpellCategory category) {
		if (category != ENCHANTS) {
			throw new IllegalArgumentException();
		}
	}
}
