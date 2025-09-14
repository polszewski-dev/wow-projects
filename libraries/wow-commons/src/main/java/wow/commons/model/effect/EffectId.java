package wow.commons.model.effect;

/**
 * User: POlszewski
 * Date: 2025-09-14
 */
public record EffectId(int value) implements Comparable<EffectId> {
	public static EffectId of(int value) {
		return new EffectId(value);
	}

	@Override
	public int compareTo(EffectId other) {
		return Integer.compare(this.value, other.value);
	}
}
