package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.pve.Zone;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-29
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class ContainedInObject extends Source {
	private final int id;
	private final String name;
	private final List<Zone> zones;

	@Override
	public String toString() {
		return name;
	}
}
