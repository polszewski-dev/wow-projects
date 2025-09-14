package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2025-09-14
 */
public record SpellId(int value) implements Comparable<SpellId> {
	public static SpellId of(int value) {
		return new SpellId(value);
	}

	public static SpellId ofNullable(Integer value) {
		if (value == null) {
			return null;
		}
		return new SpellId(value);
	}

	@Override
	public int compareTo(SpellId other) {
		return Integer.compare(this.value, other.value);
	}
}
