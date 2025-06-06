package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.PetType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@Getter
@Setter
public class BuildPO {
	private List<TalentPO> talents;
	private PveRole role;
	private String rotation;
	private PetType activePet;
}
