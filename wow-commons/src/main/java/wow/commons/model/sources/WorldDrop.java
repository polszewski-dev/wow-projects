package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class WorldDrop extends Source {
	@Override
	public boolean isWorldDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "WorldDrop";
	}
}
