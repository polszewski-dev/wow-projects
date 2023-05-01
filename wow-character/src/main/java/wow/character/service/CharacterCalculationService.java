package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Spell;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
public interface CharacterCalculationService {
	Snapshot getSnapshot(Character character, Attributes totalStats);

	Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats);
}
