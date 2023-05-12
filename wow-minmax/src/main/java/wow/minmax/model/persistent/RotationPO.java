package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.spells.SpellId;

import java.io.Serializable;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-10
 */
@Data
@AllArgsConstructor
public class RotationPO implements Serializable {
	private final List<SpellId> cooldowns;
	private final SpellId filler;
}
