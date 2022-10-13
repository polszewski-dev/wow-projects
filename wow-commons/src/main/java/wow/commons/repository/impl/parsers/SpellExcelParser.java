package wow.commons.repository.impl.parsers;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.modifiers.ProcEvent;
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
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

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
				new SheetReader("spells", this::readSpells, COL_SPELL_SPELL),
				new SheetReader("ranks", this::readRanks, COL_RANK_SPELL),
				new SheetReader("talents", this::readTalents, COL_TALENT_TALENT),
				new SheetReader("effects", this::readEffects, COL_EFFECT_EFFECT),
				new SheetReader("buffs", this::readBuffs, COL_BUFF_NAME)
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

			SimpleColumn(String name, AttributeId id) {
				super(id);
				this.name = column(name);
			}

			@Override
			Attribute readAttribute() {
				if (id.isDoubleAttribute()) {
					var value = name.getDouble(0);
					return Attribute.ofNullable(id, value);
				}
				if (id.isPercentAttribute()) {
					var value = name.getPercent(null);
					return Attribute.ofNullable(id, value);
				}
				if (id.isBooleanAttribute()) {
					var value = name.getBoolean();
					return Attribute.ofNullable(id, value);
				}
				if (id.isDurationAttribute()) {
					var value = name.getDuration(null);
					return Attribute.ofNullable(id, value);
				}
				throw new IllegalArgumentException("Unhandled type: " + id.getType());
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
			Attribute readAttribute() {
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
			public Attribute readAttribute() {
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
			public Attribute readAttribute() {
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

		BenefitAttributeColumns add(AttributeId id, String name) {
			columns.add(new SimpleColumn(name, id));
			return this;
		}

		BenefitAttributeColumns addStatConversion(
				String colConversionFrom,
				String colConversionTo,
				String colConversionRationPct
		) {
			columns.add(new StatConversionColumn(colConversionFrom, colConversionTo, colConversionRationPct, AttributeId.statConversion));
			return this;
		}

		BenefitAttributeColumns addProcTrigger(
				String colType,
				String colChancePct,
				String colEffect,
				String colDuration,
				String colStacks
		) {
			columns.add(new ProcTriggerColumn(colType, colChancePct, colEffect, colDuration, colStacks, AttributeId.SpecialAbilities));
			return this;
		}

		BenefitAttributeColumns addEffectIncreasePerEffectOnTarget(
				String colEffectTree,
				String colIncreasePerEffectPct,
				String colMaxIncreasePct
		) {
			columns.add(new EffectIncreasePerEffectOnTargetColumn(colEffectTree, colIncreasePerEffectPct, colMaxIncreasePct, AttributeId.effectIncreasePerEffectOnTarget));
			return this;
		}

		Attributes readAttributes(TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes) {
			AttributesBuilder result = new AttributesBuilder();
			for (Column column : columns) {
				Attribute attribute = column.readAttribute();
				if (attribute != null) {
					spells = !spells.isEmpty() ? spells : Collections.singleton(null);
					petTypes = !petTypes.isEmpty() ? petTypes : Collections.singleton(null);

					for (SpellId spellId : spells) {
						for (PetType petType : petTypes) {
							AttributeCondition condition = AttributeCondition.of(talentTree, spellSchool, spellId, petType, null);
							result.addAttribute(attribute.attachCondition(condition));
						}
					}
				}
			}
			return result.toAttributes();
		}
	}

	private final ExcelColumn COL_SPELL_SPELL = column("spell");
	private final ExcelColumn COL_SPELL_TREE = column("tree");
	private final ExcelColumn COL_SPELL_SCHOOL = column("school");
	private final ExcelColumn COL_SPELL_COEFF_DIRECT = column("coeff direct");
	private final ExcelColumn COL_SPELL_COEFF_DOT = column("coeff dot");
	private final ExcelColumn COL_SPELL_COOLDOWN = column("cooldown");
	private final ExcelColumn COL_SPELL_IGNORES_GCD = column("ignores gcd");
	private final ExcelColumn COL_SPELL_REQUITED_TALENT = column("required talent");
	private final ExcelColumn COL_SPELL_BOLT = column("bolt");
	private final ExcelColumn COL_SPELL_CONVERSION_FROM = column("conversion: from");
	private final ExcelColumn COL_SPELL_CONVERSION_TO = column("conversion: to");
	private final ExcelColumn COL_SPELL_CONVERSION_PCT = column("conversion: %");
	private final ExcelColumn COL_SPELL_REQUITED_EFFECT = column("required effect");
	private final ExcelColumn COL_SPELL_EFFECT_REMOVED_ON_HIT = column("effect removed on hit");
	private final ExcelColumn COL_SPELL_BONUS_DAMAGE_IF_UNDER_EFFECT = column("bonus dmg if under effect");
	private final ExcelColumn COL_SPELL_DOT_SCHEME = column("dot scheme");

	private final ExcelColumn COL_RANK_SPELL = column("spell");
	private final ExcelColumn COL_RANK_RANK = column("rank");
	private final ExcelColumn COL_RANK_LEVEL = column("level");
	private final ExcelColumn COL_RANK_MANA_COST = column("mana cost");
	private final ExcelColumn COL_RANK_CAST_TIME = column("cast time");
	private final ExcelColumn COL_RANK_CHANNELED = column("channeled");
	private final ExcelColumn COL_RANK_MIN_DMG = column("min dmg");
	private final ExcelColumn COL_RANK_MAX_DMG = column("max dmg");
	private final ExcelColumn COL_RANK_DOT_DMG = column("dot dmg");
	private final ExcelColumn COL_RANK_NUM_TICKS = column("num ticks");
	private final ExcelColumn COL_RANK_TICK_INTERVAL = column("tick interval");
	private final ExcelColumn COL_RANK_MIN_DMG2 = column("min dmg2");
	private final ExcelColumn COL_RANK_MAX_DMG2 = column("max dmg2");
	private final ExcelColumn COL_RANK_ADDITIONAL_COST_TYPE = column("additional cost: type");
	private final ExcelColumn COL_RANK_ADDITIONAL_COST_AMOUNT = column("additional cost: amount");
	private final ExcelColumn COL_RANK_ADDITIONAL_COST_SCALED = column("additional cost: scaled");
	private final ExcelColumn COL_RANK_APPLIED_EFFECT = column("applied effect");
	private final ExcelColumn COL_RANK_APPLIED_EFFECT_DURATION = column("applied effect duration");

	private final ExcelColumn COL_TALENT_TALENT = column("talent");
	private final ExcelColumn COL_TALENT_RANK = column("rank");
	private final ExcelColumn COL_TALENT_MAX_RANK = column("max rank");
	private final ExcelColumn COL_TALENT_DESCRIPTION = column("description");

	private final ExcelColumn COL_TALENT_TREE = column("tree");
	private final ExcelColumn COL_TALENT_SCHOOL = column("school");
	private final ExcelColumn COL_TALENT_SPELL = column("spell");
	private final ExcelColumn COL_TALENT_PET = column("pet");

	private final ExcelColumn COL_EFFECT_EFFECT = column("effect");
	private final ExcelColumn COL_EFFECT_FRIENDLY = column("friendly");
	private final ExcelColumn COL_EFFECT_SCOPE = column("scope");
	private final ExcelColumn COL_EFFECT_MAX_STACKS = column("max stacks");
	private final ExcelColumn COL_EFFECT_REMOVE_AFTER = column("remove after");
	private final ExcelColumn COL_EFFECT_REMOVE_AFTER_SCHOOL = column("remove after: school");
	private final ExcelColumn COL_EFFECT_ON_APPLY = column("on apply");
	private final ExcelColumn COL_EFFECT_TREE = column("tree");
	private final ExcelColumn COL_EFFECT_SCHOOL = column("school");
	private final ExcelColumn COL_EFFECT_SPELL = column("spell");
	private final ExcelColumn COL_EFFECT_PET = column("pet");
	private final ExcelColumn COL_EFFECT_STACK_SCALING = column("stack scaling");

	private final ExcelColumn COL_BUFF_ID = column("id");
	private final ExcelColumn COL_BUFF_NAME = column("name");
	private final ExcelColumn COL_BUFF_LEVEL = column("level");
	private final ExcelColumn COL_BUFF_TYPE = column("type");
	private final ExcelColumn COL_BUFF_EXCLUSION_GROUP = column("exclusion group");
	private final ExcelColumn COL_BUFF_STAT = column("stat");
	private final ExcelColumn COL_BUFF_AMOUNT = column("amount");
	private final ExcelColumn COL_BUFF_DURATION = column("duration");
	private final ExcelColumn COL_BUFF_COOLDOWN = column("cooldown");
	private final ExcelColumn COL_BUFF_DESCRIPTION = column("description");
	private final ExcelColumn COL_BUFF_SOURCE_SPELL = column("source spell");

	private final BenefitAttributeColumns TALENT_COLUMNS = new BenefitAttributeColumns()
			.add(AttributeId.castTimeReduction, "cast time reduction")
			.add(AttributeId.cooldownReduction, "cooldown reduction")
			.add(AttributeId.costReductionPct, "cost reduction%")
			.add(AttributeId.threatReductionPct, "threat reduction%")
			.add(AttributeId.pushbackReductionPct, "pushback reduction%")
			.add(AttributeId.rangeIncreasePct, "range increase%")
			.add(AttributeId.durationIncreasePct, "duration increase%")

			.add(AttributeId.spellCoeffBonusPct, "spell coeff bonus%")
			.add(AttributeId.effectIncreasePct, "effect increase%")
			.add(AttributeId.directDamageIncreasePct, "direct damage increase%")
			.add(AttributeId.dotDamageIncreasePct, "dot damage increase%")
			.add(AttributeId.critDamageIncreasePct, "spell crit damage increase%")

			.add(AttributeId.staIncreasePct, "sta increase%")
			.add(AttributeId.intIncreasePct, "int increase%")
			.add(AttributeId.spiIncreasePct, "spi increase%")
			.add(AttributeId.maxHealthIncreasePct, "max health increase%")
			.add(AttributeId.maxManaIncreasePct, "max mana increase%")
			.add(AttributeId.SpellHitPct, "spell hit increase%")
			.add(AttributeId.SpellCritPct, "spell crit increase%")
			.add(AttributeId.meleeCritIncreasePct, "melee crit increase%")

			.add(AttributeId.petStaIncreasePct, "pet sta increase%")
			.add(AttributeId.petIntIncreasePct, "pet int increase%")
			.add(AttributeId.petSpellCritIncreasePct, "pet spell crit increase%")
			.add(AttributeId.petMeleeCritIncreasePct, "pet melee crit increase%")
			.add(AttributeId.petMeleeDamageIncreasePct, "pet melee damage increase%")

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

			.add(AttributeId.manaTransferredToPetPct, "mana transferred to pet%")

			;

	private final BenefitAttributeColumns EFFECT_COLUMNS = new BenefitAttributeColumns()
			.add(AttributeId.effectIncreasePct, "effect increase%")
			.add(AttributeId.damageTakenIncreasePct, "damage taken increase%")
			.add(AttributeId.SpellDamage, "sp bonus")
			.add(AttributeId.castInstantly, "cast instanly")
			;


	private void readSpells() {
		SpellInfo spellInfo = getSpellInfo();

		if (spellDataRepository.getSpellInfo(spellInfo.getSpellId()) != null) {
			throw new IllegalArgumentException("Duplicate: " + spellInfo.getSpellId());
		}

		spellDataRepository.addSpellInfo(spellInfo);
	}

	private SpellInfo getSpellInfo() {
		var spellId = SpellId.parse(COL_SPELL_SPELL.getString());
		var talentTree = COL_SPELL_TREE.getEnum(TalentTree::parse);
		var spellSchool = COL_SPELL_SCHOOL.getEnum(SpellSchool::parse, null);
		var coeffDirect = COL_SPELL_COEFF_DIRECT.getPercent(Percent.ZERO);
		var coeffDot = COL_SPELL_COEFF_DOT.getPercent(Percent.ZERO);
		var cooldown = COL_SPELL_COOLDOWN.getDuration(null);
		var ignoresGCD = COL_SPELL_IGNORES_GCD.getBoolean();
		var requiredTalent = COL_SPELL_REQUITED_TALENT.getEnum(TalentId::parse, null);
		var bolt = COL_SPELL_BOLT.getBoolean();
		var requiredEffect = COL_SPELL_REQUITED_EFFECT.getEnum(EffectId::parse, null);
		var effectRemovedOnHit = COL_SPELL_EFFECT_REMOVED_ON_HIT.getEnum(EffectId::parse, null);
		var bonusDamageIfUnderEffect = COL_SPELL_BONUS_DAMAGE_IF_UNDER_EFFECT.getEnum(EffectId::parse, null);
		var dotScheme = COL_SPELL_DOT_SCHEME.getList(Integer::parseInt);
		var conversion = getConversion();

		return new SpellInfo(spellId, talentTree, spellSchool, coeffDirect, coeffDot, cooldown, ignoresGCD, requiredTalent, bolt, conversion, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect, dotScheme);
	}

	private Conversion getConversion() {
		var conversionFrom = COL_SPELL_CONVERSION_FROM.getEnum(Conversion.From::parse, null);
		var conversionTo = COL_SPELL_CONVERSION_TO.getEnum(Conversion.To::parse, null);

		if (conversionFrom != null && conversionTo != null) {
			var conversionPct = COL_SPELL_CONVERSION_PCT.getPercent();
			return new Conversion(conversionFrom, conversionTo, conversionPct);
		} else if (conversionFrom != null || conversionTo != null) {
			throw new IllegalArgumentException("Conversion misses either from or to part");
		}

		return null;
	}

	private void readRanks() {
		SpellRankInfo spellRankInfo = getSpellRankInfo();
		SpellInfo spellInfo = spellDataRepository.getSpellInfo(spellRankInfo.getSpellId());

		if (spellInfo == null) {
			throw new IllegalArgumentException("No spell: " + spellRankInfo.getSpellId());
		}

		if (spellInfo.getRanks().containsKey(spellRankInfo.getRank())) {
			throw new IllegalArgumentException("Duplicate rank: " + spellRankInfo.getSpellId() + " " + spellRankInfo.getRank());
		}

		spellInfo.getRanks().put(spellRankInfo.getRank(), spellRankInfo);
	}

	private SpellRankInfo getSpellRankInfo() {
		var spellId = SpellId.parse(COL_RANK_SPELL.getString());
		var rank = COL_RANK_RANK.getInteger(0);
		var level = COL_RANK_LEVEL.getInteger();
		var manaCost = COL_RANK_MANA_COST.getInteger();
		var castTime = COL_RANK_CAST_TIME.getDuration();
		var channeled = COL_RANK_CHANNELED.getBoolean();
		var minDmg = COL_RANK_MIN_DMG.getInteger(0);
		var maxDmg = COL_RANK_MAX_DMG.getInteger(0);
		var dotDmg = COL_RANK_DOT_DMG.getInteger(0);
		var numTicks = COL_RANK_NUM_TICKS.getInteger(0);
		var tickInterval = COL_RANK_TICK_INTERVAL.getDuration(null);
		var minDmg2 = COL_RANK_MIN_DMG2.getInteger(0);
		var maxDmg2 = COL_RANK_MAX_DMG2.getInteger(0);
		var appliedEffect = COL_RANK_APPLIED_EFFECT.getEnum(EffectId::parse, null);
		var appliedEffectDuration = COL_RANK_APPLIED_EFFECT_DURATION.getDuration(null);
		var additionalCost = getAdditionalCost();

		if (appliedEffect != null && appliedEffectDuration == null) {
			appliedEffectDuration = Duration.INFINITE;
		}

		return new SpellRankInfo(spellId, rank, level, manaCost, castTime, channeled, minDmg, maxDmg, minDmg2, maxDmg2, dotDmg, numTicks, tickInterval, additionalCost, appliedEffect, appliedEffectDuration);
	}

	private AdditionalCost getAdditionalCost() {
		var additionalCostType = COL_RANK_ADDITIONAL_COST_TYPE.getEnum(CostType::parse, null);

		if (additionalCostType == null) {
			return null;
		}

		var additionalCostAmount = COL_RANK_ADDITIONAL_COST_AMOUNT.getInteger();
		var additionalCostScaled = COL_RANK_ADDITIONAL_COST_SCALED.getBoolean();

		return new AdditionalCost(additionalCostType, additionalCostAmount, additionalCostScaled);
	}

	private void readTalents() {
		TalentInfo talentInfo = getTalentInfo();
		Attributes talentBenefit = getTalentBenefit();
		spellDataRepository.addTalent(talentInfo, talentBenefit);
	}

	private TalentInfo getTalentInfo() {
		var talentName = TalentId.parse(COL_TALENT_TALENT.getString());
		var rank = COL_TALENT_RANK.getInteger();
		var maxRank = COL_TALENT_MAX_RANK.getInteger();
		var description = COL_TALENT_DESCRIPTION.getString(null);

		return new TalentInfo(talentName, rank, maxRank, description, Attributes.EMPTY);
	}

	private Attributes getTalentBenefit() {
		var talentTree = COL_TALENT_TREE.getEnum(TalentTree::parse, null);
		var spellSchool = COL_TALENT_SCHOOL.getEnum(SpellSchool::parse, null);
		var spells = COL_TALENT_SPELL.getSet(SpellId::parse);
		var petTypes = COL_TALENT_PET.getSet(PetType::parse);

		return TALENT_COLUMNS.readAttributes(talentTree, spellSchool, spells, petTypes);
	}

	private void readEffects() {
		EffectInfo effectInfo = getEffectInfo();
		spellDataRepository.addEffectInfo(effectInfo);
	}

	private EffectInfo getEffectInfo() {
		var effectId = EffectId.parse(COL_EFFECT_EFFECT.getString());
		var friendly = COL_EFFECT_FRIENDLY.getBoolean();
		var scope = COL_EFFECT_SCOPE.getEnum(Scope::parse, Scope.PERSONAL);
		var maxStacks = COL_EFFECT_MAX_STACKS.getInteger(1);
		var onApply = COL_EFFECT_ON_APPLY.getEnum(OnApply::parse, null);
		var stackScaling = COL_EFFECT_STACK_SCALING.getBoolean();
		var attributes = getEffectAttributes();
		var removeCondition = getRemoveCondition();

		return new EffectInfo(effectId, friendly, scope, maxStacks, removeCondition, onApply, attributes, stackScaling);
	}

	private Attributes getEffectAttributes() {
		var talentTree = COL_EFFECT_TREE.getEnum(TalentTree::parse, null);
		var spellSchool = COL_EFFECT_SCHOOL.getEnum(SpellSchool::parse, null);
		var spells = COL_EFFECT_SPELL.getSet(SpellId::parse);
		var petTypes = COL_EFFECT_PET.getSet(PetType::parse);

		return EFFECT_COLUMNS.readAttributes(talentTree, spellSchool, spells, petTypes);
	}

	private RemoveCondition getRemoveCondition() {
		var removeConditionEvent = COL_EFFECT_REMOVE_AFTER.getEnum(RemoveEvent::parse, null);
		var removeConditionSpellSchool = COL_EFFECT_REMOVE_AFTER_SCHOOL.getEnum(SpellSchool::parse, null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}

	private void readBuffs() {
		Buff buff = getBuff();
		spellDataRepository.addBuff(buff);
	}

	private Buff getBuff() {
		var buffId = COL_BUFF_ID.getInteger();
		var name = COL_BUFF_NAME.getString();
		var level = COL_BUFF_LEVEL.getInteger(0);
		var type = COL_BUFF_TYPE.getEnum(BuffType::parse);
		var exclusionGroup = COL_BUFF_EXCLUSION_GROUP.getEnum(BuffExclusionGroup::parse, null);
		var duration = COL_BUFF_DURATION.getDuration(null);
		var cooldown = COL_BUFF_COOLDOWN.getDuration(null);
		var description = COL_BUFF_DESCRIPTION.getString(null);
		var sourceSpell = COL_BUFF_SOURCE_SPELL.getEnum(SpellId::parse, null);
		var buffAttributes = getBuffAttributes();

		return new Buff(buffId, name, level, type, exclusionGroup, buffAttributes, sourceSpell, duration, cooldown, description);
	}

	private Attributes getBuffAttributes() {
		AttributesBuilder builder = new AttributesBuilder();
		int maxAttributes = 5;

		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			var attributeStr = COL_BUFF_STAT.multi(statNo).getString(null);
			if (attributeStr != null) {
				SimpleAttributeParser attributeParser = new SimpleAttributeParser(attributeStr);
				int amount = COL_BUFF_AMOUNT.multi(statNo).getInteger();
				for (Attribute attribute : attributeParser.getAttributes(amount)) {
					builder.addAttribute(attribute);
				}
			}
		}

		return builder.toAttributes();
	}
}
