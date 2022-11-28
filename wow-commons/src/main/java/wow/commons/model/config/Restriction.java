package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.Race;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
@Builder
public class Restriction {
	private Phase phase;
	private int requiredLevel;
	private List<CharacterClass> requiredClass;
	private List<Race> requiredRace;
	private Side requiredSide;
	private Profession requiredProfession;
	private int requiredProfessionLevel;
	private ProfessionSpecialization requiredProfessionSpec;
	private TalentId requiredTalent;

	public boolean isMetBy(CharacterInfo characterInfo, Phase phase) {
		if (characterInfo.getLevel() < requiredLevel) {
			return false;
		}
		if (this.phase != null && phase.isEarlier(this.phase)) {
			return false;
		}
		if (requiredClass != null && !requiredClass.isEmpty() && !requiredClass.contains(characterInfo.getCharacterClass())) {
			return false;
		}
		if (requiredRace != null && !requiredRace.isEmpty() && !requiredRace.contains(characterInfo.getRace())) {
			return false;
		}
		if (requiredSide != null && requiredSide != characterInfo.getSide()) {
			return false;
		}
		if (requiredProfession != null && !characterInfo.hasProfession(requiredProfession, requiredProfessionLevel)) {
			return false;
		}
		if (requiredProfessionSpec != null && !characterInfo.hasProfessionSpecialization(requiredProfessionSpec)) {
			return false;
		}
		return requiredTalent == null || characterInfo.hasTalent(requiredTalent);
	}

	public Restriction merge(Restriction secondary) {
		RestrictionBuilder builder = builder();

		builder.phase(phase != null ? phase : secondary.phase);

		builder.requiredLevel(requiredLevel > 0 ? requiredLevel : secondary.requiredLevel);

		if (requiredClass != null && !requiredClass.isEmpty()) {
			builder.requiredClass(requiredClass);
		} else {
			builder.requiredClass(secondary.requiredClass);
		}

		if (requiredRace != null && !requiredRace.isEmpty()) {
			builder.requiredRace(requiredRace);
		} else {
			builder.requiredRace(secondary.requiredRace);
		}

		builder.requiredSide(requiredSide != null ? requiredSide : secondary.requiredSide);

		if (requiredProfession != null) {
			builder
					.requiredProfession(requiredProfession)
					.requiredProfessionLevel(requiredProfessionLevel)
					.requiredProfessionSpec(requiredProfessionSpec);
		} else {
			builder
					.requiredProfession(secondary.requiredProfession)
					.requiredProfessionLevel(secondary.requiredProfessionLevel)
					.requiredProfessionSpec(secondary.requiredProfessionSpec);
		}

		builder.requiredTalent(requiredTalent != null ? requiredTalent : secondary.requiredTalent);

		return builder.build();
	}
}
