package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
	private final List<CombatRatingInfo> combatRatingInfos = new ArrayList<>();

	public Phase getPhase(PhaseId phaseId) {
		if (phaseId == null) {
			return null;
		}
		return phases.stream()
				.filter(x -> x.getPhaseId() == phaseId)
				.findFirst()
				.orElseThrow();
	}

	public CharacterClass getCharacterClass(CharacterClassId characterClassId) {
		if (characterClassId == null) {
			return null;
		}
		return characterClasses.stream()
				.filter(x -> x.getCharacterClassId() == characterClassId)
				.findFirst()
				.orElseThrow();
	}

	public Race getRace(RaceId raceId) {
		if (raceId == null) {
			return null;
		}
		return races.stream()
				.filter(x -> x.getRaceId() == raceId)
				.findFirst()
				.orElseThrow();
	}

	public Profession getProfession(ProfessionId professionId) {
		if (professionId == null) {
			return null;
		}
		return professions.stream()
				.filter(x -> x.getProfessionId() == professionId)
				.findFirst()
				.orElseThrow();
	}

	public ProfessionProficiency getProficiency(ProfessionProficiencyId proficiencyId) {
		return proficiencies.stream()
				.filter(x -> x.getProficiencyId() == proficiencyId)
				.findFirst()
				.orElseThrow();
	}

	public Pet getPet(PetType petType) {
		if (petType == null) {
			return null;
		}
		return pets.stream()
				.filter(x -> x.getPetType() == petType)
				.findFirst()
				.orElseThrow();
	}

	public CombatRatingInfo getCombatRatingInfo(int level) {
		return combatRatingInfos.stream()
				.filter(x -> x.getLevel() == level)
				.findFirst()
				.orElseThrow();
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
