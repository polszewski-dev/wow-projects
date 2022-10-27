package wow.commons.model.item;

import lombok.Data;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.Race;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Data
public class ItemRestriction {
	private Integer requiredLevel;
	private Phase phase;
	private List<CharacterClass> classRestriction;
	private List<Race> raceRestriction;
	private Side sideRestriction;
	private Profession requiredProfession;
	private int requiredProfessionLevel;
	private ProfessionSpecialization requiredProfessionSpec;

	public boolean isMetBy(CharacterInfo characterInfo, Phase phase) {
		if (requiredLevel != null && characterInfo.getLevel() < requiredLevel) {
			return false;
		}
		if (this.phase != null && phase.isEarlier(this.phase)) {
			return false;
		}
		if (classRestriction != null && !classRestriction.isEmpty() && !classRestriction.contains(characterInfo.getCharacterClass())) {
			return false;
		}
		if (raceRestriction != null && !raceRestriction.isEmpty() && !raceRestriction.contains(characterInfo.getRace())) {
			return false;
		}
		if (sideRestriction != null && sideRestriction != characterInfo.getSide()) {
			return false;
		}
		if (requiredProfession != null) {
			// TODO
		}
		if (requiredProfessionSpec != null) {
			//TODO
		}
		return true;
	}
}
