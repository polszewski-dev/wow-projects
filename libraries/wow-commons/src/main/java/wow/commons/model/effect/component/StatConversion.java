package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		StatConversionType type,
		Percent ratioPct
) implements EffectComponent {
	public StatConversion {
		Objects.requireNonNull(type);
		Objects.requireNonNull(ratioPct);
	}

	public AttributeId to() {
		return type.getTo();
	}

	public boolean isFromOwner() {
		return switch (type) {
			case
					OWNER_INTELLECT_TO_SPELL_POWER,
					OWNER_INTELLECT_TO_SPELL_DAMAGE,
					OWNER_INTELLECT_TO_SPELL_HEALING,
					OWNER_INTELLECT_TO_MP5,
					OWNER_INTELLECT_TO_ARMOR,
					OWNER_SPIRIT_TO_SPELL_POWER
			-> true;
			default -> false;
		};
	}
}
