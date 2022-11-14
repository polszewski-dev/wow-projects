package wow.minmax.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;
import wow.minmax.model.PVERole;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class PlayerProfileDTO {
	private final UUID profileId;
	private final String profileName;

	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final CreatureType enemyType;

	private final PVERole role;
	private final Phase phase;

	private EquipmentDTO equipment;
	private List<BuffDTO> buffs;
	private List<TalentDTO> talents;

	private LocalDateTime lastModified;

	private Map<ItemSlot, List<ItemDTO>> availableItemsBySlot;

	@JsonIgnore
	public Collection<EquippableItemDTO> getEquippedItems() {
		return equipment.getItemsBySlot().values();
	}
}
