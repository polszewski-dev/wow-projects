package wow.commons.util.condition;

import wow.commons.model.character.CreatureType;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;

import static wow.commons.model.spell.SpellTargetCondition.*;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public class SpellTargetConditionParser extends ConditionParser<SpellTargetCondition, Void> {
	public static SpellTargetCondition parseCondition(String value) {
		return new SpellTargetConditionParser(value).parse();
	}

	private SpellTargetConditionParser(String value) {
		super(value);
	}

	@Override
	protected SpellTargetCondition commaOperator(List<SpellTargetCondition> conditions) {
		return comma(conditions);
	}

	@Override
	protected SpellTargetCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return EMPTY;
		}

		var creatureType = CreatureType.tryParse(value);

		if (creatureType != null) {
			return SpellTargetCondition.of(creatureType);
		}

		var hasEffect = tryParseHasEffect(value);

		if (hasEffect != null) {
			return hasEffect;
		}

		var healthBelowPct = tryParseHealthAtMostPct(value);

		if (healthBelowPct != null) {
			return healthBelowPct;
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected SpellTargetCondition getEmptyCondition() {
		return EMPTY;
	}

	private HasEffect tryParseHasEffect(String value) {
		if (value != null && value.startsWith(HAS_EFFECT_PREFIX)) {
			var abilityIdStr = withoutPrefix(value, HAS_EFFECT_PREFIX);
			var abilityId = AbilityId.parse(abilityIdStr);

			return new HasEffect(abilityId);
		}

		return null;
	}

	private HealthAtMostPct tryParseHealthAtMostPct(String value) {
		var healthPct = parsePercent(HEALTH_AT_MOST_PREFIX, value);

		if (healthPct == null) {
			return null;
		}

		return new HealthAtMostPct(healthPct);
	}

	static final String HAS_EFFECT_PREFIX = "HasEffect:";
	static final String HEALTH_AT_MOST_PREFIX = "HealthAtMost";
}
