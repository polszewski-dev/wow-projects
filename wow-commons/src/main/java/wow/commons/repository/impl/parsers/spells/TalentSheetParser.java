package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.special.ProcEvent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.effects.EffectId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.model.talents.TalentTree;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	private final SpellDataRepositoryImpl spellDataRepository;

	public TalentSheetParser(String sheetName, SpellDataRepositoryImpl spellDataRepository) {
		super(sheetName);
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colTalent;
	}

	@Override
	protected void readSingleRow() {
		Talent talent = getTalent();
		Attributes talentBenefit = getTalentBenefit();
		spellDataRepository.addTalent(talent, talentBenefit);
	}

	private Talent getTalent() {
		var talentId = colTalent.getEnum(TalentId::parse);
		var rank = colRank.getInteger();
		var maxRank = colMaxRank.getInteger();
		var talentCalculatorPosition = colCalculatorPosition.getInteger();

		TalentIdAndRank id = new TalentIdAndRank(talentId, rank);
		Description description = getDescription(talentId.getName());
		Restriction restriction = getRestriction();

		return new Talent(id, description, restriction, Attributes.EMPTY, maxRank, talentCalculatorPosition);
	}

	private Attributes getTalentBenefit() {
		Attributes attributesWithoutConditions = new AttributesBuilder()
				.addAttributeList(readPrimitiveAttributes())
				.addAttribute(getStatConversion())
				.addAttribute(getSpecialAbility())
				.addAttribute(getEffectIncreasePerEffectOnTarget())
				.toAttributes();

		return attachConditions(attributesWithoutConditions);
	}

	private List<PrimitiveAttribute> readPrimitiveAttributes() {
		return Stream.of(
				getDouble(CAST_TIME_REDUCTION, "cast time reduction"),
				getDouble(COOLDOWN_REDUCTION, "cooldown reduction"),
				getDouble(COST_REDUCTION_PCT, "cost reduction%"),
				getDouble(THREAT_REDUCTION_PCT, "threat reduction%"),
				getDouble(PUSHBACK_REDUCTION_PCT, "pushback reduction%"),
				getDouble(RANGE_INCREASE_PCT, "range increase%"),
				getDouble(DURATION_INCREASE_PCT, "duration increase%"),

				getDouble(SPELL_COEFF_BONUS_PCT, "spell coeff bonus%"),
				getDouble(EFFECT_INCREASE_PCT, "effect increase%"),
				getDouble(DIRECT_DAMAGE_INCREASE_PCT, "direct damage increase%"),
				getDouble(DOT_DAMAGE_INCREASE_PCT, "dot damage increase%"),
				getDouble(CRIT_DAMAGE_INCREASE_PCT, "spell crit damage increase%"),

				getDouble(STA_INCREASE_PCT, "sta increase%"),
				getDouble(INT_INCREASE_PCT, "int increase%"),
				getDouble(SPI_INCREASE_PCT, "spi increase%"),
				getDouble(MAX_HEALTH_INCREASE_PCT, "max health increase%"),
				getDouble(MAX_MANA_INCREASE_PCT, "max mana increase%"),
				getDouble(SPELL_HIT_PCT, "spell hit increase%"),
				getDouble(SPELL_CRIT_PCT, "spell crit increase%"),
				getDouble(MELEE_CRIT_INCREASE_PCT, "melee crit increase%"),

				getDouble(PET_STA_INCREASE_PCT, "pet sta increase%"),
				getDouble(PET_INT_INCREASE_PCT, "pet int increase%"),
				getDouble(PET_SPELL_CRIT_INCREASE_PCT, "pet spell crit increase%"),
				getDouble(PET_MELEE_CRIT_INCREASE_PCT, "pet melee crit increase%"),
				getDouble(PET_MELEE_DAMAGE_INCREASE_PCT, "pet melee damage increase%"),

				getDouble(MANA_TRANSFERRED_TO_PET_PCT, "mana transferred to pet%")
		).collect(Collectors.toList());
	}

	private final ExcelColumn colConversionFrom = column("stat conversion from");
	private final ExcelColumn colConversionTo = column("stat conversion to");
	private final ExcelColumn colConversionRationPct = column("stat conversion ratio%");

	private StatConversion getStatConversion() {
		var conversionFrom = colConversionFrom.getEnum(StatConversion.Stat::parse, null);
		var conversionTo = colConversionTo.getEnum(StatConversion.Stat::parse, null);

		if (conversionFrom != null && conversionTo != null) {
			var conversionRatioPct = colConversionRationPct.getPercent();
			return new StatConversion(conversionFrom, conversionTo, conversionRatioPct, AttributeCondition.EMPTY);
		} else if (conversionFrom != null || conversionTo != null) {
			throw new IllegalArgumentException("Missing conversion from or to");
		}

		return null;
	}

	private final ExcelColumn colType = column("proc trigger");
	private final ExcelColumn colChancePct = column("proc chance%");
	private final ExcelColumn colEffect = column("proc name");
	private final ExcelColumn colDuration = column("proc duration");
	private final ExcelColumn colStacks = column("proc stacks");

	private SpecialAbility getSpecialAbility() {
		var effectId = colEffect.getEnum(EffectId::parse, null);

		if (effectId == null) {
			return null;
		}

		var type = colType.getEnum(ProcEvent::parse);
		var chancePct = colChancePct.getPercent(Percent._100);
		var duration = colDuration.getDuration(Duration.INFINITE);
		var stacks = colStacks.getInteger(1);

		return SpecialAbility.talentProc(type, chancePct, effectId, duration, stacks, null);
	}

	private final ExcelColumn colEffectTree = column("effect increase per effect on target: tree");
	private final ExcelColumn colIncreasePerEffectPct = column("effect increase per effect on target: %");
	private final ExcelColumn colMaxIncreasePct = column("effect increase per effect on target: max%");

	private EffectIncreasePerEffectOnTarget getEffectIncreasePerEffectOnTarget() {
		var effectTree = colEffectTree.getEnum(TalentTree::parse, null);

		if (effectTree == null) {
			return null;
		}

		var increasePerEffectPct = colIncreasePerEffectPct.getPercent();
		var maxIncreasePct = colMaxIncreasePct.getPercent();

		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, AttributeCondition.EMPTY);
	}
}
