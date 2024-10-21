package wow.commons.model.spell;

import wow.commons.model.Percent;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.component.EffectComponent;
import wow.commons.model.spell.component.SpellComponent;
import wow.commons.util.EnumUtil;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Conversion(
		AttributeCondition condition,
		From from,
		To to,
		Percent ratioPct
) implements SpellComponent, EffectComponent {
	public Conversion {
		Objects.requireNonNull(condition);
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(ratioPct);
	}

	public enum From {
		DAMAGE_DONE,
		HEALING_DONE,
		MANA_DRAINED,
		MANA_GAINED,
		HEALTH_PAID,
		MANA_PAID,
		DAMAGE_ABSORBED;

		public static From parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public enum To {
		HEALTH,
		MANA,
		PARTY_HEALTH,
		PARTY_MANA,
		PET_HEALTH,
		PET_MANA,
		DAMAGE_ON_TARGET,
		DAMAGE_ON_ATTACKER;

		public static To parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public boolean isLocal() {
		return condition.isEmpty();
	}
}
