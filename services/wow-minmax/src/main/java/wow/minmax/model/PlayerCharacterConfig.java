package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Document("character")
@AllArgsConstructor
@Getter
@Setter
public class PlayerCharacterConfig {
	@Id
	private String characterId;
	private String name;
	private CharacterClassId characterClassId;
	private RaceId race;
	private int level;
	private PhaseId phaseId;
	private BuildConfig build;
	private EquipmentConfig equipment;
	private List<CharacterProfessionConfig> professions;
	private List<ExclusiveFaction> exclusiveFactions;
	private List<BuffConfig> buffs;
	private List<ConsumableConfig> consumables;
	private NonPlayerCharacterConfig target;

	public CharacterId getCharacterIdAsRecord() {
		return CharacterId.parse(characterId);
	}
}
