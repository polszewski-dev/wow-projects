package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public enum BuildId {
	DESTRO_SHADOW;

	public static BuildId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
