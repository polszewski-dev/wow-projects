package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.NpcType;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-07-15
 */
@AllArgsConstructor
@Getter
public enum WowheadNpcClassification {
	NORMAL(0, NpcType.NORMAL),
	ELITE(1, NpcType.ELITE),
	RARE(4, NpcType.RARE),
	RARE_ELITE(2, NpcType.RARE_ELITE),
	BOSS(3, NpcType.BOSS);

	private final int code;
	private final NpcType type;

	public static WowheadNpcClassification fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
