package wow.commons.model.pve;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Faction implements TimeRestricted {
	private final int id;
	private final String name;
	private final GameVersionId version;
	private final Side side;
	private final TimeRestriction timeRestriction;

	@Override
	public String toString() {
		return name;
	}
}
