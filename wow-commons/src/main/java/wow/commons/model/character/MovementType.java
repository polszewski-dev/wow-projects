package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-14
 */
@AllArgsConstructor
public enum MovementType {
	RUNNING("Running"),
	MOUNTED("Mounted"),
	FLYING("Flying"),
	SWIMMING("Swimming");

	private final String name;

	public static MovementType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static MovementType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
