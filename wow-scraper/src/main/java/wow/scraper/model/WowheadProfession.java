package wow.scraper.model;

import wow.commons.model.professions.Profession;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
public enum WowheadProfession {
	Enchanting(333, Profession.Enchanting),
	Jewelcrafting(755, Profession.Jewelcrafting),
	Alchemy(171, Profession.Alchemy),
	Tailoring(197, Profession.Tailoring),
	Leatherworking(165, Profession.Leatherworking),
	Blacksmithing(164, Profession.Blacksmithing),
	Engineering(202, Profession.Engineering),
	;

	private final int code;
	private final Profession profession;

	WowheadProfession(int code, Profession profession) {
		this.code = code;
		this.profession = profession;
	}

	public static WowheadProfession fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}

	public int getCode() {
		return code;
	}

	public Profession getProfession() {
		return profession;
	}
}
