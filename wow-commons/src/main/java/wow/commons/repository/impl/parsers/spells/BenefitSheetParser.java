package wow.commons.repository.impl.parsers.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.complex.modifiers.ProcEvent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.effects.EffectId;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.PetType;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ExcelSheetParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public abstract class BenefitSheetParser extends WowExcelSheetParser {
	protected BenefitSheetParser(String sheetName) {
		super(sheetName);
	}

	protected interface Column {
		void addAttribute(AttributesBuilder builder, List<AttributeCondition> conditions);
	}

	private class SimpleColumn implements Column {
		final PrimitiveAttributeId id;
		final ExcelSheetParser.ExcelColumn name;

		SimpleColumn(String name, PrimitiveAttributeId id) {
			this.id = id;
			this.name = column(name);
		}

		private PrimitiveAttribute readAttribute() {
			var value = name.getDouble(0);
			return Attribute.ofNullable(id, value);
		}

		@Override
		public void addAttribute(AttributesBuilder builder, List<AttributeCondition> conditions) {
			PrimitiveAttribute attribute = readAttribute();
			if (attribute == null) {
				return;
			}
			for (AttributeCondition condition : conditions) {
				builder.addAttribute(attribute.attachCondition(condition));
			}
		}
	}

	private abstract class ComplexColumn implements Column {
		protected abstract ComplexAttribute readAttribute();

		@Override
		public void addAttribute(AttributesBuilder builder, List<AttributeCondition> conditions) {
			ComplexAttribute attribute = readAttribute();
			if (attribute == null) {
				return;
			}
			for (AttributeCondition condition : conditions) {
				builder.addAttribute(attribute.attachCondition(condition));
			}
		}
	}

	private class StatConversionColumn extends ComplexColumn {
		final ExcelColumn colConversionFrom;
		final ExcelColumn colConversionTo;
		final ExcelColumn colConversionRationPct;

		StatConversionColumn(String colConversionFrom, String colConversionTo, String colConversionRationPct) {
			this.colConversionFrom = column(colConversionFrom);
			this.colConversionTo = column(colConversionTo);
			this.colConversionRationPct = column(colConversionRationPct);
		}

		@Override
		protected StatConversion readAttribute() {
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
	}

	private class ProcTriggerColumn extends ComplexColumn {
		final ExcelColumn colType;
		final ExcelColumn colChancePct;
		final ExcelColumn colEffect;
		final ExcelColumn colDuration;
		final ExcelColumn colStacks;

		ProcTriggerColumn(String colType, String colChancePct, String colEffect, String colDuration, String colStacks) {
			this.colType = column(colType);
			this.colChancePct = column(colChancePct);
			this.colEffect = column(colEffect);
			this.colDuration = column(colDuration);
			this.colStacks = column(colStacks);
		}

		@Override
		protected SpecialAbility readAttribute() {
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

	private class EffectIncreasePerEffectOnTargetColumn extends ComplexColumn {
		final ExcelColumn colEffectTree;
		final ExcelColumn colIncreasePerEffectPct;
		final ExcelColumn colMaxIncreasePct;

		EffectIncreasePerEffectOnTargetColumn(String colEffectTree, String colIncreasePerEffectPct, String colMaxIncreasePct) {
			this.colEffectTree = column(colEffectTree);
			this.colIncreasePerEffectPct = column(colIncreasePerEffectPct);
			this.colMaxIncreasePct = column(colMaxIncreasePct);
		}

		@Override
		protected EffectIncreasePerEffectOnTarget readAttribute() {
			var effectTree = colEffectTree.getEnum(TalentTree::parse, null);

			if (effectTree == null) {
				return null;
			}

			var increasePerEffectPct = colIncreasePerEffectPct.getPercent();
			var maxIncreasePct = colMaxIncreasePct.getPercent();

			return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, AttributeCondition.EMPTY);
		}
	}

	private final List<Column> columns = new ArrayList<>();

	protected BenefitSheetParser add(PrimitiveAttributeId id, String name) {
		columns.add(new SimpleColumn(name, id));
		return this;
	}

	protected BenefitSheetParser addStatConversion(
			String colConversionFrom,
			String colConversionTo,
			String colConversionRationPct
	) {
		columns.add(new StatConversionColumn(colConversionFrom, colConversionTo, colConversionRationPct));
		return this;
	}

	protected BenefitSheetParser addProcTrigger(
			String colType,
			String colChancePct,
			String colEffect,
			String colDuration,
			String colStacks
	) {
		columns.add(new ProcTriggerColumn(colType, colChancePct, colEffect, colDuration, colStacks));
		return this;
	}

	protected BenefitSheetParser addEffectIncreasePerEffectOnTarget(
			String colEffectTree,
			String colIncreasePerEffectPct,
			String colMaxIncreasePct
	) {
		columns.add(new EffectIncreasePerEffectOnTargetColumn(colEffectTree, colIncreasePerEffectPct, colMaxIncreasePct));
		return this;
	}

	protected Attributes readAttributes(TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes) {
		AttributesBuilder result = new AttributesBuilder();

		for (Column column : columns) {
			var conditions = getPossibleConditions(talentTree, spellSchool, spells, petTypes);
			column.addAttribute(result, conditions);
		}

		return result.toAttributes();
	}

	private List<AttributeCondition> getPossibleConditions(TalentTree talentTree, SpellSchool spellSchool, Set<SpellId> spells, Set<PetType> petTypes) {
		if (talentTree != null) {
			if (spellSchool == null && spells.isEmpty() && petTypes.isEmpty()) {
				return List.of(AttributeCondition.of(talentTree));
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (spellSchool != null) {
			if (spells.isEmpty() && petTypes.isEmpty()) {
				return List.of(AttributeCondition.of(spellSchool));
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (!spells.isEmpty()) {
			if (petTypes.isEmpty()) {
				return spells.stream()
						.map(AttributeCondition::of)
						.collect(Collectors.toList());
			} else {
				throw new IllegalArgumentException();
			}
		}

		if (!petTypes.isEmpty()) {
			return petTypes.stream()
					.map(AttributeCondition::of)
					.collect(Collectors.toList());
		}

		return List.of(AttributeCondition.EMPTY);
	}
}
