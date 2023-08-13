package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.spells.*;
import wow.commons.model.spells.impl.SpellImpl;
import wow.commons.repository.impl.SpellRepositoryImpl;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellRankSheetParser extends RankedElementSheetParser<SpellId, SpellInfo> {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colCostAmount = column("cost: amount");
	private final ExcelColumn colCostType = column("cost: type");
	private final ExcelColumn colCostBaseStatPct = column("cost: base stat%");
	private final ExcelColumn colCostCoeff = column("cost: coeff");
	private final ExcelColumn colCastTime = column("cast time");
	private final ExcelColumn colChanneled = column("channeled");
	private final ExcelColumn colMinDmg = column("min dmg");
	private final ExcelColumn colMaxDmg = column("max dmg");
	private final ExcelColumn colDotDmg = column("dot dmg");
	private final ExcelColumn colNumTicks = column("num ticks");
	private final ExcelColumn colTickInterval = column("tick interval");
	private final ExcelColumn colMinDmg2 = column("min dmg2");
	private final ExcelColumn colMaxDmg2 = column("max dmg2");
	private final ExcelColumn colAppliedEffect = column("applied effect");
	private final ExcelColumn colAppliedEffectDuration = column("applied effect duration");

	private final SpellRepositoryImpl spellRepository;

	public SpellRankSheetParser(String sheetName, SpellRepositoryImpl spellRepository, Map<SpellId, List<SpellInfo>> spellInfoById) {
		super(sheetName, spellInfoById);
		this.spellRepository = spellRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		var spellId = colSpell.getEnum(SpellId::parse);

		for (SpellInfo spellInfo : getCorrespondingElements(spellId)) {
			Spell spell = getSpell(spellInfo);
			spellRepository.addSpell(spell);
		}
	}

	private Spell getSpell(SpellInfo spellInfo) {
		var spellId = colSpell.getEnum(SpellId::parse);
		var rank = colRank.getInteger(0);

		var id = new SpellIdAndRank(spellId, rank);
		var description = getDescription(spellId.getName()).merge(spellInfo.getDescription());
		var timeRestriction = getTimeRestriction().merge(spellInfo.getTimeRestriction());
		var characterRestriction = getRestriction().merge(spellInfo.getCharacterRestriction());
		var castInfo = getCastInfo();
		var directDamageInfo = getDirectDamageInfo();
		var dotDamageInfo = getDotDamageInfo();

		return new SpellImpl(id, timeRestriction, characterRestriction, description, spellInfo, castInfo, directDamageInfo, dotDamageInfo);
	}

	private CastInfo getCastInfo() {
		var cost = getCost();
		var castTime = colCastTime.getDuration();
		var channeled = colChanneled.getBoolean();
		var appliedEffect = getAppliedEffect();
		return new CastInfo(cost, castTime, channeled, appliedEffect);
	}

	private Cost getCost() {
		var type = colCostType.getEnum(ResourceType::parse, ResourceType.MANA);
		var amount = colCostAmount.getInteger();
		var baseStatPct = colCostBaseStatPct.getPercent(Percent.ZERO);
		var coeff = colCostCoeff.getPercent(Percent.ZERO);
		return new Cost(type, amount, baseStatPct, coeff);
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
}
