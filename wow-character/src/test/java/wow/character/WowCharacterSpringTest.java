package wow.character;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;

import java.util.Comparator;
import java.util.List;

import static wow.commons.model.buff.BuffId.*;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;
import static wow.commons.model.talent.TalentId.*;

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
	protected SpellRepository spellRepository;

	@Autowired
	protected PveRepository pveRepository;

	@Autowired
	protected CharacterService characterService;

	@Autowired
	protected CharacterRepository characterRepository;

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
		return itemRepository.getGem(name, PHASE).orElseThrow();
	}

	protected Gem getGem(int gemId) {
		return itemRepository.getGem(gemId, PHASE).orElseThrow();
	}

	protected Enchant getEnchant(String name) {
		return itemRepository.getEnchant(name, PHASE).orElseThrow();
	}

	protected List<Talent> getTalents() {
		return List.of(
				getTalent(IMPROVED_HEALTHSTONE, 2),
				getTalent(DEMONIC_EMBRACE, 5),
				getTalent(IMPROVED_VOIDWALKER, 1),
				getTalent(FEL_INTELLECT, 3),
				getTalent(FEL_DOMINATION, 1),
				getTalent(FEL_STAMINA, 3),
				getTalent(DEMONIC_AEGIS, 3),
				getTalent(MASTER_SUMMONER, 2),
				getTalent(DEMONIC_SACRIFICE, 1),
				getTalent(IMPROVED_SHADOW_BOLT, 5),
				getTalent(CATACLYSM, 5),
				getTalent(BANE, 5),
				getTalent(DEVASTATION, 5),
				getTalent(SHADOWBURN, 1),
				getTalent(INTENSITY, 2),
				getTalent(DESTRUCTIVE_REACH, 2),
				getTalent(IMPROVED_SEARING_PAIN, 1),
				getTalent(RUIN, 1),
				getTalent(NETHER_PROTECTION, 3),
				getTalent(BACKLASH, 3),
				getTalent(SOUL_LEECH, 2),
				getTalent(SHADOW_AND_FLAME, 5)
		);
	}

	protected List<Buff> getBuffs() {
		return List.of(
				getBuff(ARCANE_BRILLIANCE, 2),
				getBuff(GIFT_OF_THE_WILD, 3),
				getBuff(GREATER_BLESSING_OF_KINGS, 0),
				getBuff(FEL_ARMOR, 2),
				getBuff(TOUCH_OF_SHADOW, 0),
				getBuff(BRILLIANT_WIZARD_OIL, 0),
				getBuff(WELL_FED_SP, 0),
				getBuff(FLASK_OF_PURE_DEATH, 0),
				getBuff(WRATH_OF_AIR_TOTEM, 1),
				getBuff(TOTEM_OF_WRATH, 1),
				getBuff(CURSE_OF_THE_ELEMENTS, 4)
		);
	}

	protected Buff getBuff(BuffId buffId, int rank) {
		return spellRepository.getBuff(buffId, rank, PHASE).orElseThrow();
	}

	protected Talent getTalent(TalentId talentId, int rank) {
		return spellRepository.getTalent(CHARACTER_CLASS, talentId, rank, PHASE).orElseThrow();
	}

	protected PlayerCharacter getCharacter() {
		return getCharacter(CHARACTER_CLASS, RACE);
	}

	protected PlayerCharacter getCharacter(CharacterClassId characterClass, RaceId race) {
		return getCharacter(characterClass, race, LEVEL, PHASE);
	}

	protected PlayerCharacter getCharacter(CharacterClassId characterClass, RaceId race, int level, PhaseId phase) {
		var character = characterService.createPlayerCharacter(
				characterClass,
				race,
				level,
				phase
		);

		var target = characterService.createNonPlayerCharacter(
				ENEMY_TYPE,
				level + LVL_DIFF,
				phase
		);

		character.setTarget(target);

		var templateId = character.getCharacterClass().getDefaultCharacterTemplateId();
		characterService.applyCharacterTemplate(character, templateId);
		return character;
	}

	protected static final CharacterClassId CHARACTER_CLASS = WARLOCK;
	protected static final RaceId RACE = RaceId.UNDEAD;
	protected static final PhaseId PHASE = TBC_P5;
	protected static final int LEVEL = 70;
	protected static final CreatureType ENEMY_TYPE = UNDEAD;
	protected static final int LVL_DIFF = 3;

	protected static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	protected static final Offset<Double> PRECISION = Offset.offset(0.01);
}
