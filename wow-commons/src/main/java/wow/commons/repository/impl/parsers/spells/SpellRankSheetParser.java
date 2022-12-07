package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spells.*;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellRankSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colManaCost = column("mana cost");
	private final ExcelColumn colCastTime = column("cast time");
	private final ExcelColumn colChanneled = column("channeled");
	private final ExcelColumn colMinDmg = column("min dmg");
	private final ExcelColumn colMaxDmg = column("max dmg");
	private final ExcelColumn colDotDmg = column("dot dmg");
	private final ExcelColumn colNumTicks = column("num ticks");
	private final ExcelColumn colTickInterval = column("tick interval");
	private final ExcelColumn colMinDmg2 = column("min dmg2");
	private final ExcelColumn colMaxDmg2 = column("max dmg2");
	private final ExcelColumn colAdditionalCostType = column("additional cost: type");
	private final ExcelColumn colAdditionalCostAmount = column("additional cost: amount");
	private final ExcelColumn colAdditionalCostScaled = column("additional cost: scaled");
	private final ExcelColumn colAppliedEffect = column("applied effect");
	private final ExcelColumn colAppliedEffectDuration = column("applied effect duration");

	private final SpellDataRepositoryImpl spellDataRepository;

	private final Map<SpellId, SpellInfo> spellInfoById;

	public SpellRankSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository, Map<SpellId, SpellInfo> spellInfoById) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
		this.spellInfoById = spellInfoById;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		Spell spell = getSpell();
		spellDataRepository.addSpell(spell);
	}

	private Spell getSpell() {
		var spellId = colSpell.getEnum(SpellId::parse);
		var rank = colRank.getInteger(0);

		SpellIdAndRank id = new SpellIdAndRank(spellId, rank);
		SpellInfo spellInfo = spellInfoById.get(spellId);
		Description description = getDescription(spellId.getName()).merge(spellInfo.getDescription());
		TimeRestriction timeRestriction = getTimeRestriction().merge(spellInfo.getTimeRestriction());
		CharacterRestriction characterRestriction = getRestriction().merge(spellInfo.getCharacterRestriction());
		CastInfo castInfo = getCastInfo();
		DirectDamageInfo directDamageInfo = getDirectDamageInfo();
		DotDamageInfo dotDamageInfo = getDotDamageInfo();

		return new Spell(id, timeRestriction, characterRestriction, description, spellInfo, castInfo, directDamageInfo, dotDamageInfo);
	}

	private CastInfo getCastInfo() {
		var manaCost = colManaCost.getInteger();
		var castTime = colCastTime.getDuration();
		var channeled = colChanneled.getBoolean();
		AdditionalCost additionalCost = getAdditionalCost();
		AppliedEffect appliedEffect = getAppliedEffect();
		return new CastInfo(manaCost, castTime, channeled, additionalCost, appliedEffect);
	}

	private DirectDamageInfo getDirectDamageInfo() {
		var minDmg = colMinDmg.getInteger(0);
		var maxDmg = colMaxDmg.getInteger(0);
		var minDmg2 = colMinDmg2.getInteger(0);
		var maxDmg2 = colMaxDmg2.getInteger(0);
		if (minDmg == 0 && maxDmg == 0) {
			return null;
		}
		return new DirectDamageInfo(minDmg, maxDmg, minDmg2, maxDmg2);
	}

	private DotDamageInfo getDotDamageInfo() {
		var dotDmg = colDotDmg.getInteger(0);
		var numTicks = colNumTicks.getInteger(0);
		var tickInterval = colTickInterval.getDuration(null);
		if (dotDmg == 0) {
			return null;
		}
		return new DotDamageInfo(dotDmg, numTicks, tickInterval);
	}

	private AppliedEffect getAppliedEffect() {
		var effectId = colAppliedEffect.getEnum(EffectId::parse, null);
		var duration = colAppliedEffectDuration.getDuration(null);

		if (effectId == null && duration == null) {
			return null;
		}
		if (effectId == null) {
			throw new IllegalArgumentException("No effect id");
		}
		if (duration == null) {
			duration = Duration.INFINITE;
		}

		return new AppliedEffect(effectId, duration);
	}

	private AdditionalCost getAdditionalCost() {
		var additionalCostType = colAdditionalCostType.getEnum(CostType::parse, null);

		if (additionalCostType == null) {
			return null;
		}

		var additionalCostAmount = colAdditionalCostAmount.getInteger();
		var additionalCostScaled = colAdditionalCostScaled.getBoolean();

		return new AdditionalCost(additionalCostType, additionalCostAmount, additionalCostScaled);
	}
}
