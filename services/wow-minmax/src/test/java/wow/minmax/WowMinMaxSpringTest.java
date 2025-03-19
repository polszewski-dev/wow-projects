package wow.minmax;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.categorization.ItemSlot;
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

	protected Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem redGem = getGem(32196);
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		equipment.equip(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem));
		equipment.equip(getItem("Amulet of Unfettered Magics"));
		equipment.equip(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem));
		equipment.equip(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(redGem));
		equipment.equip(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(redGem, redGem, redGem));
		equipment.equip(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer - Spellpower")).gem(orangeGem));
		equipment.equip(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(redGem, redGem));
		equipment.equip(getItem("Belt of the Malefic").gem(orangeGem));
		equipment.equip(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(redGem, redGem, orangeGem));
		equipment.equip(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(orangeGem));
		equipment.equip(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring - Spellpower")), ItemSlot.FINGER_1);
		equipment.equip(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring - Spellpower")), ItemSlot.FINGER_2);
		equipment.equip(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.equip(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.equip(getItem("Sunflare").enchant(getEnchant("Enchant Weapon - Soulfrost")));
		equipment.equip(getItem("Chronicle of Dark Secrets"));
		equipment.equip(getItem("Wand of the Demonsoul").gem(getGem("Veiled Pyrestone")));

		return equipment;
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
				characterClass,
				race,
				level,
				phase,
				PlayerImpl::new
		);

		var target = characterService.createNonPlayerCharacter(
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
