package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.*;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionProficiencyId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@AllArgsConstructor
@Getter
public class GameVersion implements Described {
	@NonNull
	private final GameVersionId gameVersionId;

	@NonNull
	private final Description description;

	private final List<Double> basePveSpellHitChancesPct;
	private final double maxPveSpellHitChancePct;

	private final List<Phase> phases = new ArrayList<>();
	private final List<CharacterClass> characterClasses = new ArrayList<>();
	private final List<Race> races = new ArrayList<>();
	private final List<Profession> professions = new ArrayList<>();
	private final List<ProfessionProficiency> proficiencies = new ArrayList<>();
	private final List<Pet> pets = new ArrayList<>();

	public Optional<Phase> getPhase(PhaseId phaseId) {
		if (phaseId == null) {
			return Optional.empty();
		}
		return phases.stream()
				.filter(x -> x.getPhaseId() == phaseId)
				.findFirst();
	}

	public Optional<CharacterClass> getCharacterClass(CharacterClassId characterClassId) {
		if (characterClassId == null) {
			return Optional.empty();
		}
		return characterClasses.stream()
				.filter(x -> x.getCharacterClassId() == characterClassId)
				.findFirst();
	}

	public Optional<Race> getRace(RaceId raceId) {
		if (raceId == null) {
			return Optional.empty();
		}
		return races.stream()
				.filter(x -> x.getRaceId() == raceId)
				.findFirst();
	}

	public Optional<Profession> getProfession(ProfessionId professionId) {
		if (professionId == null) {
			return Optional.empty();
		}
		return professions.stream()
				.filter(x -> x.getProfessionId() == professionId)
				.findFirst();
	}

	public Optional<ProfessionProficiency> getProficiency(ProfessionProficiencyId proficiencyId) {
		return proficiencies.stream()
				.filter(x -> x.getProficiencyId() == proficiencyId)
				.findFirst();
	}

	public Optional<Pet> getPet(PetType petType) {
		return pets.stream()
				.filter(x -> x.getPetType() == petType)
				.findFirst();
	}

	public boolean supports(CharacterClassId characterClassId, RaceId raceId) {
		return characterClasses.stream().anyMatch(x -> x.getCharacterClassId() == characterClassId && x.isAvailableTo(raceId));
	}

	public Phase getLastPhase() {
		return phases.stream()
				.max(Comparator.comparing(Phase::getPhaseId))
				.orElseThrow();
	}

	public double getBaseSpellHitChancePct(int levelDifference) {
		return basePveSpellHitChancesPct.get(levelDifference);
	}

	@Override
	public String toString() {
		return getName();
	}
}
