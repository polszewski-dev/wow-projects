package wow.character.model.script;

import wow.commons.util.condition.ConditionParser;

import java.util.HashMap;
import java.util.Map;

import static wow.character.model.script.ScriptCommandCondition.*;
import static wow.character.model.script.ScriptCommandConditionStrings.*;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public class ScriptCommandConditionParser extends ConditionParser<ScriptCommandCondition, Expression> {
	private final Map<String, String> stringMacros;
	private final Map<String, Node> parsedMacros = new HashMap<>();

	public ScriptCommandConditionParser(String value, Map<String, String> macros) {
		super(value);
		this.stringMacros = macros;
	}

	@Override
	public ScriptCommandCondition parse() {
		parseMacros();

		return super.parse();
	}

	private void parseMacros() {
		for (var entry : stringMacros.entrySet()) {
			var name = entry.getKey();
			var definition = entry.getValue();
			var parsedDefinition = new Parser(definition).parse();

			parsedMacros.put(name, parsedDefinition);
		}
	}

	@Override
	protected ScriptCommandCondition orOperator(ScriptCommandCondition left, ScriptCommandCondition right) {
		return new Or(left, right);
	}

	@Override
	protected ScriptCommandCondition andOperator(ScriptCommandCondition left, ScriptCommandCondition right) {
		return new And(left, right);
	}

	@Override
	protected ScriptCommandCondition notOperator(ScriptCommandCondition condition) {
		return new Not(condition);
	}

	@Override
	protected ScriptCommandCondition lessThanOperator(Expression left, Expression right) {
		return new LessThan(left, right);
	}

	@Override
	protected ScriptCommandCondition lessThanOrEqualOperator(Expression left, Expression right) {
		return new LessThanOrEqual(left, right);
	}

	@Override
	protected ScriptCommandCondition greaterThanOperator(Expression left, Expression right) {
		return new GreaterThan(left, right);
	}

	@Override
	protected ScriptCommandCondition greaterThanOrEqualOperator(Expression left, Expression right) {
		return new GreaterThanOrEqual(left, right);
	}

	@Override
	protected ScriptCommandCondition getBasicCondition(String value) {
		var macro = parsedMacros.get(value);

		if (macro != null) {
			return transform(macro);
		}

		return basicConditionChain
				.tryParse(value)
				.orElseThrow(() -> new IllegalArgumentException("Invalid condition: " + value));
	}

	private final ParseChain<ScriptCommandCondition> basicConditionChain = new ParseChain<>(
			this::parseCasterHasEffect,
			this::parseTargetHasEffect,
			this::parseCanCastMoreBeforeSimulationEnds,
			this::parseAssetEnabled
	);

	@Override
	protected Expression getBasicExpression(String value) {
		var macro = parsedMacros.get(value);

		if (macro != null) {
			return expressionTransform(macro);
		}

		return basicExpressionChain.tryParse(value).orElse(null);
	}

	private final ParseChain<Expression> basicExpressionChain = new ParseChain<>(
			parseConstant(CASTER_HEALTH, CasterHealth::new),
			parseConstant(CASTER_MANA, CasterMana::new),
			parseConstant(CASTER_HEALTH_PCT, CasterHealthPct::new),
			parseConstant(CASTER_MANA_PCT, CasterManaPct::new),
			parseConstant(TARGET_HEALTH_PCT, TargetHealthPct::new),
			parseConstant(TARGET_MANA_PCT, TargetManaPct::new),
			parseConstant(FOCUS_HEALTH_PCT, FocusHealthPct::new),
			parseConstant(FOCUS_MANA_PCT, FocusManaPct::new),
			parseConstant(PET_HEALTH_PCT, PetHealthPct::new),
			parseConstant(PET_MANA_PCT, PetManaPct::new),
			parseConstant(MASTER_HEALTH_PCT, MasterHealthPct::new),
			parseConstant(MASTER_MANA_PCT, MasterManaPct::new),
			parseConstant(FULL_DURATION, FullDuration::new),
			parseConstant(REMAINING_SIMULATION_DURATION, RemainingSimulationDuration::new),
			this::parseRemainingCooldown,
			this::parseTargetNumAnyStacks,
			this::parseTargetRemainingAnyEffectDuration
	);

	@Override
	protected Constant getConstant(double value) {
		return new Constant(value);
	}

	private CasterHasEffect parseCasterHasEffect(String value) {
		return parseStringArgument(value, CASTER_HAS_EFFECT, CasterHasEffect::new);
	}

	private TargetHasEffect parseTargetHasEffect(String value) {
		return parseStringArgument(value, TARGET_HAS_EFFECT, TargetHasEffect::new);
	}

	private CanCastMoreBeforeSimulationEnds parseCanCastMoreBeforeSimulationEnds(String value) {
		return parseAbilityIdArgument(value, CAN_CAST_MORE_BEFORE_SIMULATION_ENDS, CanCastMoreBeforeSimulationEnds::new);
	}

	private AssetEnabled parseAssetEnabled(String value) {
		return parseStringArgument(value, ASSET_ENABLED, AssetEnabled::new);
	}

	private RemainingCooldown parseRemainingCooldown(String value) {
		return parseAbilityIdArgument(value, REMAINING_COOLDOWN, RemainingCooldown::new);
	}

	private TargetNumStacksOfAny parseTargetNumAnyStacks(String value) {
		return parseStringArgument(value, TARGET_NUM_STACKS_OF_ANY, TargetNumStacksOfAny::new);
	}

	private TargetRemainingEffectDurationOfAny parseTargetRemainingAnyEffectDuration(String value) {
		return parseStringArgument(value, TARGET_REMAINING_EFFECT_DURATION_OF_ANY, TargetRemainingEffectDurationOfAny::new);
	}

	@Override
	protected ScriptCommandCondition getEmptyCondition() {
		return EMPTY;
	}
}
