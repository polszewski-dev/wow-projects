package wow.commons.client;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.pve.PhaseId;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsClientSpringTestConfig.class)
public abstract class WowCommonsClientSpringTest {
	@Autowired
	protected CharacterService characterService;

	protected static void assertId(AbstractItem<?> item, int id) {
		assertThat(item.getId().value()).isEqualTo(id);
	}

	protected static void assertId(Enchant enchant, int id) {
		assertThat(enchant.getId().value()).isEqualTo(id);
	}

	protected PlayerCharacter getPlayer() {
		return getPlayer(CHARACTER_CLASS, RACE);
	}

	protected PlayerCharacter getPlayer(CharacterClassId characterClass, RaceId race) {
		return getPlayer(characterClass, race, LEVEL, PHASE);
	}

	protected PlayerCharacter getPlayer(CharacterClassId characterClass, RaceId race, int level, PhaseId phase) {
		return getPlayer("Player", characterClass, race, level, phase);
	}

	protected PlayerCharacter getPlayer(String name, CharacterClassId characterClass, RaceId race, int level, PhaseId phase) {
		var character = characterService.createPlayerCharacter(
				name,
				characterClass,
				race,
				level,
				phase,
				PlayerCharacterImpl::new
		);

		var target = characterService.createNonPlayerCharacter(
				"Target",
				ENEMY_TYPE,
				level + LVL_DIFF,
				phase,
				NonPlayerCharacterImpl::new
		);

		character.setTarget(target);

		characterService.applyDefaultCharacterTemplate(character);
		return character;
	}

	protected static final CharacterClassId CHARACTER_CLASS = WARLOCK;
	protected static final RaceId RACE = RaceId.UNDEAD;
	protected static final PhaseId PHASE = TBC_P5;
	protected static final int LEVEL = 70;
	protected static final CreatureType ENEMY_TYPE = UNDEAD;
	protected static final int LVL_DIFF = 3;
}
