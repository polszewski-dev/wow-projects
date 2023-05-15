package wow.character.model.build;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spells.SpellId;
import wow.commons.util.parser.ParserUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-15
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RotationTemplate {
	private final String value;
	private final List<SpellId> spellIds;

	public static RotationTemplate parse(String value) {
		List<SpellId> spellIds = ParserUtil.getValues(value, SpellId::parse);

		return new RotationTemplate(value, spellIds);
	}

	public Rotation createRotation() {
		return Rotation.create(this);
	}

	@Override
	public String toString() {
		return value;
	}
}
