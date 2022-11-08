package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
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
				new SheetReader("enchants", this::readEnchants, COL_ENCHANT_NAME)
		);
	}

	private final ExcelColumn COL_ENCHANT_ID = column("id");
	private final ExcelColumn COL_ENCHANT_NAME = column("name");
	private final ExcelColumn COL_ENCHANT_ITEM_TYPES = column("item_types");
	private final ExcelColumn COL_ENCHANT_SP = column("sp");
	private final ExcelColumn COL_ENCHANT_SP_SHADOW = column("sp_shadow");
	private final ExcelColumn COL_ENCHANT_SPELL_CRIT_RATING = column("spell_crit_rating");
	private final ExcelColumn COL_ENCHANT_SPELL_HIT_RATING = column("spell_hit_rating");
	private final ExcelColumn COL_ENCHANT_ALL_STATS = column("all_stats");
	private final ExcelColumn COL_ENCHANT_STA = column("sta");
	private final ExcelColumn COL_ENCHANT_INT = column("int");
	private final ExcelColumn COL_ENCHANT_SPI = column("spi");
	private final ExcelColumn COL_ENCHANT_THREAT_REDUCTION_PCT = column("threat reduction%");
	private final ExcelColumn COL_ENCHANT_SPEED_INCREASE_PCT = column("speed increase%");
	private final ExcelColumn COL_ENCHANT_SHADOW_RESIST = column("shadow resist");

	private void readEnchants() {
		Enchant enchant = getEnchant();
		itemDataRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = COL_ENCHANT_ID.getInteger();
		var name = COL_ENCHANT_NAME.getString();
		var itemTypes = COL_ENCHANT_ITEM_TYPES.getList(x -> ItemType.parse(x.trim()));
		var itemStats = getEnchantStats();

		return new Enchant(id, name, itemTypes, itemStats);
	}

	private Attributes getEnchantStats() {
		var sp = COL_ENCHANT_SP.getInteger(0);
		var spShadow = COL_ENCHANT_SP_SHADOW.getInteger(0);
		var spellCritRating = COL_ENCHANT_SPELL_CRIT_RATING.getInteger(0);
		var spellHitRating = COL_ENCHANT_SPELL_HIT_RATING.getInteger(0);
		var allStats = COL_ENCHANT_ALL_STATS.getInteger(0);
		var sta = COL_ENCHANT_STA.getInteger(0);
		var int_ = COL_ENCHANT_INT.getInteger(0);
		var spi = COL_ENCHANT_SPI.getInteger(0);
		var threatReductionPct = COL_ENCHANT_THREAT_REDUCTION_PCT.getPercent(null);
		var speedIncreasePct = COL_ENCHANT_SPEED_INCREASE_PCT.getPercent(null);
		var shadowResist = COL_ENCHANT_SHADOW_RESIST.getInteger(0);

		AttributesBuilder itemStats = new AttributesBuilder();

		itemStats
				.addAttribute(AttributeId.SPELL_DAMAGE, sp)
				.addAttribute(AttributeId.SPELL_DAMAGE, spShadow, AttributeCondition.of(SpellSchool.SHADOW))
				.addAttribute(AttributeId.SPELL_CRIT_RATING, spellCritRating)
				.addAttribute(AttributeId.SPELL_HIT_RATING, spellHitRating)
				.addAttribute(AttributeId.BASE_STATS_INCREASE, allStats)
				.addAttribute(AttributeId.STAMINA, sta)
				.addAttribute(AttributeId.INTELLECT, int_)
				.addAttribute(AttributeId.SPIRIT, spi)
				.addAttribute(AttributeId.THREAT_REDUCTION_PCT, threatReductionPct)
				.addAttribute(AttributeId.SPEED_INCREASE_PCT, speedIncreasePct)
				.addAttribute(AttributeId.RESISTANCE, shadowResist, AttributeCondition.of(SpellSchool.SHADOW))
		;
		return itemStats.toAttributes();
	}
}
