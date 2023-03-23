package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.professions.Profession;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@AllArgsConstructor
@Getter
public enum WowheadProfession {
	ENCHANTING(333, Profession.ENCHANTING),
	JEWELCRAFTING(755, Profession.JEWELCRAFTING),
	ALCHEMY(171, Profession.ALCHEMY),
	TAILORING(197, Profession.TAILORING),
	LEATHERWORKING(165, Profession.LEATHERWORKING),
	BLACKSMITHING(164, Profession.BLACKSMITHING),
	ENGINEERING(202, Profession.ENGINEERING);

	private final int code;
	private final Profession profession;

	public static WowheadProfession fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
