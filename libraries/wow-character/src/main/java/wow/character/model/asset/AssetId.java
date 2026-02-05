package wow.character.model.asset;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
public record AssetId(int value) implements Comparable<AssetId> {
	public static AssetId of(int value) {
		return new AssetId(value);
	}

	@Override
	public int compareTo(AssetId other) {
		return Integer.compare(this.value, other.value);
	}
}
