package wow.commons.model.talent;

/**
 * User: POlszewski
 * Date: 2025-09-13
 */
public record TalentId(int value) implements Comparable<TalentId> {
	public static TalentId of(int value) {
		return new TalentId(value);
	}

	@Override
	public int compareTo(TalentId other) {
		return Integer.compare(this.value, other.value);
	}
}
