package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SnapshotState;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
public interface CharacterCalculationService {
	Snapshot createSnapshot(Character character, Spell spell, Attributes totalStats);

	void advanceSnapshot(Snapshot snapshot, SnapshotState targetState);
}
