package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spells.*;
import wow.commons.model.talents.TalentTree;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellSheetParser extends RankedElementSheetParser<SpellId, SpellInfo> {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colCoeffDirect = column("coeff direct");
	private final ExcelColumn colCoeffDot = column("coeff dot");
	private final ExcelColumn colCooldown = column("cooldown");
	private final ExcelColumn colChanneled = column("channeled");
	private final ExcelColumn colIgnoresGcd = column("ignores gcd");
	private final ExcelColumn colTarget = column("target");
	private final ExcelColumn colBolt = column("bolt");
	private final ExcelColumn colConversionFrom = column("conversion: from");
	private final ExcelColumn colConversionTo = column("conversion: to");
	private final ExcelColumn colConversionPct = column("conversion: %");
	private final ExcelColumn colRequiredEffect = column("required effect");
	private final ExcelColumn colEffectRemovedOnHit = column("effect removed on hit");
	private final ExcelColumn colBonusDamageIfUnderEffect = column("bonus dmg if under effect");

	public SpellSheetParser(String sheetName, Map<SpellId, List<SpellInfo>> spellInfoById) {
		super(sheetName, spellInfoById);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		SpellInfo spellInfo = getSpellInfo();
		addElement(spellInfo.getId(), spellInfo);
	}

	private SpellInfo getSpellInfo() {
		var spellId = colSpell.getEnum(SpellId::parse);
		var talentTree = colTree.getEnum(TalentTree::parse);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var cooldown = colCooldown.getDuration(Duration.ZERO);
		var channeled = colChanneled.getBoolean();
		var ignoresGCD = colIgnoresGcd.getBoolean();
		var target = colTarget.getEnum(SpellTarget::parse, SpellTarget.ENEMY);

		Description description = getDescription(spellId.getName());
		TimeRestriction timeRestriction = getTimeRestriction();
		CharacterRestriction characterRestriction = getRestriction();
		DamagingSpellInfo damagingSpellInfo = getDamagingSpellInfo();
		Conversion conversion = getConversion();

		return new SpellInfo(
				spellId, description, timeRestriction, characterRestriction, talentTree, spellSchool, cooldown, channeled, ignoresGCD, target, damagingSpellInfo, conversion
		);
	}

	private DamagingSpellInfo getDamagingSpellInfo() {
		var coeffDirect = colCoeffDirect.getPercent(Percent.ZERO);
		var coeffDot = colCoeffDot.getPercent(Percent.ZERO);
		var bolt = colBolt.getBoolean();
		var requiredEffect = colRequiredEffect.getEnum(EffectId::parse, null);
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(EffectId::parse, null);
		var bonusDamageIfUnderEffect = colBonusDamageIfUnderEffect.getEnum(EffectId::parse, null);

		return new DamagingSpellInfo(coeffDirect, coeffDot, bolt, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect);
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
