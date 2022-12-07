package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Percent;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.spells.*;
import wow.commons.model.talents.TalentTree;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colCoeffDirect = column("coeff direct");
	private final ExcelColumn colCoeffDot = column("coeff dot");
	private final ExcelColumn colCooldown = column("cooldown");
	private final ExcelColumn colIgnoresGcd = column("ignores gcd");
	private final ExcelColumn colBolt = column("bolt");
	private final ExcelColumn colConversionFrom = column("conversion: from");
	private final ExcelColumn colConversionTo = column("conversion: to");
	private final ExcelColumn colConversionPct = column("conversion: %");
	private final ExcelColumn colRequiredEffect = column("required effect");
	private final ExcelColumn colEffectRemovedOnHit = column("effect removed on hit");
	private final ExcelColumn colBonusDamageIfUnderEffect = column("bonus dmg if under effect");
	private final ExcelColumn colDotScheme = column("dot scheme");

	private final Map<SpellId, SpellInfo> spellInfoById;

	public SpellSheetParser(String sheetName, Map<SpellId, SpellInfo> spellInfoById) {
		super(sheetName);
		this.spellInfoById = spellInfoById;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		SpellInfo spellInfo = getSpellInfo();

		if (spellInfoById.containsKey(spellInfo.getSpellId())) {
			throw new IllegalArgumentException("Duplicate: " + spellInfo.getSpellId());
		}

		spellInfoById.put(spellInfo.getSpellId(), spellInfo);
	}

	private SpellInfo getSpellInfo() {
		var spellId = colSpell.getEnum(SpellId::parse);
		var talentTree = colTree.getEnum(TalentTree::parse);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var cooldown = colCooldown.getDuration(null);
		var ignoresGCD = colIgnoresGcd.getBoolean();

		Description description = getDescription(spellId.getName());
		Restriction restriction = getRestriction();
		DamagingSpellInfo damagingSpellInfo = getDamagingSpellInfo();
		Conversion conversion = getConversion();

		return new SpellInfo(spellId, description, restriction, talentTree, spellSchool, cooldown, ignoresGCD, damagingSpellInfo, conversion);
	}

	private DamagingSpellInfo getDamagingSpellInfo() {
		var coeffDirect = colCoeffDirect.getPercent(Percent.ZERO);
		var coeffDot = colCoeffDot.getPercent(Percent.ZERO);
		var bolt = colBolt.getBoolean();
		var requiredEffect = colRequiredEffect.getEnum(EffectId::parse, null);
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(EffectId::parse, null);
		var bonusDamageIfUnderEffect = colBonusDamageIfUnderEffect.getEnum(EffectId::parse, null);
		var dotScheme = colDotScheme.getList(Integer::parseInt);

		return new DamagingSpellInfo(coeffDirect, coeffDot, bolt, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect, dotScheme);
	}

	private Conversion getConversion() {
		var conversionFrom = colConversionFrom.getEnum(Conversion.From::parse, null);
		var conversionTo = colConversionTo.getEnum(Conversion.To::parse, null);

		if (conversionFrom != null && conversionTo != null) {
			var conversionPct = colConversionPct.getPercent();
			return new Conversion(conversionFrom, conversionTo, conversionPct);
		} else if (conversionFrom != null || conversionTo != null) {
			throw new IllegalArgumentException("Conversion misses either from or to part");
		}

		return null;
	}
}
