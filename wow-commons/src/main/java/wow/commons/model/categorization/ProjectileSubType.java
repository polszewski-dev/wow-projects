package wow.commons.model.categorization;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-17
 */
public enum ProjectileSubType implements ItemSubType {
	Arrow("Arrow"),
	Bullet("Bullet"),
	;

	private final String tooltipText;

	ProjectileSubType(String tooltipText) {
		this.tooltipText = tooltipText;
	}

	public static ProjectileSubType tryParse(String line) {
		return Stream.of(values()).filter(x -> x.tooltipText.equals(line)).findAny().orElse(null);
	}
}
