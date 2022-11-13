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
	private final GameVersion version;
	private final Phase phase;

	@Override
	public String toString() {
		return name;
	}
}
