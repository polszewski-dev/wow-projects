package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.profession.ProfessionId;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
public enum WowheadProfession {
	ENCHANTING(333, ProfessionId.ENCHANTING),
	JEWELCRAFTING(755, ProfessionId.JEWELCRAFTING),
	ALCHEMY(171, ProfessionId.ALCHEMY),
	TAILORING(197, ProfessionId.TAILORING),
	LEATHERWORKING(165, ProfessionId.LEATHERWORKING),
	BLACKSMITHING(164, ProfessionId.BLACKSMITHING),
	ENGINEERING(202, ProfessionId.ENGINEERING);

	private final int code;
	private final ProfessionId profession;

	public static WowheadProfession fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
