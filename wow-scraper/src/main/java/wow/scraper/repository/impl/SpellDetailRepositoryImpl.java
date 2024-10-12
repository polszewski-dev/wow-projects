package wow.scraper.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.GameVersionMap;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.parser.EnchantIdParser;
import wow.scraper.importer.parser.SpellIdParser;
import wow.scraper.importer.spell.SpellEnchantImporter;
import wow.scraper.importer.spell.SpellImporter;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.repository.ItemDetailRepository;
import wow.scraper.repository.SpellDetailRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.scraper.model.WowheadItemCategory.ENCHANTS_PERMANENT;
import static wow.scraper.model.WowheadSpellCategory.ENCHANTS;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
@Slf4j
public class SpellDetailRepositoryImpl extends DetailRepository<WowheadSpellCategory, JsonSpellDetails, SpellImporter> implements SpellDetailRepository {
	private final ItemDetailRepository itemDetailRepository;
	private final WowheadFetcher wowheadFetcher;

	public SpellDetailRepositoryImpl(ScraperConfig scraperConfig, ScraperDatafixes scraperDatafixes, WowheadFetcher wowheadFetcher, ItemDetailRepository itemDetailRepository) {
		super(scraperConfig, scraperDatafixes, wowheadFetcher);
		this.itemDetailRepository = itemDetailRepository;
		this.wowheadFetcher = wowheadFetcher;
	}

	@Override
	protected Stream<SpellImporter> createImporters() {
		var stream1 = Stream.of(WowheadSpellCategory.values())
				.filter(x -> x.getType() != WowheadSpellCategory.Type.ENCHANT)
				.map(SpellImporter::new);
		var stream2 = Stream.of(new SpellEnchantImporter());

		return Stream.concat(stream1, stream2);
	}

	@Override
	public Optional<JsonSpellDetails> getEnchantDetail(GameVersionId gameVersion, int enchantId) {
		mergeSpellAndItemEnchants(gameVersion);
		return enchantDetailsById.getOptional(gameVersion, enchantId);
	}

	@Override
	public List<Integer> getEnchantDetailIds(GameVersionId gameVersion) {
		mergeSpellAndItemEnchants(gameVersion);
		return enchantDetailsById.keySet(gameVersion).stream()
				.sorted()
				.toList();
	}

	private final GameVersionMap<Integer, JsonSpellDetails> enchantDetailsById = new GameVersionMap<>();

	private void mergeSpellAndItemEnchants(GameVersionId gameVersion) {
		if (enchantDetailsById.containsKey(gameVersion)) {
			return;
		}

		for (Integer spellId : this.getDetailIds(gameVersion, ENCHANTS)) {
			JsonSpellDetails enchantSpell = getEnchantSpell(gameVersion, spellId);
			enchantDetailsById.put(gameVersion, enchantSpell.getId(), enchantSpell);
		}

		for (Integer itemId : itemDetailRepository.getDetailIds(gameVersion, ENCHANTS_PERMANENT)) {
			JsonItemDetails enchantItem = getEnchantItem(gameVersion, itemId);
			JsonSpellDetails enchantSpell = getCorrespondingEnchantSpell(enchantItem, gameVersion);

			if (enchantSpell != null) {
				mergeIntoEnchantSpell(enchantSpell, enchantItem);
				enchantDetailsById.put(gameVersion, enchantSpell.getId(), enchantSpell);
			}
		}

		for (JsonSpellDetails enchantSpell : enchantDetailsById.values(gameVersion)) {
			fetchEnchantId(enchantSpell, gameVersion);
		}
	}

	private JsonSpellDetails getEnchantSpell(GameVersionId gameVersion, Integer spellId) {
		return this
				.getDetail(gameVersion, ENCHANTS, spellId)
				.orElseThrow();
	}

	private JsonItemDetails getEnchantItem(GameVersionId gameVersion, Integer itemId) {
		return itemDetailRepository
				.getDetail(gameVersion, ENCHANTS_PERMANENT, itemId)
				.orElseThrow();
	}

	private JsonSpellDetails getCorrespondingEnchantSpell(JsonItemDetails enchantItem, GameVersionId gameVersion) {
		Integer spellId = getSpellId(enchantItem);

		if (spellId == null) {
			return null;
		}

		return enchantDetailsById.getOptional(gameVersion, spellId)
				.orElseGet(() -> newSpellDetails(spellId, enchantItem, ENCHANTS));
	}

	private void mergeIntoEnchantSpell(JsonSpellDetails existing, JsonItemDetails itemDetails) {
		if (!existing.getName().equals(itemDetails.getName())) {
			throw new IllegalArgumentException("Different names for %s and %s".formatted(existing.getEnchantId(), itemDetails.getName()));
		}

		existing.getSourceItemIds().add(itemDetails.getId());
	}

	private void fetchEnchantId(JsonSpellDetails spellDetails, GameVersionId gameVersion) {
		String spellHtml = wowheadFetcher.fetchRaw(gameVersion, "spell=" + spellDetails.getId());

		Integer enchantId = new EnchantIdParser(spellHtml).parse();

		if (enchantId == null) {
			log.error("Unable to fetch enchantId for: {} [{}]", spellDetails.getId(), spellDetails.getName());
			return;
		}

		spellDetails.setEnchantId(enchantId);
	}

	private Integer getSpellId(JsonItemDetails enchantItem) {
		Integer spellId = new SpellIdParser(enchantItem.getHtmlTooltip()).parse();

		if (spellId == null) {
			log.error("Unable to fetch spellId for: {} [{}]", enchantItem.getId(), enchantItem.getName());
			return null;
		}

		return spellId;
	}

	private JsonSpellDetails newSpellDetails(Integer spellId, JsonItemDetails itemDetails, WowheadSpellCategory category) {
		JsonSpellDetails spellDetails = new JsonSpellDetails();

		spellDetails.setId(spellId);
		spellDetails.setName(itemDetails.getName());
		spellDetails.setLevel(itemDetails.getLevel());
		spellDetails.setQuality(itemDetails.getQuality());
		spellDetails.setHtmlTooltip(itemDetails.getHtmlTooltip());
		spellDetails.setIcon(itemDetails.getIcon());
		spellDetails.setSourceItemIds(new HashSet<>());
		spellDetails.setCategory(category);
		return spellDetails;
	}

	@Override
	public boolean appearedInPreviousVersion(Integer detailId, GameVersionId gameVersion, WowheadSpellCategory category) {
		if (category != ENCHANTS) {
			return super.appearedInPreviousVersion(detailId, gameVersion, category);
		}

		var previousVersion = gameVersion.getPreviousVersion();

		if (previousVersion.isEmpty()) {
			return false;
		}

		return getEnchantDetail(previousVersion.get(), detailId).isPresent();
	}
}
