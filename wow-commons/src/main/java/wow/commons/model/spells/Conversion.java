package wow.commons.model.spells;

import wow.commons.model.Percent;
import wow.commons.util.EnumUtil;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Conversion(
		From from,
		To to,
		Percent ratioPct
) {
	public Conversion {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(ratioPct);
	}

	public enum From {
		DAMAGE_DONE,
		HEALTH_PAID,
		MANA_PAID;

		public static From parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public enum To {
		HEALTH,
		MANA;

		public static To parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}
}
