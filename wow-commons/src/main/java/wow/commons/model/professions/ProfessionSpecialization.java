package wow.commons.model.professions;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public enum ProfessionSpecialization {
	GnomishEngineer(Profession.Engineering),
	GoblinEngineer(Profession.Engineering),
	MasterSwordsmith(Profession.Blacksmithing),
	MoonclothTailoring(Profession.Tailoring),
	ShadoweaveTailoring(Profession.Tailoring),
	SpellfireTailoring(Profession.Tailoring),
	;

	private final Profession profession;

	ProfessionSpecialization(Profession profession) {
		this.profession = profession;
	}

	public Profession getProfession() {
		return profession;
	}
}
