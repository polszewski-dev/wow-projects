package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2021-04-03
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class PvP extends Source {
	@Override
	public boolean isPvP() {
		return true;
	}

	@Override
	public String toString() {
		return "PvP";
	}
}
