package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.pve.GameVersionId;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
public enum WowheadGameVersion {
	VANILLA(0, GameVersionId.VANILLA),
	TBC(1, GameVersionId.TBC),
	WOTLK(2, GameVersionId.WOTLK);

	private final int code;
	private final GameVersionId gameVersion;

	public static WowheadGameVersion fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
