package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.modifiers.ProcEvent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.effects.*;
import wow.commons.model.spells.*;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.PetType;
import wow.commons.repository.impl.SpellDataRepositoryImpl;
import wow.commons.repository.impl.parsers.stats.PrimitiveAttributeSupplier;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static wow.commons.model.attributes.complex.ComplexAttributeId.*;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public class SpellExcelParser extends ExcelParser {
	private final SpellDataRepositoryImpl spellDataRepository;

	public SpellExcelParser(SpellDataRepositoryImpl spellDataRepository) {
		this.spellDataRepository = spellDataRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/spell_data.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader("spells", this::readSpells, colSpellSpell),
				new SheetReader("ranks", this::readRanks, colRankSpell),
				new SheetReader("talents", this::readTalents, colTalentTalent),
				new SheetReader("effects", this::readEffects, colEffectEffect),
				new SheetReader("buffs", this::readBuffs, colBuffName)
		);
	}

	private class BenefitAttributeColumns {
		private abstract class Column {
			final AttributeId id;

			Column(AttributeId id) {
				this.id = id;
			}

			abstract Attribute readAttribute();
		}

		private class SimpleColumn extends Column {
			final ExcelColumn name;

			SimpleColumn(String name, PrimitiveAttributeId id) {
				super(id);
				this.name = column(name);
			}

			@Override
			PrimitiveAttribute readAttribute() {
				var value = name.getDouble(0);
				return Attribute.ofNullable((PrimitiveAttributeId) id, value);
			}
		}

		private class StatConversionColumn extends Column {
			final ExcelColumn colConversionFrom;
			final ExcelColumn colConversionTo;
			final ExcelColumn colConversionRationPct;

			StatConversionColumn(String colConversionFrom, String colConversionTo, String colConversionRationPct, AttributeId id) {
				super(id);
				this.colConversionFrom = column(colConversionFrom);
				this.colConversionTo = column(colConversionTo);
				this.colConversionRationPct = column(colConversionRationPct);
			}

			@Override
			StatConversion readAttribute() {
				var conversionFrom = colConversionFrom.getEnum(StatConversion.Stat::parse, null);
				var conversionTo = colConversionTo.getEnum(StatConversion.Stat::parse, null);

				if (conversionFrom != null && conversionTo != null) {
					var conversionRatioPct = colConversionRationPct.getPercent();
					return new StatConversion(conversionFrom, conversionTo, conversionRatioPct, null);
				} else if (conversionFrom != null || conversionTo != null) {
					throw new IllegalArgumentException("Missing conversion from or to");
				}

				return null;
			}
		}

		private class ProcTriggerColumn extends Column {
			final ExcelColumn colType;
			final ExcelColumn colChancePct;
			final ExcelColumn colEffect;
			final ExcelColumn colDuration;
			final ExcelColumn colStacks;

			ProcTriggerColumn(String colType, String colChancePct, String colEffect, String colDuration, String colStacks, AttributeId id) {
				super(id);
				this.colType = column(colType);
				this.colChancePct = column(colChancePct);
				this.colEffect = column(colEffect);
				this.colDuration = column(colDuration);
				this.colStacks = column(colStacks);
			}

			@Override
			public SpecialAbility readAttribute() {
				var effectId = colEffect.getEnum(EffectId::parse, null);

				if (effectId == null) {
					return null;
				}

				var type = colType.getEnum(ProcEvent::parse);
				var chancePct = colChancePct.getPercent(Percent._100);
				var duration = colDuration.getDuration(Duration.INFINITE);
				var stacks = colStacks.getInteger(1);

				return SpecialAbility.talentProc(type, chancePct, effectId, duration, stacks);
			}
		}

		private class EffectIncreasePerEffectOnTargetColumn extends Column {
			final ExcelColumn colEffectTree;
			final ExcelColumn colIncreasePerEffectPct;
			final ExcelColumn colMaxIncreasePct;

			EffectIncreasePerEffectOnTargetColumn(String colEffectTree, String colIncreasePerEffectPct, String colMaxIncreasePct, AttributeId id) {
				super(id);
				this.colEffectTree = column(colEffectTree);
				this.colIncreasePerEffectPct = column(colIncreasePerEffectPct);
				this.colMaxIncreasePct = column(colMaxIncreasePct);
			}

			@Override
			public EffectIncreasePerEffectOnTarget readAttribute() {
				var effectTree = colEffectTree.getEnum(TalentTree::parse, null);

				if (effectTree == null) {
					return null;
				}

				var increasePerEffectPct = colIncreasePerEffectPct.getPercent();
				var maxIncreasePct = colMaxIncreasePct.getPercent();

				return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, null);
			}
		}

		private final List<Column> columns = new ArrayList<>();

		BenefitAttributeColumns add(PrimitiveAttributeId id, String name) {
			columns.add(new SimpleColumn(name, id));
			return this;
		}

		BenefitAttributeColumns addStatConversion(
				String colConversionFrom,
				String colConversionTo,
				String colConversionRationPct
		) {
			columns.add(new StatConversionColumn(colConversionFrom, colConversionTo, colConversionRationPct, STAT_CONVERSION));
			return this;
		}

		BenefitAttributeColumns addProcTrigger(
				String colType,
				String colChancePct,
				String colEffect,
				String colDuration,
				String colStacks
		) {
			columns.add(new ProcTriggerColumn(colType, colChancePct, colEffect, colDuration, colStacks, SPECIAL_ABILITIES));
			return this;
		}

		BenefitAttributeColumns addEffectIncreasePerEffectOnTarget(
				String colEffectTree,
				String colIncreasePerEffectPct,
				String colMaxIncreasePct
		) {
			columns.add(new EffectIncreasePerEffectOnTargetColumn(colEffectTree, colIncreasePerEffectPct, colMaxIncreasePct, EFFECT_INCREASE_PER_EFFECT_ON_TARGET));
			return this;
		}

		Attributes readAttributes(TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes) {
			AttributesBuilder result = new AttributesBuilder();
			for (Column column : columns) {
				Attribute attribute = column.readAttribute();
				addAttributeWithConditions(attribute, talentTree, spellSchool, spells, petTypes, result);
			}
			return result.toAttributes();
		}

		private void addAttributeWithConditions(Attribute attribute, TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes, AttributesBuilder result) {
			if (attribute == null) {
				return;
			}

			forEachPossibleCondition(talentTree, spellSchool, spells, petTypes, condition -> {
				if (attribute instanceof PrimitiveAttribute) {
					result.addAttribute((PrimitiveAttribute) attribute.attachCondition(condition));
				} else {
					result.addAttribute((ComplexAttribute) attribute.attachCondition(condition));
				}
			});
		}

		private void forEachPossibleCondition(TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes, Consumer<AttributeCondition> conditionConsumer) {
			Set<SpellId> possibleSpells = !spells.isEmpty() ? spells : Collections.singleton(null);
			Set<PetType> possiblePetTypes = !petTypes.isEmpty() ? petTypes : Collections.singleton(null);

			for (SpellId spellId : possibleSpells) {
				for (PetType petType : possiblePetTypes) {
					AttributeCondition condition = AttributeCondition.of(talentTree, spellSchool, spellId, petType, null);
					conditionConsumer.accept(condition);
				}
			}
		}
	}

	private final ExcelColumn colSpellSpell = column("spell");
	private final ExcelColumn colSpellTree = column("tree");
	private final ExcelColumn colSpellSchool = column("school");
	private final ExcelColumn colSpellCoeffDirect = column("coeff direct");
	private final ExcelColumn colSpellCoeffDot = column("coeff dot");
	private final ExcelColumn colSpellCooldown = column("cooldown");
	private final ExcelColumn colSpellIgnoresGcd = column("ignores gcd");
	private final ExcelColumn colSpellRequitedTalent = column("required talent");
	private final ExcelColumn colSpellBolt = column("bolt");
	private final ExcelColumn colSpellConversionFrom = column("conversion: from");
	private final ExcelColumn colSpellConversionTo = column("conversion: to");
	private final ExcelColumn colSpellConversionPct = column("conversion: %");
	private final ExcelColumn colSpellRequitedEffect = column("required effect");
	private final ExcelColumn colSpellEffectRemovedOnHit = column("effect removed on hit");
	private final ExcelColumn colSpellBonusDamageIfUnderEffect = column("bonus dmg if under effect");
	private final ExcelColumn colSpellDotScheme = column("dot scheme");

	private final ExcelColumn colRankSpell = column("spell");
	private final ExcelColumn colRankRank = column("rank");
	private final ExcelColumn colRankLevel = column("level");
	private final ExcelColumn colRankManaCost = column("mana cost");
	private final ExcelColumn colRankCastTime = column("cast time");
	private final ExcelColumn colRankChanneled = column("channeled");
	private final ExcelColumn colRankMinDmg = column("min dmg");
	private final ExcelColumn colRankMaxDmg = column("max dmg");
	private final ExcelColumn colRankDotDmg = column("dot dmg");
	private final ExcelColumn colRankNumTicks = column("num ticks");
	private final ExcelColumn colRankTickInterval = column("tick interval");
	private final ExcelColumn colRankMinDmg2 = column("min dmg2");
	private final ExcelColumn colRankMaxDmg2 = column("max dmg2");
	private final ExcelColumn colRankAdditionalCostType = column("additional cost: type");
	private final ExcelColumn colRankAdditionalCostAmount = column("additional cost: amount");
	private final ExcelColumn colRankAdditionalCostScaled = column("additional cost: scaled");
	private final ExcelColumn colRankAppliedEffect = column("applied effect");
	private final ExcelColumn colRankAppliedEffectDuration = column("applied effect duration");

	private final ExcelColumn colTalentTalent = column("talent");
	private final ExcelColumn colTalentRank = column("rank");
	private final ExcelColumn colTalentMaxRank = column("max rank");
	private final ExcelColumn colTalentDescription = column("description");

	private final ExcelColumn colTalentTree = column("tree");
	private final ExcelColumn colTalentSchool = column("school");
	private final ExcelColumn colTalentSpell = column("spell");
	private final ExcelColumn colTalentPet = column("pet");

	private final ExcelColumn colEffectEffect = column("effect");
	private final ExcelColumn colEffectFriendly = column("friendly");
	private final ExcelColumn colEffectScope = column("scope");
	private final ExcelColumn colEffectMaxStacks = column("max stacks");
	private final ExcelColumn colEffectRemoveAfter = column("remove after");
	private final ExcelColumn colEffectRemoveAfterSchool = column("remove after: school");
	private final ExcelColumn colEffectOnApply = column("on apply");
	private final ExcelColumn colEffectTree = column("tree");
	private final ExcelColumn colEffectSchool = column("school");
	private final ExcelColumn colEffectSpell = column("spell");
	private final ExcelColumn colEffectPet = column("pet");
	private final ExcelColumn colEffectStackScaling = column("stack scaling");

	private final ExcelColumn colBuffId = column("id");
	private final ExcelColumn colBuffName = column("name");
	private final ExcelColumn colBuffLevel = column("level");
	private final ExcelColumn colBuffType = column("type");
	private final ExcelColumn colBuffExclusionGroup = column("exclusion group");
	private final ExcelColumn colBuffStat = column("stat");
	private final ExcelColumn colBuffAmount = column("amount");
	private final ExcelColumn colBuffDuration = column("duration");
	private final ExcelColumn colBuffCooldown = column("cooldown");
	private final ExcelColumn colBuffDescription = column("description");
	private final ExcelColumn colBuffSourceSpell = column("source spell");

	private final BenefitAttributeColumns talentColumns = new BenefitAttributeColumns()
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

	private final BenefitAttributeColumns effectColumns = new BenefitAttributeColumns()
			.add(EFFECT_INCREASE_PCT, "effect increase%")
			.add(DAMAGE_TAKEN_INCREASE_PCT, "damage taken increase%")
			.add(SPELL_DAMAGE, "sp bonus")
			.add(NUM_NEXT_SPELLS_CAST_INSTANTLY, "#next spells cast instantly")
			;


	private void readSpells() {
		SpellInfo spellInfo = getSpellInfo();

		if (spellDataRepository.getSpellInfo(spellInfo.getSpellId()).isPresent()) {
			throw new IllegalArgumentException("Duplicate: " + spellInfo.getSpellId());
		}

		spellDataRepository.addSpellInfo(spellInfo);
	}

	private SpellInfo getSpellInfo() {
		var spellId = SpellId.parse(colSpellSpell.getString());
		var talentTree = colSpellTree.getEnum(TalentTree::parse);
		var spellSchool = colSpellSchool.getEnum(SpellSchool::parse, null);
		var coeffDirect = colSpellCoeffDirect.getPercent(Percent.ZERO);
		var coeffDot = colSpellCoeffDot.getPercent(Percent.ZERO);
		var cooldown = colSpellCooldown.getDuration(null);
		var ignoresGCD = colSpellIgnoresGcd.getBoolean();
		var requiredTalent = colSpellRequitedTalent.getEnum(TalentId::parse, null);
		var bolt = colSpellBolt.getBoolean();
		var requiredEffect = colSpellRequitedEffect.getEnum(EffectId::parse, null);
		var effectRemovedOnHit = colSpellEffectRemovedOnHit.getEnum(EffectId::parse, null);
		var bonusDamageIfUnderEffect = colSpellBonusDamageIfUnderEffect.getEnum(EffectId::parse, null);
		var dotScheme = colSpellDotScheme.getList(Integer::parseInt);
		var conversion = getConversion();

		return new SpellInfo(spellId, talentTree, spellSchool, coeffDirect, coeffDot, cooldown, ignoresGCD, requiredTalent, bolt, conversion, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect, dotScheme);
	}

	private Conversion getConversion() {
		var conversionFrom = colSpellConversionFrom.getEnum(Conversion.From::parse, null);
		var conversionTo = colSpellConversionTo.getEnum(Conversion.To::parse, null);

		if (conversionFrom != null && conversionTo != null) {
			var conversionPct = colSpellConversionPct.getPercent();
			return new Conversion(conversionFrom, conversionTo, conversionPct);
		} else if (conversionFrom != null || conversionTo != null) {
			throw new IllegalArgumentException("Conversion misses either from or to part");
		}

		return null;
	}

	private void readRanks() {
		SpellRankInfo spellRankInfo = getSpellRankInfo();
		SpellInfo spellInfo = spellDataRepository.getSpellInfo(spellRankInfo.getSpellId()).orElseThrow();

		if (spellInfo.getRanks().containsKey(spellRankInfo.getRank())) {
			throw new IllegalArgumentException("Duplicate rank: " + spellRankInfo.getSpellId() + " " + spellRankInfo.getRank());
		}

		spellInfo.getRanks().put(spellRankInfo.getRank(), spellRankInfo);
	}

	private SpellRankInfo getSpellRankInfo() {
		var spellId = SpellId.parse(colRankSpell.getString());
		var rank = colRankRank.getInteger(0);
		var level = colRankLevel.getInteger();
		var manaCost = colRankManaCost.getInteger();
		var castTime = colRankCastTime.getDuration();
		var channeled = colRankChanneled.getBoolean();
		var minDmg = colRankMinDmg.getInteger(0);
		var maxDmg = colRankMaxDmg.getInteger(0);
		var dotDmg = colRankDotDmg.getInteger(0);
		var numTicks = colRankNumTicks.getInteger(0);
		var tickInterval = colRankTickInterval.getDuration(null);
		var minDmg2 = colRankMinDmg2.getInteger(0);
		var maxDmg2 = colRankMaxDmg2.getInteger(0);
		var appliedEffect = colRankAppliedEffect.getEnum(EffectId::parse, null);
		var appliedEffectDuration = colRankAppliedEffectDuration.getDuration(null);
		var additionalCost = getAdditionalCost();

		if (appliedEffect != null && appliedEffectDuration == null) {
			appliedEffectDuration = Duration.INFINITE;
		}

		return new SpellRankInfo(spellId, rank, level, manaCost, castTime, channeled, minDmg, maxDmg, minDmg2, maxDmg2, dotDmg, numTicks, tickInterval, additionalCost, appliedEffect, appliedEffectDuration);
	}

	private AdditionalCost getAdditionalCost() {
		var additionalCostType = colRankAdditionalCostType.getEnum(CostType::parse, null);

		if (additionalCostType == null) {
			return null;
		}

		var additionalCostAmount = colRankAdditionalCostAmount.getInteger();
		var additionalCostScaled = colRankAdditionalCostScaled.getBoolean();

		return new AdditionalCost(additionalCostType, additionalCostAmount, additionalCostScaled);
	}

	private void readTalents() {
		TalentInfo talentInfo = getTalentInfo();
		Attributes talentBenefit = getTalentBenefit();
		spellDataRepository.addTalent(talentInfo, talentBenefit);
	}

	private TalentInfo getTalentInfo() {
		var talentName = TalentId.parse(colTalentTalent.getString());
		var rank = colTalentRank.getInteger();
		var maxRank = colTalentMaxRank.getInteger();
		var description = colTalentDescription.getString(null);

		return new TalentInfo(talentName, rank, maxRank, description, Attributes.EMPTY);
	}

	private Attributes getTalentBenefit() {
		var talentTree = colTalentTree.getEnum(TalentTree::parse, null);
		var spellSchool = colTalentSchool.getEnum(SpellSchool::parse, null);
		var spells = colTalentSpell.getSet(SpellId::parse);
		var petTypes = colTalentPet.getSet(PetType::parse);

		return talentColumns.readAttributes(talentTree, spellSchool, spells, petTypes);
	}

	private void readEffects() {
		EffectInfo effectInfo = getEffectInfo();
		spellDataRepository.addEffectInfo(effectInfo);
	}

	private EffectInfo getEffectInfo() {
		var effectId = EffectId.parse(colEffectEffect.getString());
		var friendly = colEffectFriendly.getBoolean();
		var scope = colEffectScope.getEnum(Scope::parse, Scope.PERSONAL);
		var maxStacks = colEffectMaxStacks.getInteger(1);
		var onApply = colEffectOnApply.getEnum(OnApply::parse, null);
		var stackScaling = colEffectStackScaling.getBoolean();
		var attributes = getEffectAttributes();
		var removeCondition = getRemoveCondition();

		return new EffectInfo(effectId, friendly, scope, maxStacks, removeCondition, onApply, attributes, stackScaling);
	}

	private Attributes getEffectAttributes() {
		var talentTree = colEffectTree.getEnum(TalentTree::parse, null);
		var spellSchool = colEffectSchool.getEnum(SpellSchool::parse, null);
		var spells = colEffectSpell.getSet(SpellId::parse);
		var petTypes = colEffectPet.getSet(PetType::parse);

		return effectColumns.readAttributes(talentTree, spellSchool, spells, petTypes);
	}

	private RemoveCondition getRemoveCondition() {
		var removeConditionEvent = colEffectRemoveAfter.getEnum(RemoveEvent::parse, null);
		var removeConditionSpellSchool = colEffectRemoveAfterSchool.getEnum(SpellSchool::parse, null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}

	private void readBuffs() {
		Buff buff = getBuff();
		spellDataRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = colBuffId.getInteger();
		var name = colBuffName.getString();
		var level = colBuffLevel.getInteger(0);
		var type = colBuffType.getEnum(BuffType::parse);
		var exclusionGroup = colBuffExclusionGroup.getEnum(BuffExclusionGroup::parse, null);
		var duration = colBuffDuration.getDuration(null);
		var cooldown = colBuffCooldown.getDuration(null);
		var description = colBuffDescription.getString(null);
		var sourceSpell = colBuffSourceSpell.getEnum(SpellId::parse, null);
		var buffAttributes = getBuffAttributes();

		return new Buff(buffId, name, level, type, exclusionGroup, buffAttributes, sourceSpell, duration, cooldown, description);
	}

	private Attributes getBuffAttributes() {
		AttributesBuilder builder = new AttributesBuilder();
		int maxAttributes = 5;

		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			var attributeStr = colBuffStat.multi(statNo).getString(null);
			if (attributeStr != null) {
				PrimitiveAttributeSupplier attributeParser = PrimitiveAttributeSupplier.fromString(attributeStr);
				int amount = colBuffAmount.multi(statNo).getInteger();
				builder.addAttributeList(attributeParser.getAttributeList(amount));
			}
		}

		return builder.toAttributes();
	}
}
