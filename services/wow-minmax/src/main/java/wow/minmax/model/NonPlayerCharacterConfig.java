package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@Getter
@Setter
public class NonPlayerCharacterConfig {
	private String name;
	private PhaseId phaseId;
	private CharacterClassId characterClassId;
	private CreatureType creatureType;
	private int level;
	private List<BuffConfig> debuffs;
}
