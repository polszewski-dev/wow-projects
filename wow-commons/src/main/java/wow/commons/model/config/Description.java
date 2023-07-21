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

	public Description merge(Description secondary) {
		return new Description(
				name,
				icon != null ? icon : secondary.icon,
				tooltip != null ? tooltip : secondary.tooltip
		);
	}
}
