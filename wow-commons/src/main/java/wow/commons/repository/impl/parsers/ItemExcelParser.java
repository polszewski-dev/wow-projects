package wow.commons.repository.impl.parsers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
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

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.util.ExcelUtil.*;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class ItemExcelParser {
	private static final String XLS_FILE_PATH = "/xls/item_data.xls";

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

	public static void readFromXls(ItemDataRepositoryImpl itemDataRepository) throws IOException, InvalidFormatException {
		try (ExcelReader excelReader = new PoiExcelReader(ItemExcelParser.class.getResourceAsStream(XLS_FILE_PATH))) {
			while (excelReader.nextSheet()) {
				if (!excelReader.nextRow()) {
					continue;
				}

				switch (excelReader.getCurrentSheetName()) {
					case SHEET_ITEM_SETS:
						readItemSets(excelReader, itemDataRepository);
						break;
					case SHEET_ENCHANTS:
						readEnchants(excelReader, itemDataRepository);
						break;
					default:
						throw new IllegalArgumentException("Unknown sheet: " + excelReader.getCurrentSheetName());
				}
			}
		}
	}

	private static void readItemSets(ExcelReader excelReader, ItemDataRepositoryImpl itemDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_ITEM_SET_NAME, excelReader, header).isPresent()) {
				ItemSet itemSet = getItemSet(excelReader, header);
				itemDataRepository.addItemSet(itemSet);
			}
		}
	}

	private static ItemSet getItemSet(ExcelReader excelReader, Map<String, Integer> header) {
		var name = getString(COL_ITEM_SET_NAME, excelReader, header);
		var tier = getOptionalString(COL_ITEM_SET_TIER, excelReader, header).map(Tier::parse).orElse(null);
		var classes = getList(COL_ITEM_SET_CLASS, x -> CharacterClass.parse(x.trim()), excelReader, header);

		return new ItemSet(name, tier, classes);
	}

	private static void readEnchants(ExcelReader excelReader, ItemDataRepositoryImpl itemDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_ITEM_SET_NAME, excelReader, header).isPresent()) {
				Enchant enchant = getEnchant(excelReader, header);
				itemDataRepository.addEnchant(enchant);
			}
		}
	}

	private static Enchant getEnchant(ExcelReader excelReader, Map<String, Integer> header) {
		var id = getInteger(COL_ENCHANT_ID, excelReader, header);
		var name = getString(COL_ENCHANT_NAME, excelReader, header);
		var itemTypes = Stream.of(getString(COL_ENCHANT_ITEM_TYPES, excelReader, header).split(","))
				.map(x -> ItemType.parse(x.trim()))
				.collect(Collectors.toList());
		var itemStats = getEnchantStats(excelReader, header);

		return new Enchant(id, name, itemTypes, itemStats);
	}

	private static Attributes getEnchantStats(ExcelReader excelReader, Map<String, Integer> header) {
		var sp = getOptionalInteger(COL_ENCHANT_SP, excelReader, header).orElse(0);
		var spShadow = getOptionalInteger(COL_ENCHANT_SP_SHADOW, excelReader, header).orElse(0);
		var spellCritRating = getOptionalInteger(COL_ENCHANT_SPELL_CRIT_RATING, excelReader, header).orElse(0);
		var spellHitRating = getOptionalInteger(COL_ENCHANT_SPELL_HIT_RATING, excelReader, header).orElse(0);
		var allStats = getOptionalInteger(COL_ENCHANT_ALL_STATS, excelReader, header).orElse(0);
		var sta = getOptionalInteger(COL_ENCHANT_STA, excelReader, header).orElse(0);
		var int_ = getOptionalInteger(COL_ENCHANT_INT, excelReader, header).orElse(0);
		var spi = getOptionalInteger(COL_ENCHANT_SPI, excelReader, header).orElse(0);
		var threatReductionPct = getOptionalPercent(COL_ENCHANT_THREAT_REDUCTION_PCT, excelReader, header).orElse(null);
		var speedIncreasePct = getOptionalPercent(COL_ENCHANT_SPEED_INCREASE_PCT, excelReader, header).orElse(null);
		var shadowResist = getOptionalInteger(COL_ENCHANT_SHADOW_RESIST, excelReader, header).orElse(0);

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
