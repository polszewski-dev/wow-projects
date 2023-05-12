package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.PetType;

import java.io.Serializable;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@AllArgsConstructor
public class BuildPO implements Serializable {
	private List<TalentPO> talents;
	private PveRole role;
	private RotationPO rotation;
	private PetType activePet;
}
