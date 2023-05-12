package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public enum CharacterTemplateId {
	DESTRO_SHADOW,
	SHADOW;

	public static CharacterTemplateId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
