package wow.minmax;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.build.BuildId;
import wow.character.model.character.Character;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.Enemy;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.SpellRepository;
import wow.minmax.model.PlayerProfile;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static wow.character.model.build.BuildId.DESTRO_SHADOW;
import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.character.Race.ORC;
import static wow.commons.model.professions.Profession.ENCHANTING;
import static wow.commons.model.professions.Profession.TAILORING;
import static wow.commons.model.professions.ProfessionSpecialization.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.Phase.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowMinMaxSpringTestConfig.class)
@TestPropertySource("classpath:application.properties")
public abstract class WowMinMaxSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected SpellRepository spellRepository;

	@Autowired
	protected CharacterService characterService;

	protected Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem redGem = getGem("Runed Crimson Spinel");
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		equipment.equip(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem));
		equipment.equip(getItem("Amulet of Unfettered Magics"));
		equipment.equip(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem));
		equipment.equip(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(redGem));
		equipment.equip(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(redGem, redGem, redGem));
		equipment.equip(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer – Spellpower")).gem(orangeGem));
		equipment.equip(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(redGem, redGem));
		equipment.equip(getItem("Belt of the Malefic").gem(orangeGem));
		equipment.equip(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(redGem, redGem, orangeGem));
		equipment.equip(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(orangeGem));
		equipment.equip(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_1);
		equipment.equip(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_2);
		equipment.equip(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.equip(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.equip(getItem("Sunflare").enchant(getEnchant("Enchant Weapon – Soulfrost")));
		equipment.equip(getItem("Chronicle of Dark Secrets"));
		equipment.equip(getItem("Wand of the Demonsoul").gem(getGem("Veiled Pyrestone")));

		return equipment;
	}

	protected EquippableItem getItem(String name) {
		return new EquippableItem(itemRepository.getItem(name, PHASE).orElseThrow());
	}

	protected Gem getGem(String name) {
		return itemRepository.getGem(name, PHASE).orElseThrow();
	}

	protected Enchant getEnchant(String name) {
		return itemRepository.getEnchant(name, PHASE).orElseThrow();
	}

	protected Spell getSpell(SpellId spellId) {
		return spellRepository.getSpellHighestRank(spellId, LEVEL, PHASE).orElseThrow();
	}

	protected Buff getBuff(String name) {
		return spellRepository.getBuff(name, PHASE).orElseThrow();
	}

	protected Talent getTalent(TalentId talentId, int rank) {
		return spellRepository.getTalent(CHARACTER_CLASS, talentId, rank, PHASE).orElseThrow();
	}

	protected Character getCharacter() {
		int maxLevel = PHASE.getGameVersion().getMaxProfession();

		var professions = List.of(
				new CharacterProfession(TAILORING, maxLevel, SHADOWEAVE_TAILORING),
				new CharacterProfession(ENCHANTING, maxLevel, null)
		);

		Character character = characterService.createCharacter(
				CHARACTER_CLASS,
				RACE,
				LEVEL,
				BUILD_ID,
				professions,
				PHASE
		);

		Enemy enemy = characterService.createEnemy(UNDEAD);

		character.setTargetEnemy(enemy);

		return character;
	}

	protected PlayerProfile getPlayerProfile() {
		Character character = getCharacter();

		return new PlayerProfile(PROFILE_ID, PROFILE_NAME, character);
	}

	protected static final UUID PROFILE_ID = UUID.fromString("88cc7c80-523a-11ed-bdc3-0242ac120002");
	protected static final String PROFILE_NAME = "test#1";

	protected static final CharacterClass CHARACTER_CLASS = WARLOCK;
	protected static final Race RACE = ORC;
	protected static final BuildId BUILD_ID = DESTRO_SHADOW;
	protected static final Phase PHASE = TBC_P5;
	protected static final int LEVEL = PHASE.getGameVersion().getMaxLevel();

	protected static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	protected static final Offset<Double> PRECISION = Offset.offset(0.01);
}