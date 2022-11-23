package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.effects.EffectId;
import wow.commons.model.spells.*;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.util.ExcelSheetReader;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class SpellRankSheetReader extends ExcelSheetReader {
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colLevel = column("level");
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

	public SpellRankSheetReader(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colSpell;
	}

	@Override
	protected void readSingleRow() {
		SpellRankInfo spellRankInfo = getSpellRankInfo();
		SpellInfo spellInfo = spellDataRepository.getSpellInfo(spellRankInfo.getSpellId()).orElseThrow();

		if (spellInfo.getRanks().containsKey(spellRankInfo.getRank())) {
			throw new IllegalArgumentException("Duplicate rank: " + spellRankInfo.getSpellId() + " " + spellRankInfo.getRank());
		}

		spellInfo.getRanks().put(spellRankInfo.getRank(), spellRankInfo);
	}

	private SpellRankInfo getSpellRankInfo() {
		var spellId = colSpell.getEnum(SpellId::parse);
		var rank = colRank.getInteger(0);
		var level = colLevel.getInteger();
		var manaCost = colManaCost.getInteger();
		var castTime = colCastTime.getDuration();
		var channeled = colChanneled.getBoolean();
		var minDmg = colMinDmg.getInteger(0);
		var maxDmg = colMaxDmg.getInteger(0);
		var dotDmg = colDotDmg.getInteger(0);
		var numTicks = colNumTicks.getInteger(0);
		var tickInterval = colTickInterval.getDuration(null);
		var minDmg2 = colMinDmg2.getInteger(0);
		var maxDmg2 = colMaxDmg2.getInteger(0);
		var appliedEffect = colAppliedEffect.getEnum(EffectId::parse, null);
		var appliedEffectDuration = colAppliedEffectDuration.getDuration(null);
		var additionalCost = getAdditionalCost();

		if (appliedEffect != null && appliedEffectDuration == null) {
			appliedEffectDuration = Duration.INFINITE;
		}

		return new SpellRankInfo(spellId, rank, level, manaCost, castTime, channeled, minDmg, maxDmg, minDmg2, maxDmg2, dotDmg, numTicks, tickInterval, additionalCost, appliedEffect, appliedEffectDuration);
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
