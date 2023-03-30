package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "name")
public class Faction {
	private final int no;
	private final String name;
	private final GameVersionId version;
	private final PhaseId phaseId;

	@Override
	public String toString() {
		return name;
	}
}
