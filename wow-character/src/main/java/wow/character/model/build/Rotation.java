package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spells.Spell;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-06
 */
@AllArgsConstructor
@Getter
public class Rotation {
	private final List<Spell> cooldowns;
	private final Spell filler;

	public static Rotation onlyFiller(Spell spell) {
		return new Rotation(List.of(), spell);
	}
}
