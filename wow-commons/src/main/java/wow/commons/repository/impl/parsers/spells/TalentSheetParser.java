package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.PetType;
import wow.commons.repository.impl.SpellDataRepositoryImpl;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class TalentSheetParser extends BenefitSheetParser {
	private final ExcelColumn colTalent = column("talent");
	private final ExcelColumn colRank = column("rank");
	private final ExcelColumn colMaxRank = column("max rank");
	private final ExcelColumn colCalculatorPosition = column("talent calculator position");
	private final ExcelColumn colDescription = column("description");

	private final ExcelColumn colTree = column("tree");
	private final ExcelColumn colSchool = column("school");
	private final ExcelColumn colSpell = column("spell");
	private final ExcelColumn colPet = column("pet");

	private final SpellDataRepositoryImpl spellDataRepository;

	public TalentSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;

		addColumnGroups();
	}

	private void addColumnGroups() {
		this
				.add(CAST_TIME_REDUCTION, "cast time reduction")
				.add(COOLDOWN_REDUCTION, "cooldown reduction")
				.add(COST_REDUCTION_PCT, "cost reduction%")
				.add(THREAT_REDUCTION_PCT, "threat reduction%")
				.add(PUSHBACK_REDUCTION_PCT, "pushback reduction%")
				.add(RANGE_INCREASE_PCT, "range increase%")
				.add(DURATION_INCREASE_PCT, "duration increase%")

				.add(SPELL_COEFF_BONUS_PCT, "spell coeff bonus%")
				.add(EFFECT_INCREASE_PCT, "effect increase%")
				.add(DIRECT_DAMAGE_INCREASE_PCT, "direct damage increase%")
				.add(DOT_DAMAGE_INCREASE_PCT, "dot damage increase%")
				.add(CRIT_DAMAGE_INCREASE_PCT, "spell crit damage increase%")

				.add(STA_INCREASE_PCT, "sta increase%")
				.add(INT_INCREASE_PCT, "int increase%")
				.add(SPI_INCREASE_PCT, "spi increase%")
				.add(MAX_HEALTH_INCREASE_PCT, "max health increase%")
				.add(MAX_MANA_INCREASE_PCT, "max mana increase%")
				.add(SPELL_HIT_PCT, "spell hit increase%")
				.add(SPELL_CRIT_PCT, "spell crit increase%")
				.add(MELEE_CRIT_INCREASE_PCT, "melee crit increase%")

				.add(PET_STA_INCREASE_PCT, "pet sta increase%")
				.add(PET_INT_INCREASE_PCT, "pet int increase%")
				.add(PET_SPELL_CRIT_INCREASE_PCT, "pet spell crit increase%")
				.add(PET_MELEE_CRIT_INCREASE_PCT, "pet melee crit increase%")
				.add(PET_MELEE_DAMAGE_INCREASE_PCT, "pet melee damage increase%")

				.addStatConversion(
						"stat conversion from",
						"stat conversion to",
						"stat conversion ratio%"
				)

				.addProcTrigger(
						"proc trigger",
						"proc chance%",
						"proc name",
						"proc duration",
						"proc stacks"
				)

				.addEffectIncreasePerEffectOnTarget(
						"effect increase per effect on target: tree",
						"effect increase per effect on target: %",
						"effect increase per effect on target: max%"
				)

				.add(MANA_TRANSFERRED_TO_PET_PCT, "mana transferred to pet%")
		;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		TalentInfo talentInfo = getTalentInfo();
		Attributes talentBenefit = getTalentBenefit();
		spellDataRepository.addTalent(talentInfo, talentBenefit);
	}

	private TalentInfo getTalentInfo() {
		var talentName = colTalent.getEnum(TalentId::parse);
		var rank = colRank.getInteger();
		var maxRank = colMaxRank.getInteger();
		var talentCalculatorPosition = colCalculatorPosition.getInteger();
		var description = colDescription.getString(null);

		return new TalentInfo(talentName, rank, maxRank, talentCalculatorPosition, description, Attributes.EMPTY);
	}

	private Attributes getTalentBenefit() {
		var talentTree = colTree.getEnum(TalentTree::parse, null);
		var spellSchool = colSchool.getEnum(SpellSchool::parse, null);
		var spells = colSpell.getSet(SpellId::parse);
		var petTypes = colPet.getSet(PetType::parse);

		return readAttributes(talentTree, spellSchool, spells, petTypes);
	}
}
