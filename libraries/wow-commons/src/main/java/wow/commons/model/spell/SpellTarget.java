package wow.commons.model.spell;

import wow.commons.util.SpellTargetParser;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-11-21
 */
public record SpellTarget(
		SpellTargetType type,
		SpellTargetCondition condition
) {
	public SpellTarget {
		Objects.requireNonNull(type);
		Objects.requireNonNull(condition);
	}

	public SpellTarget(SpellTargetType type) {
		this(type, SpellTargetCondition.EMPTY);
	}

	public static SpellTarget of(SpellTargetType type) {
		return of(type, SpellTargetCondition.EMPTY);
	}

	public static SpellTarget of(SpellTargetType type, SpellTargetCondition condition) {
		return new SpellTarget(type, condition);
	}

	public static SpellTarget parse(String value) {
		return SpellTargetParser.parse(value);
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
