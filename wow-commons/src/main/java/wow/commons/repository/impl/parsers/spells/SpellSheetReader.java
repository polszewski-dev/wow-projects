package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Percent;
import wow.commons.model.effects.EffectId;
import wow.commons.model.spells.Conversion;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentTree;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellSheetReader extends WowExcelSheetParser {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colCoeffDirect = column("coeff direct");
	private final ExcelColumn colCoeffDot = column("coeff dot");
	private final ExcelColumn colCooldown = column("cooldown");
	private final ExcelColumn colIgnoresGcd = column("ignores gcd");
	private final ExcelColumn colRequitedTalent = column("required talent");
	private final ExcelColumn colBolt = column("bolt");
	private final ExcelColumn colConversionFrom = column("conversion: from");
	private final ExcelColumn colConversionTo = column("conversion: to");
	private final ExcelColumn colConversionPct = column("conversion: %");
	private final ExcelColumn colRequitedEffect = column("required effect");
	private final ExcelColumn colEffectRemovedOnHit = column("effect removed on hit");
	private final ExcelColumn colBonusDamageIfUnderEffect = column("bonus dmg if under effect");
	private final ExcelColumn colDotScheme = column("dot scheme");

	private final SpellDataRepositoryImpl spellDataRepository;

	public SpellSheetReader(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		SpellInfo spellInfo = getSpellInfo();

		if (spellDataRepository.getSpellInfo(spellInfo.getSpellId()).isPresent()) {
			throw new IllegalArgumentException("Duplicate: " + spellInfo.getSpellId());
		}

		spellDataRepository.addSpellInfo(spellInfo);
	}

	private SpellInfo getSpellInfo() {
		var spellId = colSpell.getEnum(SpellId::parse);
		var talentTree = colTree.getEnum(TalentTree::parse);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var coeffDirect = colCoeffDirect.getPercent(Percent.ZERO);
		var coeffDot = colCoeffDot.getPercent(Percent.ZERO);
		var cooldown = colCooldown.getDuration(null);
		var ignoresGCD = colIgnoresGcd.getBoolean();
		var requiredTalent = colRequitedTalent.getEnum(TalentId::parse, null);
		var bolt = colBolt.getBoolean();
		var requiredEffect = colRequitedEffect.getEnum(EffectId::parse, null);
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(EffectId::parse, null);
		var bonusDamageIfUnderEffect = colBonusDamageIfUnderEffect.getEnum(EffectId::parse, null);
		var dotScheme = colDotScheme.getList(Integer::parseInt);
		var conversion = getConversion();

		return new SpellInfo(spellId, talentTree, spellSchool, coeffDirect, coeffDot, cooldown, ignoresGCD, requiredTalent, bolt, conversion, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect, dotScheme);
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
