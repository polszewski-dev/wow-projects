package wow.minmax;

import org.assertj.core.data.Offset;
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
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.impl.NonPlayerImpl;
import wow.minmax.model.impl.PlayerImpl;
import wow.minmax.service.StatsService;
import wow.minmax.service.UpgradeService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxSpringTestConfig.class)
@TestPropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties",
		"classpath:wow-minmax.properties",
		"classpath:application.properties"
})
public abstract class WowMinMaxSpringTest {
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

	@Autowired
	protected UpgradeService upgradeService;

	@Autowired
	protected StatsService statsService;

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

	protected PlayerProfile getPlayerProfile() {
		Player character = getCharacter();

		return new PlayerProfile(
				PROFILE_ID, PROFILE_NAME, character.getCharacterClassId(), character.getRaceId(), LocalDateTime.now(), CHARACTER_KEY
		);
	}

	protected static final UUID PROFILE_ID = UUID.fromString("88cc7c80-523a-11ed-bdc3-0242ac120002");
	protected static final String PROFILE_NAME = "test#1";

	protected static final CharacterClassId CHARACTER_CLASS = WARLOCK;
	protected static final RaceId RACE = RaceId.UNDEAD;
	protected static final PhaseId PHASE = TBC_P5;
	protected static final int LEVEL = 70;
	protected static final CreatureType ENEMY_TYPE = UNDEAD;
	protected static final int LVL_DIFF = 3;
	protected static final CharacterId CHARACTER_KEY = new CharacterId(PROFILE_ID, PHASE, LEVEL, ENEMY_TYPE, LVL_DIFF);

	protected static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	protected static final Offset<Double> PRECISION = Offset.offset(0.01);
}
