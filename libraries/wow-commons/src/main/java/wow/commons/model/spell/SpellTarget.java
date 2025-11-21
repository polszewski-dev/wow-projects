package wow.commons.model.spell;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-11-21
 */
public record SpellTarget(
		SpellTargetType type
) {
	public SpellTarget {
		Objects.requireNonNull(type);
	}

	public static SpellTarget of(SpellTargetType type) {
		return new SpellTarget(type);
	}

	public static SpellTarget parse(String value) {
		if (value == null) {
			return null;
		}
		return of(SpellTargetType.parse(value));
	}

	public boolean hasType(SpellTargetType type) {
		return this.type == type;
	}

	public boolean isAoE() {
		return type.isAoE();
	}

	public boolean isSingle() {
		return type.isSingle();
	}
}
