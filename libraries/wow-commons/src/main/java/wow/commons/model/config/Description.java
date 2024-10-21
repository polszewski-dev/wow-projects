package wow.commons.model.config;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public record Description(String name, String icon, String tooltip) {
	public Description {
		Objects.requireNonNull(name);
	}
}
