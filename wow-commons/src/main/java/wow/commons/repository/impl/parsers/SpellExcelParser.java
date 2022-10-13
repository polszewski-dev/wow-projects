package wow.commons.repository.impl.parsers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
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

import java.io.IOException;
import java.util.*;

import static wow.commons.util.ExcelUtil.*;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public class SpellExcelParser {
	public static void readFromXls(SpellDataRepositoryImpl spellDataRepository) throws IOException, InvalidFormatException {
		try (ExcelReader excelReader = new PoiExcelReader(SpellExcelParser.class.getResourceAsStream("/xls/spell_data.xls"))) {
			while (excelReader.nextSheet()) {
				if (!excelReader.nextRow()) {
					continue;
				}

				switch (excelReader.getCurrentSheetName()) {
					case SHEET_SPELLS:
						readSpells(excelReader, spellDataRepository);
						break;
					case SHEET_RANKS:
						readRanks(excelReader, spellDataRepository);
						break;
					case SHEET_TALENTS:
						readTalents(excelReader, spellDataRepository);
						break;
					case SHEET_EFFECTS:
						readEffects(excelReader, spellDataRepository);
						break;
					case SHEET_BUFFS:
						readBuffs(excelReader, spellDataRepository);
						break;
					default:
						throw new IllegalArgumentException("Unknown sheet: " + excelReader.getCurrentSheetName());
				}
			}
		}
	}

	private static class BenefitAttributeColumns {
		private static abstract class Column {
			final AttributeId id;

			Column(AttributeId id) {
				this.id = id;
			}

			abstract Attribute readAttribute(ExcelReader excelReader, Map<String, Integer> header);
		}

		private static class SimpleColumn extends Column {
			final String name;

			SimpleColumn(String name, AttributeId id) {
				super(id);
				this.name = name;
			}

			@Override
			Attribute readAttribute(ExcelReader excelReader, Map<String, Integer> header) {
				if (id.isDoubleAttribute()) {
					var value = getOptionalDouble(name, excelReader, header).orElse(0);
					return Attribute.ofNullable(id, value);
				}
				if (id.isPercentAttribute()) {
					var value = getOptionalPercent(name, excelReader, header).orElse(null);
					return Attribute.ofNullable(id, value);
				}
				if (id.isBooleanAttribute()) {
					var value = getBoolean(name, excelReader, header);
					return Attribute.ofNullable(id, value);
				}
				if (id.isDurationAttribute()) {
					var value = getOptionalDuration(name, excelReader, header).orElse(null);
					return Attribute.ofNullable(id, value);
				}
				throw new IllegalArgumentException("Unhandled type: " + id.getType());
			}
		}

		private static class StatConversionColumn extends Column {
			final String colConversionFrom;
			final String colConversionTo;
			final String colConversionRationPct;

			StatConversionColumn(String colConversionFrom, String colConversionTo, String colConversionRationPct, AttributeId id) {
				super(id);
				this.colConversionFrom = colConversionFrom;
				this.colConversionTo = colConversionTo;
				this.colConversionRationPct = colConversionRationPct;
			}

			@Override
			Attribute readAttribute(ExcelReader excelReader, Map<String, Integer> header) {
				var conversionFrom = getOptionalEnum(colConversionFrom, StatConversion.Stat::valueOf, excelReader, header).orElse(null);
				var conversionTo = getOptionalEnum(colConversionTo, StatConversion.Stat::valueOf, excelReader, header).orElse(null);

				if (conversionFrom != null && conversionTo != null) {
					var conversionRatioPct = getPercent(colConversionRationPct, excelReader, header);
					return new StatConversion(conversionFrom, conversionTo, conversionRatioPct, null);
				} else if (conversionFrom != null || conversionTo != null) {
					throw new IllegalArgumentException("Missing conversion from or to");
				}

				return null;
			}
		}

		private static class ProcTriggerColumn extends Column {
			final String colType;
			final String colChancePct;
			final String colEffect;
			final String colDuration;
			final String colStacks;

			ProcTriggerColumn(String colType, String colChancePct, String colEffect, String colDuration, String colStacks, AttributeId id) {
				super(id);
				this.colType = colType;
				this.colChancePct = colChancePct;
				this.colEffect = colEffect;
				this.colDuration = colDuration;
				this.colStacks = colStacks;
			}

			@Override
			public Attribute readAttribute(ExcelReader excelReader, Map<String, Integer> header) {
				var effectId = getOptionalString(colEffect, excelReader, header).map(EffectId::parse).orElse(null);

				if (effectId == null) {
					return null;
				}

				var type = getEnum(colType, ProcEvent::valueOf, excelReader, header);
				var chancePct = getOptionalPercent(colChancePct, excelReader, header).orElse(Percent._100);
				var duration = getOptionalDuration(colDuration, excelReader, header).orElse(Duration.INFINITE);
				var stacks = getOptionalInteger(colStacks, excelReader, header).orElse(1);

				return SpecialAbility.talentProc(type, chancePct, effectId, duration, stacks);
			}
		}

		private static class EffectIncreasePerEffectOnTargetColumn extends Column {
			final String colEffectTree;
			final String colIncreasePerEffectPct;
			final String colMaxIncreasePct;

			EffectIncreasePerEffectOnTargetColumn(String colEffectTree, String colIncreasePerEffectPct, String colMaxIncreasePct, AttributeId id) {
				super(id);
				this.colEffectTree = colEffectTree;
				this.colIncreasePerEffectPct = colIncreasePerEffectPct;
				this.colMaxIncreasePct = colMaxIncreasePct;
			}

			@Override
			public Attribute readAttribute(ExcelReader excelReader, Map<String, Integer> header) {
				var effectTree = getOptionalEnum(colEffectTree, TalentTree::parse, excelReader, header).orElse(null);

				if (effectTree == null) {
					return null;
				}

				var increasePerEffectPct = getPercent(colIncreasePerEffectPct, excelReader, header);
				var maxIncreasePct = getPercent(colMaxIncreasePct, excelReader, header);

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

		Attributes readAttributes(ExcelReader excelReader, Map<String, Integer> header, TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes) {
			AttributesBuilder result = new AttributesBuilder();
			for (Column column : columns) {
				Attribute attribute = column.readAttribute(excelReader, header);
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

	private static final String SHEET_SPELLS = "spells";
	private static final String SHEET_RANKS = "ranks";
	private static final String SHEET_TALENTS = "talents";
	private static final String SHEET_EFFECTS = "effects";
	private static final String SHEET_BUFFS = "buffs";

	private static final String COL_SPELL_SPELL = "spell";
	private static final String COL_SPELL_TREE = "tree";
	private static final String COL_SPELL_SCHOOL = "school";
	private static final String COL_SPELL_COEFF_DIRECT = "coeff direct";
	private static final String COL_SPELL_COEFF_DOT = "coeff dot";
	private static final String COL_SPELL_COOLDOWN = "cooldown";
	private static final String COL_SPELL_IGNORES_GCD = "ignores gcd";
	private static final String COL_SPELL_REQUITED_TALENT = "required talent";
	private static final String COL_SPELL_BOLT = "bolt";
	private static final String COL_SPELL_CONVERSION_FROM = "conversion: from";
	private static final String COL_SPELL_CONVERSION_TO = "conversion: to";
	private static final String COL_SPELL_CONVERSION_PCT = "conversion: %";
	private static final String COL_SPELL_REQUITED_EFFECT = "required effect";
	private static final String COL_SPELL_EFFECT_REMOVED_ON_HIT = "effect removed on hit";
	private static final String COL_SPELL_BONUS_DAMAGE_IF_UNDER_EFFECT = "bonus dmg if under effect";
	private static final String COL_SPELL_DOT_SCHEME = "dot scheme";

	private static final String COL_RANK_SPELL = "spell";
	private static final String COL_RANK_RANK = "rank";
	private static final String COL_RANK_LEVEL = "level";
	private static final String COL_RANK_MANA_COST = "mana cost";
	private static final String COL_RANK_CAST_TIME = "cast time";
	private static final String COL_RANK_CHANNELED = "channeled";
	private static final String COL_RANK_MIN_DMG = "min dmg";
	private static final String COL_RANK_MAX_DMG = "max dmg";
	private static final String COL_RANK_DOT_DMG = "dot dmg";
	private static final String COL_RANK_NUM_TICKS = "num ticks";
	private static final String COL_RANK_TICK_INTERVAL = "tick interval";
	private static final String COL_RANK_MIN_DMG2 = "min dmg2";
	private static final String COL_RANK_MAX_DMG2 = "max dmg2";
	private static final String COL_RANK_ADDITIONAL_COST_TYPE = "additional cost: type";
	private static final String COL_RANK_ADDITIONAL_COST_AMOUNT = "additional cost: amount";
	private static final String COL_RANK_ADDITIONAL_COST_SCALED = "additional cost: scaled";
	private static final String COL_RANK_APPLIED_EFFECT = "applied effect";
	private static final String COL_RANK_APPLIED_EFFECT_DURATION = "applied effect duration";

	private static final String COL_TALENT_TALENT = "talent";
	private static final String COL_TALENT_RANK = "rank";
	private static final String COL_TALENT_MAX_RANK = "max rank";
	private static final String COL_TALENT_DESCRIPTION = "description";

	private static final String COL_TALENT_TREE = "tree";
	private static final String COL_TALENT_SCHOOL = "school";
	private static final String COL_TALENT_SPELL = "spell";
	private static final String COL_TALENT_PET = "pet";

	private static final String COL_EFFECT_EFFECT = "effect";
	private static final String COL_EFFECT_FRIENDLY = "friendly";
	private static final String COL_EFFECT_SCOPE = "scope";
	private static final String COL_EFFECT_MAX_STACKS = "max stacks";
	private static final String COL_EFFECT_REMOVE_AFTER = "remove after";
	private static final String COL_EFFECT_REMOVE_AFTER_SCHOOL = "remove after: school";
	private static final String COL_EFFECT_ON_APPLY = "on apply";
	private static final String COL_EFFECT_TREE = "tree";
	private static final String COL_EFFECT_SCHOOL = "school";
	private static final String COL_EFFECT_SPELL = "spell";
	private static final String COL_EFFECT_PET = "pet";
	private static final String COL_EFFECT_STACK_SCALING = "stack scaling";

	private static final String COL_BUFF_ID = "id";
	private static final String COL_BUFF_NAME = "name";
	private static final String COL_BUFF_LEVEL = "level";
	private static final String COL_BUFF_TYPE = "type";
	private static final String COL_BUFF_EXCLUSION_GROUP = "exclusion group";
	private static final String COL_BUFF_STAT = "stat";
	private static final String COL_BUFF_AMOUNT = "amount";
	private static final String COL_BUFF_DURATION = "duration";
	private static final String COL_BUFF_COOLDOWN = "cooldown";
	private static final String COL_BUFF_DESCRIPTION = "description";
	private static final String COL_BUFF_SOURCE_SPELL = "source spell";

	private static final BenefitAttributeColumns TALENT_COLUMNS = new BenefitAttributeColumns()
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

	private static final BenefitAttributeColumns EFFECT_COLUMNS = new BenefitAttributeColumns()
			.add(AttributeId.effectIncreasePct, "effect increase%")
			.add(AttributeId.damageTakenIncreasePct, "damage taken increase%")
			.add(AttributeId.SpellDamage, "sp bonus")
			.add(AttributeId.castInstantly, "cast instanly")
			;


	private static void readSpells(ExcelReader excelReader, SpellDataRepositoryImpl spellDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_SPELL_SPELL, excelReader, header).isPresent()) {
				SpellInfo spellInfo = getSpellInfo(excelReader, header);

				if (spellDataRepository.getSpellInfo(spellInfo.getSpellId()) != null) {
					throw new IllegalArgumentException("Duplicate: " + spellInfo.getSpellId());
				}

				spellDataRepository.addSpellInfo(spellInfo);
			}
		}
	}

	private static SpellInfo getSpellInfo(ExcelReader excelReader, Map<String, Integer> header) {
		var spellId = SpellId.parse(getString(COL_SPELL_SPELL, excelReader, header));
		var talentTree = getEnum(COL_SPELL_TREE, TalentTree::parse, excelReader, header);
		var spellSchool = getOptionalEnum(COL_SPELL_SCHOOL, SpellSchool::parse, excelReader, header).orElse(null);
		var coeffDirect = getOptionalPercent(COL_SPELL_COEFF_DIRECT, excelReader, header).orElse(Percent.ZERO);
		var coeffDot = getOptionalPercent(COL_SPELL_COEFF_DOT, excelReader, header).orElse(Percent.ZERO);
		var cooldown = getOptionalDuration(COL_SPELL_COOLDOWN, excelReader, header).orElse(null);
		var ignoresGCD = getBoolean(COL_SPELL_IGNORES_GCD, excelReader, header);
		var requiredTalent = getOptionalString(COL_SPELL_REQUITED_TALENT, excelReader, header).map(TalentId::parse).orElse(null);
		var bolt = getBoolean(COL_SPELL_BOLT, excelReader, header);
		var requiredEffect = getOptionalString(COL_SPELL_REQUITED_EFFECT, excelReader, header).map(EffectId::parse).orElse(null);
		var effectRemovedOnHit = getOptionalString(COL_SPELL_EFFECT_REMOVED_ON_HIT, excelReader, header).map(EffectId::parse).orElse(null);
		var bonusDamageIfUnderEffect = getOptionalString(COL_SPELL_BONUS_DAMAGE_IF_UNDER_EFFECT, excelReader, header).map(EffectId::parse).orElse(null);
		var dotScheme = getList(COL_SPELL_DOT_SCHEME, Integer::parseInt, excelReader, header);
		var conversion = getConversion(excelReader, header);

		return new SpellInfo(spellId, talentTree, spellSchool, coeffDirect, coeffDot, cooldown, ignoresGCD, requiredTalent, bolt, conversion, requiredEffect, effectRemovedOnHit, bonusDamageIfUnderEffect, dotScheme);
	}

	private static Conversion getConversion(ExcelReader excelReader, Map<String, Integer> header) {
		var conversionFrom = getOptionalEnum(COL_SPELL_CONVERSION_FROM, Conversion.From::valueOf, excelReader, header).orElse(null);
		var conversionTo = getOptionalEnum(COL_SPELL_CONVERSION_TO, Conversion.To::valueOf, excelReader, header).orElse(null);

		if (conversionFrom != null && conversionTo != null) {
			var conversionPct = getPercent(COL_SPELL_CONVERSION_PCT, excelReader, header);
			return new Conversion(conversionFrom, conversionTo, conversionPct);
		} else if (conversionFrom != null || conversionTo != null) {
			throw new IllegalArgumentException("Conversion misses either from or to part");
		}

		return null;
	}

	private static void readRanks(ExcelReader excelReader, SpellDataRepositoryImpl spellDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_RANK_SPELL, excelReader, header).isPresent()) {
				SpellRankInfo spellRankInfo = getSpellRankInfo(excelReader, header);
				SpellInfo spellInfo = spellDataRepository.getSpellInfo(spellRankInfo.getSpellId());

				if (spellInfo == null) {
					throw new IllegalArgumentException("No spell: " + spellRankInfo.getSpellId());
				}

				if (spellInfo.getRanks().containsKey(spellRankInfo.getRank())) {
					throw new IllegalArgumentException("Duplicate rank: " + spellRankInfo.getSpellId() + " " + spellRankInfo.getRank());
				}

				spellInfo.getRanks().put(spellRankInfo.getRank(), spellRankInfo);
			}
		}
	}

	private static SpellRankInfo getSpellRankInfo(ExcelReader excelReader, Map<String, Integer> header) {
		var spellId = SpellId.parse(getString(COL_RANK_SPELL, excelReader, header));
		var rank = getOptionalInteger(COL_RANK_RANK, excelReader, header).orElse(0);
		var level = getInteger(COL_RANK_LEVEL, excelReader, header);
		var manaCost = getInteger(COL_RANK_MANA_COST, excelReader, header);
		var castTime = getDuration(COL_RANK_CAST_TIME, excelReader, header);
		var channeled = getBoolean(COL_RANK_CHANNELED, excelReader, header);
		var minDmg = getOptionalInteger(COL_RANK_MIN_DMG, excelReader, header).orElse(0);
		var maxDmg = getOptionalInteger(COL_RANK_MAX_DMG, excelReader, header).orElse(0);
		var dotDmg = getOptionalInteger(COL_RANK_DOT_DMG, excelReader, header).orElse(0);
		var numTicks = getOptionalInteger(COL_RANK_NUM_TICKS, excelReader, header).orElse(0);
		var tickInterval = getOptionalDuration(COL_RANK_TICK_INTERVAL, excelReader, header).orElse(null);
		var minDmg2 = getOptionalInteger(COL_RANK_MIN_DMG2, excelReader, header).orElse(0);
		var maxDmg2 = getOptionalInteger(COL_RANK_MAX_DMG2, excelReader, header).orElse(0);
		var appliedEffect = getOptionalString(COL_RANK_APPLIED_EFFECT, excelReader, header).map(EffectId::parse).orElse(null);
		var appliedEffectDuration = getOptionalDuration(COL_RANK_APPLIED_EFFECT_DURATION, excelReader, header).orElse(null);
		var additionalCost = getAdditionalCost(excelReader, header);

		if (appliedEffect != null && appliedEffectDuration == null) {
			appliedEffectDuration = Duration.INFINITE;
		}

		return new SpellRankInfo(spellId, rank, level, manaCost, castTime, channeled, minDmg, maxDmg, minDmg2, maxDmg2, dotDmg, numTicks, tickInterval, additionalCost, appliedEffect, appliedEffectDuration);
	}

	private static AdditionalCost getAdditionalCost(ExcelReader excelReader, Map<String, Integer> header) {
		var additionalCostType = getOptionalEnum(COL_RANK_ADDITIONAL_COST_TYPE, CostType::valueOf, excelReader, header).orElse(null);

		if (additionalCostType == null) {
			return null;
		}

		var additionalCostAmount = getInteger(COL_RANK_ADDITIONAL_COST_AMOUNT, excelReader, header);
		var additionalCostScaled = getBoolean(COL_RANK_ADDITIONAL_COST_SCALED, excelReader, header);

		return new AdditionalCost(additionalCostType, additionalCostAmount, additionalCostScaled);
	}

	private static void readTalents(ExcelReader excelReader, SpellDataRepositoryImpl spellDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_TALENT_TALENT, excelReader, header).isPresent()) {
				TalentInfo talentInfo = getTalentInfo(excelReader, header);
				Attributes talentBenefit = getTalentBenefit(excelReader, header);

				spellDataRepository.addTalent(talentInfo, talentBenefit);
			}
		}
	}

	private static TalentInfo getTalentInfo(ExcelReader excelReader, Map<String, Integer> header) {
		var talentName = TalentId.parse(getString(COL_TALENT_TALENT, excelReader, header));
		var rank = getInteger(COL_TALENT_RANK, excelReader, header);
		var maxRank = getInteger(COL_TALENT_MAX_RANK, excelReader, header);
		var description = getOptionalString(COL_TALENT_DESCRIPTION, excelReader, header).orElse(null);

		return new TalentInfo(talentName, rank, maxRank, description, Attributes.EMPTY);
	}

	private static Attributes getTalentBenefit(ExcelReader excelReader, Map<String, Integer> header) {
		var talentTree = getOptionalEnum(COL_TALENT_TREE, TalentTree::parse, excelReader, header).orElse(null);
		var spellSchool = getOptionalEnum(COL_TALENT_SCHOOL, SpellSchool::parse, excelReader, header).orElse(null);
		var spells = getSet(COL_TALENT_SPELL, SpellId::parse, excelReader, header);
		var petTypes = getSet(COL_TALENT_PET, PetType::parse, excelReader, header);

		return TALENT_COLUMNS.readAttributes(excelReader, header, talentTree, spellSchool, spells, petTypes);
	}

	private static void readEffects(ExcelReader excelReader, SpellDataRepositoryImpl spellDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_EFFECT_EFFECT, excelReader, header).isPresent()) {
				EffectInfo effectInfo = getEffectInfo(excelReader, header);
				spellDataRepository.addEffectInfo(effectInfo);
			}
		}
	}

	private static EffectInfo getEffectInfo(ExcelReader excelReader, Map<String, Integer> header) {
		var effectId = EffectId.parse(getString(COL_EFFECT_EFFECT, excelReader, header));

		var friendly = getBoolean(COL_EFFECT_FRIENDLY, excelReader, header);
		var scope = getOptionalEnum(COL_EFFECT_SCOPE, Scope::valueOf, excelReader, header).orElse(Scope.PERSONAL);
		var maxStacks = getOptionalInteger(COL_EFFECT_MAX_STACKS, excelReader, header).orElse(1);
		var onApply = getOptionalEnum(COL_EFFECT_ON_APPLY, OnApply::valueOf, excelReader, header).orElse(null);
		var stackScaling = getBoolean(COL_EFFECT_STACK_SCALING, excelReader, header);
		var attributes = getEffectAttributes(excelReader, header);
		var removeCondition = getRemoveCondition(excelReader, header);

		return new EffectInfo(effectId, friendly, scope, maxStacks, removeCondition, onApply, attributes, stackScaling);
	}

	private static Attributes getEffectAttributes(ExcelReader excelReader, Map<String, Integer> header) {
		var talentTree = getOptionalEnum(COL_EFFECT_TREE, TalentTree::parse, excelReader, header).orElse(null);
		var spellSchool = getOptionalEnum(COL_EFFECT_SCHOOL, SpellSchool::parse, excelReader, header).orElse(null);
		var spells = getSet(COL_EFFECT_SPELL, SpellId::parse, excelReader, header);
		var petTypes = getSet(COL_EFFECT_PET, PetType::parse, excelReader, header);

		return EFFECT_COLUMNS.readAttributes(excelReader, header, talentTree, spellSchool, spells, petTypes);
	}

	private static RemoveCondition getRemoveCondition(ExcelReader excelReader, Map<String, Integer> header) {
		var removeConditionEvent = getOptionalEnum(COL_EFFECT_REMOVE_AFTER, RemoveEvent::valueOf, excelReader, header).orElse(null);
		var removeConditionSpellSchool = getOptionalEnum(COL_EFFECT_REMOVE_AFTER_SCHOOL, SpellSchool::parse, excelReader, header).orElse(null);

		return RemoveCondition.create(removeConditionEvent, removeConditionSpellSchool);
	}

	private static void readBuffs(ExcelReader excelReader, SpellDataRepositoryImpl spellDataRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getOptionalString(COL_BUFF_NAME, excelReader, header).isPresent()) {
				Buff buff = getBuff(excelReader, header);
				spellDataRepository.addBuff(buff);
			}
		}
	}

	private static Buff getBuff(ExcelReader excelReader, Map<String, Integer> header) {
		var buffId = getInteger(COL_BUFF_ID, excelReader, header);
		var name = getString(COL_BUFF_NAME, excelReader, header);
		var level = getOptionalInteger(COL_BUFF_LEVEL, excelReader, header).orElse(0);
		var type = getEnum(COL_BUFF_TYPE, BuffType::valueOf, excelReader, header);
		var exclusionGroup = getOptionalEnum(COL_BUFF_EXCLUSION_GROUP, BuffExclusionGroup::valueOf, excelReader, header).orElse(null);
		var duration = getOptionalDuration(COL_BUFF_DURATION, excelReader, header).orElse(null);
		var cooldown = getOptionalDuration(COL_BUFF_COOLDOWN, excelReader, header).orElse(null);
		var description = getOptionalString(COL_BUFF_DESCRIPTION, excelReader, header).orElse(null);
		var sourceSpell = getOptionalEnum(COL_BUFF_SOURCE_SPELL, SpellId::parse, excelReader, header).orElse(null);
		var buffAttributes = getBuffAttributes(excelReader, header);

		return new Buff(buffId, name, level, type, exclusionGroup, buffAttributes, sourceSpell, duration, cooldown, description);
	}

	private static Attributes getBuffAttributes(ExcelReader excelReader, Map<String, Integer> header) {
		AttributesBuilder builder = new AttributesBuilder();
		int maxAttributes = 5;

		for (int statNo = 1; statNo <= maxAttributes; ++statNo) {
			var attributeStr = getOptionalString(COL_BUFF_STAT + statNo, excelReader, header);
			if (attributeStr.isPresent()) {
				SimpleAttributeParser attributeParser = new SimpleAttributeParser(attributeStr.get());
				int amount = getInteger(COL_BUFF_AMOUNT + statNo, excelReader, header);
				for (Attribute attribute : attributeParser.getAttributes(amount)) {
					builder.addAttribute(attribute);
				}
			}
		}

		return builder.toAttributes();
	}
}
