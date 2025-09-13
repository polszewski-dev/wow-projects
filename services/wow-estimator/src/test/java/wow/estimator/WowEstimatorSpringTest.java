package wow.estimator;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.estimator.model.Player;
import wow.estimator.model.impl.NonPlayerImpl;
import wow.estimator.model.impl.PlayerImpl;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowEstimatorSpringTestConfig.class)
@TestPropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties",
		"classpath:wow-estimator.properties",
		"classpath:application.properties"
})
public abstract class WowEstimatorSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected EnchantRepository enchantRepository;

	@Autowired
	protected GemRepository gemRepository;

	@Autowired
	protected SpellRepository spellRepository;

	@Autowired
	protected CharacterService characterService;

	protected void equipGearSet(Player character) {
		characterService.equipGearSet(character, "Wowhead TBC P5 BiS");
	}

	protected EquippableItem getItem(String name) {
		return new EquippableItem(itemRepository.getItem(name, PHASE).orElseThrow());
	}

	protected Gem getGem(String name) {
		return gemRepository.getGem(name, PHASE).orElseThrow();
	}

	protected Gem getGem(int gemId) {
		return gemRepository.getGem(gemId, PHASE).orElseThrow();
	}

	protected Enchant getEnchant(String name) {
		return enchantRepository.getEnchant(name, PHASE).orElseThrow();
	}

	protected Player getCharacter() {
		return getCharacter(CHARACTER_CLASS, RACE);
	}

	protected Player getCharacter(CharacterClassId characterClass, RaceId race) {
		return getCharacter(characterClass, race, LEVEL, PHASE);
	}

	protected Player getCharacter(CharacterClassId characterClass, RaceId race, int level, PhaseId phase) {
		var character = characterService.createPlayerCharacter(
				"Player",
				characterClass,
				race,
				level,
				phase,
				PlayerImpl::new
		);

		var target = characterService.createNonPlayerCharacter(
				"Target",
				ENEMY_TYPE,
				level + LVL_DIFF,
				phase,
				NonPlayerImpl::new
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
