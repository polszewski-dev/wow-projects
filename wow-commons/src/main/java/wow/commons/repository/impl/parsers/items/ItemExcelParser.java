package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

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
				new SheetReader("enchants", this::readEnchants, colEnchantName)
		);
	}

	private final ExcelColumn colEnchantId = column("id");
	private final ExcelColumn colEnchantName = column("name");
	private final ExcelColumn colEnchantItemTypes = column("item_types");
	private final ExcelColumn colEnchantSp = column("sp");
	private final ExcelColumn colEnchantSd = column("sd");
	private final ExcelColumn colEnchantSpShadow = column("sp_shadow");
	private final ExcelColumn colEnchantSpellCritRating = column("spell_crit_rating");
	private final ExcelColumn colEnchantSpellHitRating = column("spell_hit_rating");
	private final ExcelColumn colEnchantAllStats = column("all_stats");
	private final ExcelColumn colEnchantSta = column("sta");
	private final ExcelColumn colEnchantInt = column("int");
	private final ExcelColumn colEnchantSpi = column("spi");
	private final ExcelColumn colEnchantThreatReductionPct = column("threat reduction%");
	private final ExcelColumn colEnchantSpeedIncreasePct = column("speed increase%");
	private final ExcelColumn colEnchantShadowResist = column("shadow resist");

	private void readEnchants() {
		Enchant enchant = getEnchant();
		itemDataRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = colEnchantId.getInteger();
		var name = colEnchantName.getString();
		var itemTypes = colEnchantItemTypes.getList(x -> ItemType.parse(x.trim()));
		var itemStats = getEnchantStats();

		return new Enchant(id, name, itemTypes, itemStats);
	}

	private Attributes getEnchantStats() {
		var sp = colEnchantSp.getInteger(0);
		var sd = colEnchantSd.getInteger(0);
		var spShadow = colEnchantSpShadow.getInteger(0);
		var spellCritRating = colEnchantSpellCritRating.getInteger(0);
		var spellHitRating = colEnchantSpellHitRating.getInteger(0);
		var allStats = colEnchantAllStats.getInteger(0);
		var stamina = colEnchantSta.getInteger(0);
		var intellect = colEnchantInt.getInteger(0);
		var spirit = colEnchantSpi.getInteger(0);
		var threatReductionPct = colEnchantThreatReductionPct.getPercent(null);
		var speedIncreasePct = colEnchantSpeedIncreasePct.getPercent(null);
		var shadowResist = colEnchantShadowResist.getInteger(0);

		AttributesBuilder itemStats = new AttributesBuilder();

		itemStats
				.addAttribute(SPELL_POWER, sp)
				.addAttribute(SPELL_DAMAGE, sd)
				.addAttribute(SPELL_DAMAGE, spShadow, AttributeCondition.of(SpellSchool.SHADOW))
				.addAttribute(SPELL_CRIT_RATING, spellCritRating)
				.addAttribute(SPELL_HIT_RATING, spellHitRating)
				.addAttribute(BASE_STATS_INCREASE, allStats)
				.addAttribute(STAMINA, stamina)
				.addAttribute(INTELLECT, intellect)
				.addAttribute(SPIRIT, spirit)
				.addAttribute(THREAT_REDUCTION_PCT, threatReductionPct)
				.addAttribute(SPEED_INCREASE_PCT, speedIncreasePct)
				.addAttribute(RESISTANCE, shadowResist, AttributeCondition.of(SpellSchool.SHADOW))
		;
		return itemStats.toAttributes();
	}
}
