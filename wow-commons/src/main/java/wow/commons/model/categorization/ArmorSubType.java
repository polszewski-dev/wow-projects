package wow.commons.model.categorization;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum ArmorSubType implements ItemSubType {
	Cloth("Cloth"),
	Leather("Leather"),
	Mail("Mail"),
	Plate("Plate");

	private final String tooltipText;

	ArmorSubType(String tooltipText) {
		this.tooltipText = tooltipText;
	}

	public static ArmorSubType tryParse(String line) {
		return Stream.of(values()).filter(x -> x.tooltipText.equals(line)).findAny().orElse(null);
	}
}
