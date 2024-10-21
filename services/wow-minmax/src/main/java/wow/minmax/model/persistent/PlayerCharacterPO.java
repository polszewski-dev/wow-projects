package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.io.Serializable;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Document("character")
@AllArgsConstructor
@Getter
@Setter
public class PlayerCharacterPO implements Serializable {
	@Id
	private String characterId;
	private CharacterClassId characterClassId;
	private RaceId race;
	private int level;
	private PhaseId phaseId;
	private BuildPO build;
	private EquipmentPO equipment;
	private List<CharacterProfessionPO> professions;
	private List<ExclusiveFaction> exclusiveFactions;
	private List<BuffPO> buffs;
	private NonPlayerCharacterPO target;
}
