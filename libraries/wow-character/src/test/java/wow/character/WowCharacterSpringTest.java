package wow.character;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.commons.repository.spell.BuffRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.repository.spell.TalentRepository;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCharacterSpringTestConfig.class)
public abstract class WowCharacterSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected EnchantRepository enchantRepository;

	@Autowired
	protected GemRepository gemRepository;

	@Autowired
	protected SpellRepository spellRepository;

	@Autowired
	protected TalentRepository talentRepository;

	@Autowired
	protected BuffRepository buffRepository;

	@Autowired
	protected CharacterService characterService;

	@Autowired
	protected GameVersionRepository gameVersionRepository;

	@Autowired
	protected PhaseRepository phaseRepository;

	protected void equipGearSet(PlayerCharacter character) {
		characterService.equipGearSet(character, "Wowhead TBC P5 BiS");
	}

	protected EquippableItem getItem(String name) {
		return new EquippableItem(itemRepository.getItem(name, PHASE).orElseThrow());
	}

	protected Gem getGem(String name) {
		return gemRepository.getGem(name, PHASE).orElseThrow();
	}

	protected Gem getGem(int gemId) {
		return gemRepository.getGem(GemId.of(gemId), PHASE).orElseThrow();
	}

	protected Enchant getEnchant(String name) {
		return enchantRepository.getEnchant(name, PHASE).orElseThrow();
	}

	protected PlayerCharacter getCharacter() {
		return getCharacter(CHARACTER_CLASS, RACE);
	}

	protected PlayerCharacter getCharacter(CharacterClassId characterClass, RaceId race) {
		return getCharacter(characterClass, race, LEVEL, PHASE);
	}

	protected PlayerCharacter getCharacter(CharacterClassId characterClass, RaceId race, int level, PhaseId phase) {
		var character = characterService.createPlayerCharacter(
				"Player",
				characterClass,
				race,
				level,
				phase
		);

		var target = characterService.createNonPlayerCharacter(
				"Target",
				ENEMY_TYPE,
				level + LVL_DIFF,
				phase
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

	protected static final String WARLOCK_TEMPLATE_NAME = "Shadow Destro";
}
