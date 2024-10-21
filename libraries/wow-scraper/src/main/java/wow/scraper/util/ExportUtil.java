package wow.scraper.util;

import wow.commons.model.config.Described;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-10-07
 */
public final class ExportUtil {
	public static Description getDescription(Described described, int level) {
		var description = described.getDescription();
		if (level == 0) {
			return description;
		}
		return new Description(
				description.name() + " - triggered".repeat(level),
				null,
				null
		);
	}

	public enum SourceType {
		ABILITY,
		ITEM,
		ITEM_SET
	}

	public static int getId(int baseId, SourceType sourceType, int effectIdx, int level, int index) {
		int typeOffset = getTypeOffset(sourceType);
		int effectIdxOffset = effectIdx * 1_000_000;
		int indexOffset = index * 10_000_000;
		int levelOffset = level * 100_000_000;
		return baseId + typeOffset + effectIdxOffset + indexOffset + levelOffset;
	}

	private static int getTypeOffset(SourceType sourceType) {
		if (sourceType == null) {
			return 0;
		}
		return switch (sourceType) {
			case ABILITY -> 0;
			case ITEM -> 100_000;
			case ITEM_SET -> 200_000;
		};
	}

	private ExportUtil() {}
}
