package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-06-29
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class ContainedInItem extends Source {
	private final int id;
	private final String name;

	@Override
	public String toString() {
		return name;
	}
}
