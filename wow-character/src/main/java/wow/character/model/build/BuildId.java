package wow.character.model.build;

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
	DESTRO_SHADOW,
	SHADOW;

	public static BuildId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
