package wow.commons.repository.impl.parsers.items;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.professions.Profession;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelSheetReader;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class EnchantSheetReader extends ExcelSheetReader {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colItemTypes = column("item_types");
	private final ExcelColumn colReqProf = column("req_prof");
	private final ExcelColumn colReqProfLvl = column("req_prof_lvl");
	private final ExcelColumn colSp = column("sp");
	private final ExcelColumn colSd = column("sd");
	private final ExcelColumn colSpShadow = column("sp_shadow");
	private final ExcelColumn colSpellCritRating = column("spell_crit_rating");
	private final ExcelColumn colSpellHitRating = column("spell_hit_rating");
	private final ExcelColumn colAllStats = column("all_stats");
	private final ExcelColumn colSta = column("sta");
	private final ExcelColumn colInt = column("int");
	private final ExcelColumn colSpi = column("spi");
	private final ExcelColumn colThreatReductionPct = column("threat reduction%");
	private final ExcelColumn colSpeedIncreasePct = column("speed increase%");
	private final ExcelColumn colShadowResist = column("shadow resist");

	private final ItemDataRepositoryImpl itemDataRepository;

	public EnchantSheetReader(String sheetName, ItemDataRepositoryImpl itemDataRepository) {
		super(sheetName);
		this.itemDataRepository = itemDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Enchant enchant = getEnchant();
		itemDataRepository.addEnchant(enchant);
	}

	private Enchant getEnchant() {
		var id = colId.getInteger();
		var name = colName.getString();
		var requiredProfession = colReqProf.getEnum(Profession::parse, null);
		var requiredProfessionLevel = colReqProfLvl.getInteger(0);
		var itemTypes = colItemTypes.getList(x -> ItemType.parse(x.trim()));

		var itemStats = getEnchantStats();
		var enchant = new Enchant(id, name, itemTypes, itemStats);

		enchant.getRestriction().setRequiredProfession(requiredProfession);
		enchant.getRestriction().setRequiredProfessionLevel(requiredProfessionLevel);

		return enchant;
	}

	private Attributes getEnchantStats() {
		var sp = colSp.getInteger(0);
		var sd = colSd.getInteger(0);
		var spShadow = colSpShadow.getInteger(0);
		var spellCritRating = colSpellCritRating.getInteger(0);
		var spellHitRating = colSpellHitRating.getInteger(0);
		var allStats = colAllStats.getInteger(0);
		var stamina = colSta.getInteger(0);
		var intellect = colInt.getInteger(0);
		var spirit = colSpi.getInteger(0);
		var threatReductionPct = colThreatReductionPct.getPercent(null);
		var speedIncreasePct = colSpeedIncreasePct.getPercent(null);
		var shadowResist = colShadowResist.getInteger(0);

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
