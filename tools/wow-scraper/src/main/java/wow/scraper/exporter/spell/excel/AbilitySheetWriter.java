package wow.scraper.exporter.spell.excel;

import wow.commons.model.Percent;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.SpellType;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class AbilitySheetWriter extends AbstractSpellSheetWriter<ClassAbility> {
	public AbilitySheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		writeIdNameHeader();
		writeAbilityInfoHeader();
		writeTimeRestrictionHeader();
		setHeader(SPELL_TYPE);
		setHeader(ABILITY_CATEGORY);
		writeCostHeader();
		writeSpellInfoHeader();
	}

	@Override
	public void writeRow(ClassAbility ability) {
		writeIdName(ability);
		writeAbilityInfo(ability);
		writeTimeRestriction(ability.getTimeRestriction());
		setValue(ability.getType(), SpellType.TRIGGERED_SPELL);
		setValue(ability.getCategory());
		writeCost(ability.getCost());
		writeSpellInfo(ability);
	}

	private void writeAbilityInfoHeader() {
		setHeader(ABILITY_RANK);
		setHeader(ABILITY_TREE);
		setHeader(REQ_CLASS);
		setHeader(REQ_LEVEL);
		setHeader(REQ_RACE);
		setHeader(REQ_TALENT);
		setHeader(REQ_PET);
	}

	private void writeAbilityInfo(ClassAbility ability) {
		setValue(ability.getRank());
		setValue(ability.getTalentTree());
		setValue(ability.getCharacterRestriction().characterClassIds());
		setValue(ability.getCharacterRestriction().level());
		setValue(ability.getCharacterRestriction().raceIds());

		var talentRestriction = ability.getCharacterRestriction().talentRestriction();

		if (talentRestriction != null) {
			setValue(talentRestriction.talentId());
		}

		setValue(ability.getCharacterRestriction().activePet());
	}

	private void writeCostHeader() {
		String prefix = COST_PREFIX;
		setHeader(COST_AMOUNT, prefix);
		setHeader(COST_TYPE, prefix);
		setHeader(COST_BASE_STAT_PCT, prefix);
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(COST_REAGENT, prefix);
	}

	private void writeCost(Cost cost) {
		if (cost == null) {
			fillRemainingEmptyCols(6);
			return;
		}

		setValue(cost.amount(), 0);
		setValue(cost.resourceType(), ResourceType.MANA);
		setValue(cost.baseStatPct(), Percent.ZERO);
		setValue(cost.coefficient().value(), Percent.ZERO);
		setValue(cost.coefficient().school());
		setValue(cost.reagent());
	}
}
