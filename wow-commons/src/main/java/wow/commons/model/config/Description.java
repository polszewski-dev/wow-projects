package wow.commons.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
@AllArgsConstructor
@Getter
public class Description {
	@NonNull
	private final String name;
	private final String icon;
	private final String tooltip;
}
