package wow.commons.repository.impl.parsers;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.Tier;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class ItemExcelParser extends ExcelParser {
	private final ItemDataRepositoryImpl itemDataRepository;

	public ItemExcelParser(ItemDataRepositoryImpl itemDataRepository) {
		this.itemDataRepository = itemDataRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/item_data.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader(SHEET_ITEM_SETS, this::readItemSets, COL_ITEM_SET_NAME),
				new SheetReader(SHEET_ENCHANTS, this::readEnchants, COL_ITEM_SET_NAME)
		);
	}

	private static final String SHEET_ITEM_SETS = "item_sets";
	private static final String SHEET_ENCHANTS = "enchants";

	private static final String COL_ITEM_SET_NAME = "name";
	private static final String COL_ITEM_SET_TIER = "tier";
	private static final String COL_ITEM_SET_CLASS = "class";

	private static final String COL_ENCHANT_ID = "id";
	private static final String COL_ENCHANT_NAME = "name";
	private static final String COL_ENCHANT_ITEM_TYPES = "item_types";
	private static final String COL_ENCHANT_SP = "sp";
	private static final String COL_ENCHANT_SP_SHADOW = "sp_shadow";
	private static final String COL_ENCHANT_SPELL_CRIT_RATING = "spell_crit_rating";
	private static final String COL_ENCHANT_SPELL_HIT_RATING = "spell_hit_rating";
	private static final String COL_ENCHANT_ALL_STATS = "all_stats";
	private static final String COL_ENCHANT_STA = "sta";
	private static final String COL_ENCHANT_INT = "int";
	private static final String COL_ENCHANT_SPI = "spi";
	private static final String COL_ENCHANT_THREAT_REDUCTION_PCT = "threat reduction%";
	private static final String COL_ENCHANT_SPEED_INCREASE_PCT = "speed increase%";
	private static final String COL_ENCHANT_SHADOW_RESIST = "shadow resist";

	private void readItemSets() {
		ItemSet itemSet = getItemSet();
		itemDataRepository.addItemSet(itemSet);
	}

	private ItemSet getItemSet() {
		var name = getString(COL_ITEM_SET_NAME);
		var tier = getOptionalString(COL_ITEM_SET_TIER).map(Tier::parse).orElse(null);
		var classes = getList(COL_ITEM_SET_CLASS, x -> CharacterClass.parse(x.trim()));

		return new ItemSet(name, tier, classes);
	}

	private void readEnchants() {
		Enchant enchant = getEnchant();
		itemDataRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = getInteger(COL_ENCHANT_ID);
		var name = getString(COL_ENCHANT_NAME);
		var itemTypes = Stream.of(getString(COL_ENCHANT_ITEM_TYPES).split(","))
				.map(x -> ItemType.parse(x.trim()))
				.collect(Collectors.toList());
		var itemStats = getEnchantStats();

		return new Enchant(id, name, itemTypes, itemStats);
	}

	private Attributes getEnchantStats() {
		var sp = getOptionalInteger(COL_ENCHANT_SP).orElse(0);
		var spShadow = getOptionalInteger(COL_ENCHANT_SP_SHADOW).orElse(0);
		var spellCritRating = getOptionalInteger(COL_ENCHANT_SPELL_CRIT_RATING).orElse(0);
		var spellHitRating = getOptionalInteger(COL_ENCHANT_SPELL_HIT_RATING).orElse(0);
		var allStats = getOptionalInteger(COL_ENCHANT_ALL_STATS).orElse(0);
		var sta = getOptionalInteger(COL_ENCHANT_STA).orElse(0);
		var int_ = getOptionalInteger(COL_ENCHANT_INT).orElse(0);
		var spi = getOptionalInteger(COL_ENCHANT_SPI).orElse(0);
		var threatReductionPct = getOptionalPercent(COL_ENCHANT_THREAT_REDUCTION_PCT).orElse(null);
		var speedIncreasePct = getOptionalPercent(COL_ENCHANT_SPEED_INCREASE_PCT).orElse(null);
		var shadowResist = getOptionalInteger(COL_ENCHANT_SHADOW_RESIST).orElse(0);

		AttributesBuilder itemStats = new AttributesBuilder();

		itemStats
				.addAttribute(AttributeId.SpellDamage, sp)
				.addAttribute(AttributeId.SpellDamage, spShadow, AttributeCondition.of(SpellSchool.Shadow))
				.addAttribute(AttributeId.SpellCritRating, spellCritRating)
				.addAttribute(AttributeId.SpellHitRating, spellHitRating)
				.addAttribute(AttributeId.BaseStatsIncrease, allStats)
				.addAttribute(AttributeId.Stamina, sta)
				.addAttribute(AttributeId.Intellect, int_)
				.addAttribute(AttributeId.Spirit, spi)
				.addAttribute(AttributeId.threatReductionPct, threatReductionPct)
				.addAttribute(AttributeId.SpeedIncreasePct, speedIncreasePct)
				.addAttribute(AttributeId.Resistance, shadowResist, AttributeCondition.of(SpellSchool.Shadow))
		;
		return itemStats.toAttributes();
	}
}
