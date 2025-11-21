package wow.scraper.repository.impl.excel.spell;

import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.*;
import wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames;
import wow.scraper.parser.spell.SpellPatternType;
import wow.scraper.parser.spell.params.*;
import wow.scraper.repository.impl.excel.AbstractPatternSheetParser;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public abstract class AbstractSpellPatternSheetParser extends AbstractPatternSheetParser {
	private static final int MAX_MODIFIER_ATTRIBUTES = 3;
	private static final int MAX_EVENT_PATTERNS = 2;
	public static final String TRIGGER_PREFIX = "trigger: ";

	protected final SpellPatternExcelParser parser;

	protected AbstractSpellPatternSheetParser(String sheetName, SpellPatternExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	private final ExcelColumn colPatternType = column("type");

	protected SpellPatternType getPatternType(SpellPatternType defaultValue) {
		return colPatternType.getEnum(SpellPatternType::parse, defaultValue);
	}

	private final ExcelColumn colCastTime = column(CAST_TIME);
	private final ExcelColumn colCastChanneled = column(CAST_CHANNELED);
	private final ExcelColumn colCastIgnoresGcd = column(CAST_IGNORES_GCD);

	private CastParams getCastParams() {
		if (colCastTime.isEmpty()) {
			assertAllColumnsAreEmpty(colCastChanneled, colCastIgnoresGcd);
			return null;
		}

		var castTime = colCastTime.getString();
		var channeled = colCastChanneled.getBoolean();
		var ignoresGcd = colCastIgnoresGcd.getBoolean();

		return new CastParams(castTime, channeled, ignoresGcd);
	}

	private CostParams getCostParams() {
		var prefix = COST_PREFIX;
		var colAmount = column(COST_AMOUNT).prefixed(prefix);
		var colType = column(COST_TYPE).prefixed(prefix);

		if (colAmount.isEmpty()) {
			assertAllColumnsAreEmpty(colType);
			assertCoefficientEmpty(prefix);
			return null;
		}

		var amount = colAmount.getString();
		var type = colType.getEnum(ResourceType::parse);
		var coeff = getCoefficient(prefix);

		return new CostParams(amount, type, coeff);
	}

	private DirectComponentParams getDirectComponentParams(int idx) {
		var prefix = SpellBaseExcelColumnNames.getDirectComponentPrefix(idx);
		var optional = idx != 1;

		var colType = column(DIRECT_TYPE, optional).prefixed(prefix);
		var colMin = column(DIRECT_MIN, optional).prefixed(prefix);
		var colMax = column(DIRECT_MAX, optional).prefixed(prefix);
		var colBolt = column(DIRECT_BOLT, true).prefixed(prefix);

		if (hasEmptyTarget(prefix)) {
			assertAllColumnsAreEmpty(colType, colMin, colMax);
			assertCoefficientEmpty(prefix);
			return null;
		}

		var target = getTarget(prefix);
		var type = colType.getEnum(ComponentType::parse);
		var coeff = getCoefficient(prefix);
		var min = colMin.getString();
		var max = colMax.getString();
		var bonus = getDirectComponentBonusParams(prefix);
		var bolt = colBolt.getBoolean();

		return new DirectComponentParams(target, type, coeff, min, max, bonus, bolt);
	}

	private DirectComponentBonusParams getDirectComponentBonusParams(String prefix) {
		var colMinBonus = column(DIRECT_BONUS_MIN, true).prefixed(prefix);
		var colMaxBonus = column(DIRECT_BONUS_MAX, true).prefixed(prefix);
		var colBonusRequiredEffect = column(DIRECT_BONUS_REQUIRED_EFFECT, true).prefixed(prefix);

		if (colMinBonus.isEmpty()) {
			assertAllColumnsAreEmpty(colMinBonus, colMaxBonus, colBonusRequiredEffect);
			return null;
		}

		var min = colMinBonus.getString(null);
		var max = colMaxBonus.getString(null);
		var requiredEffect = colBonusRequiredEffect.getEnum(AbilityId::parse, null);

		return new DirectComponentBonusParams(min, max, requiredEffect);
	}

	private PeriodicComponentParams getPeriodicComponentParams() {
		var prefix = PERIODIC_PREFIX;
		var colType = column(PERIODIC_TYPE).prefixed(prefix);
		var colTotalAmount = column("total amount").prefixed(prefix);
		var colTickAmount = column("tick amount").prefixed(prefix);
		var colTickInterval = column(PERIODIC_TICK_INTERVAL).prefixed(prefix);
		var colTickWeights = column(PERIODIC_TICK_WEIGHTS, true).prefixed(prefix);

		if (hasEmptyTarget(prefix)) {
			assertAllColumnsAreEmpty(colType, colTotalAmount, colTickAmount, colTickInterval, colTickWeights);
			assertCoefficientEmpty(prefix);
			return null;
		}

		var target = getTarget(prefix);
		var type = colType.getEnum(ComponentType::parse);
		var coeff = getCoefficient(prefix);
		var totalAmount = colTotalAmount.getString(null);
		var tickAmount = colTickAmount.getString(null);
		var tickInterval = colTickInterval.getString(null);
		var tickWeights = colTickWeights.getString(null);

		return new PeriodicComponentParams(target, type, coeff, totalAmount, tickAmount, tickInterval, tickWeights);
	}

	private ModifierComponentParams getModifierComponentParams() {
		var prefix = MOD_PREFIX;

		var attributes = getAttributes(prefix, MAX_MODIFIER_ATTRIBUTES);

		if (attributes.isEmpty()) {
			assertTargetIsEmpty(prefix);
			return null;
		}

		var target = getTarget(prefix);

		return new ModifierComponentParams(target, attributes);
	}

	private final ExcelColumn colEffectDuration = column("duration");
	private final ExcelColumn colEffectCharges = column("charges");
	private final ExcelColumn colEffectStacksNum = column("stacks: #");
	private final ExcelColumn colEffectStacksMax = column(STACKS_MAX);

	private EffectApplicationParams getEffectApplicationParams() {
		var effect = readEffectPatternParams();

		if (!effect.hasAnyEffectComponents()) {
			assertAllColumnsAreEmpty(colEffectCharges, colEffectStacksNum);
			return null;
		}

		var duration = colEffectDuration.getString(null);
		var charges = colEffectCharges.getString(null);
		var stacksNum = colEffectStacksNum.getString(null);

		return new EffectApplicationParams(effect, duration, charges, stacksNum);
	}

	private AbsorptionComponentParams getAbsorptionComponentParams() {
		var prefix = ABSORB_PREFIX;
		var colCondition = column(ABSORB_CONDITION).prefixed(prefix);
		var colMin = column(ABSORB_MIN).prefixed(prefix);
		var colMax = column(ABSORB_MAX).prefixed(prefix);

		if (hasEmptyTarget(prefix)) {
			assertAllColumnsAreEmpty(colCondition, colMin, colMax);
			assertCoefficientEmpty(prefix);
			return null;
		}

		var target = getTarget(prefix);
		var coeff = getCoefficient(prefix);
		var condition = colCondition.getEnum(AbsorptionCondition::parse, AbsorptionCondition.EMPTY);
		var min = colMin.getString();
		var max = colMax.getString();

		return new AbsorptionComponentParams(target, coeff, condition, min, max);
	}

	private List<StatConversionParams> getStatConversionParams() {
		return IntStream.rangeClosed(1, MAX_STAT_CONVERSIONS)
				.mapToObj(this::getStatConversionParams)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colStatConversionFrom = column(STAT_CONVERSION_FROM);
	private final ExcelColumn colStatConversionTo = column(STAT_CONVERSION_TO);
	private final ExcelColumn colStatConversionToCondition = column(STAT_CONVERSION_TO_CONDITION);
	private final ExcelColumn colStatConversionRatio = column(STAT_CONVERSION_RATIO);

	private StatConversionParams getStatConversionParams(int idx) {
		var prefix = getStatConversionPrefix(idx);

		if (hasEmptyTarget(prefix)) {
			return null;
		}

		var target = getTarget(prefix);
		var from = colStatConversionFrom.prefixed(prefix).getEnum(AttributeId::parse);
		var to = colStatConversionTo.prefixed(prefix).getEnum(AttributeId::parse);
		var toCondition = colStatConversionToCondition.prefixed(prefix).getEnum(StatConversionCondition::parse, StatConversionCondition.EMPTY);
		var ratio = colStatConversionRatio.prefixed(prefix).getString();

		return new StatConversionParams(target, from, to, toCondition, ratio);
	}

	protected EventParams getEventParams(String prefix) {
		var colOn = column(EVENT_ON).prefixed(prefix);
		var colCondition = column(EVENT_CONDITION).prefixed(prefix);
		var colChancePct = column(EVENT_CHANCE_PCT).prefixed(prefix);
		var colAction = column(EVENT_ACTION).prefixed(prefix);
		var colCooldown = column(EVENT_COOLDOWN).prefixed(prefix);

		if (colOn.isEmpty()) {
			assertTargetIsEmpty(prefix);
			assertAllColumnsAreEmpty(colCondition, colChancePct, colAction, colCooldown);
			return null;
		}

		var target = getTarget(prefix);
		var eventTypes = colOn.getList(EventType::parse);
		var condition = colCondition.getEnum(EventCondition::parse, EventCondition.EMPTY);
		var chancePct = colChancePct.getString(null);
		var actions = colAction.getList(EventAction::parse);
		var cooldown = colCooldown.getString(null);

		return new EventParams(target, eventTypes, condition, chancePct, actions, cooldown, null);
	}

	private Coefficient getCoefficient(String prefix) {
		var colValue = column(COEFF_VALUE).prefixed(prefix);
		var colSchool = column(COEFF_SCHOOL).prefixed(prefix);

		var value = colValue.getPercent();
		var school = colSchool.getEnum(SpellSchool::parse, null);
		return new Coefficient(value, school);
	}

	private final ExcelColumn colAbilityCategory = column(ABILITY_CATEGORY, true);
	private final ExcelColumn colRequiredEffect = column(REQUIRED_EFFECT);
	private final ExcelColumn colEffectRemovedOnHit = column(EFFECT_REMOVED_ON_HIT);

	protected SpellPatternParams getSpellPatternParams() {
		var abilityCategory = colAbilityCategory.getEnum(AbilityCategory::parse, null);
		var cast = getCastParams();
		var cost = getCostParams();
		var requiredEffect = colRequiredEffect.getEnum(AbilityId::parse, null);
		var effectRemovedOnHit = colEffectRemovedOnHit.getEnum(AbilityId::parse, null);
		var directComponents = getDirectComponents();
		var effectApplication = getEffectApplicationParams();

		return new SpellPatternParams(
				abilityCategory,
				cast,
				cost,
				requiredEffect,
				effectRemovedOnHit,
				directComponents,
				effectApplication
		);
	}

	protected EffectPatternParams getEffectPatternParams() {
		return readEffectPatternParams();
	}

	private List<DirectComponentParams> getDirectComponents() {
		return IntStream.rangeClosed(1, MAX_DIRECT_COMPONENTS)
				.mapToObj(this::getDirectComponentParams)
				.filter(Objects::nonNull)
				.toList();
	}

	private final ExcelColumn colAugmentedAbility = column(AUGMENTED_ABILITY);

	private EffectPatternParams readEffectPatternParams() {
		var augmentedAbilities = colAugmentedAbility.getList(AbilityId::parse);
		var periodicComponent = getPeriodicComponentParams();
		var modifierComponent = getModifierComponentParams();
		var absorptionComponent = getAbsorptionComponentParams();
		var statConversions = getStatConversionParams();
		var events = getEventParamList();
		var stacksMax = colEffectStacksMax.getString(null);
		var duration = colEffectDuration.getString(null);

		var params = new EffectPatternParams(
				augmentedAbilities,
				periodicComponent,
				modifierComponent,
				absorptionComponent,
				statConversions,
				new TreeMap<>(),
				stacksMax,
				duration
		);

		for (int i = 0; i < events.size(); ++i) {
			params.setEvent(i, events.get(i));
		}

		return params;
	}

	private List<EventParams> getEventParamList() {
		return IntStream.rangeClosed(1, MAX_EVENT_PATTERNS)
				.mapToObj(SpellBaseExcelColumnNames::getEventPrefix)
				.map(this::getEventParams)
				.filter(Objects::nonNull)
				.toList();
	}

	private SpellTarget getTarget(String prefix) {
		if (prefix.equals(TRIGGER_PREFIX)) {
			return SpellTargets.SELF;
		}
		return getColTarget(prefix, false).getEnum(SpellTarget::parse, null);
	}

	private boolean hasEmptyTarget(String prefix) {
		return getColTarget(prefix, true).isEmpty();
	}

	private ExcelColumn getColTarget(String prefix, boolean optional) {
		return column(TARGET, optional).prefixed(prefix);
	}

	private void assertTargetIsEmpty(String prefix) {
		var colTarget = getColTarget(prefix, true);

		assertAllColumnsAreEmpty(colTarget);
	}

	private void assertCoefficientEmpty(String prefix) {
		var colValue = column(COEFF_VALUE, true).prefixed(prefix);
		var colSchool = column(COEFF_SCHOOL, true).prefixed(prefix);

		assertAllColumnsAreEmpty(colValue, colSchool);
	}
}
