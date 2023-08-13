package wow.character.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.spells.Spell;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.snapshot.SnapshotState.*;

/**
 * User: POlszewski
 * Date: 2023-04-28
 */
class CharacterCalculationServiceTest extends WowCharacterSpringTest {
	@Autowired
	CharacterCalculationService underTest;


	@Test
	void createSnapshot() {
		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.createSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getState()).isEqualTo(INITIAL);
	}

	@Test
	void advanceSnapshotToMidState() {
		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.createSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getState()).isEqualTo(INITIAL);
		assertThat(snapshot.getSp()).isZero();
		assertThat(snapshot.getCost()).isZero();

		underTest.advanceSnapshot(snapshot, SPELL_STATS);

		assertThat(snapshot.getState()).isEqualTo(SPELL_STATS);
		assertThat(snapshot.getSp()).isEqualTo(370);
		assertThat(snapshot.getCost()).isZero();
	}

	@Test
	void advanceSnapshotToEndState() {
		Spell spell = character.getRotation().getFiller();
		Snapshot snapshot = underTest.createSnapshot(character, spell, character.getStats());

		assertThat(snapshot.getState()).isEqualTo(INITIAL);
		assertThat(snapshot.getSp()).isZero();

		underTest.advanceSnapshot(snapshot, COMPLETE);

		assertThat(snapshot.getState()).isEqualTo(COMPLETE);
		assertThat(snapshot.getSp()).isEqualTo(370);
		assertThat(snapshot.getCost()).isEqualTo(399);
	}

	Character character;

	@BeforeEach
	void setup() {
		character = getCharacter();
	}
}