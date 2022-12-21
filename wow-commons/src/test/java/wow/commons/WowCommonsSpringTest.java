package wow.commons;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.*;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.CharacterRepository;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.SpellRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.character.CharacterClass.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.character.Race.ORC;
import static wow.commons.model.professions.Profession.ENCHANTING;
import static wow.commons.model.professions.Profession.TAILORING;
import static wow.commons.model.professions.ProfessionSpecialization.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.Phase.TBC_P5;
import static wow.commons.model.talents.TalentId.*;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsSpringTestConfig.class)
public abstract class WowCommonsSpringTest {
	@Autowired
	protected ItemRepository itemRepository;

	@Autowired
	protected SpellRepository spellRepository;

	@Autowired
	protected CharacterRepository characterRepository;

	@Autowired
	protected PveRepository pveRepository;

	protected Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem redGem = getGem("Runed Crimson Spinel");
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		equipment.set(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem));
		equipment.set(getItem("Amulet of Unfettered Magics"));
		equipment.set(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem));
		equipment.set(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(redGem));
		equipment.set(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(redGem, redGem, redGem));
		equipment.set(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer – Spellpower")).gem(orangeGem));
		equipment.set(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(redGem, redGem));
		equipment.set(getItem("Belt of the Malefic").gem(orangeGem));
		equipment.set(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(redGem, redGem, orangeGem));
		equipment.set(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(orangeGem));
		equipment.set(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_1);
		equipment.set(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_2);
		equipment.set(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.set(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.set(getItem("Sunflare").enchant(getEnchant("Enchant Weapon – Soulfrost")));
		equipment.set(getItem("Chronicle of Dark Secrets"));
		equipment.set(getItem("Wand of the Demonsoul").gem(getGem("Veiled Pyrestone")));

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
		return Stream.of(
						"Arcane Brilliance",
						"Gift of the Wild",
						"Greater Blessing of Kings",
						"Fel Armor",
						"Touch of Shadow",
						"Brilliant Wizard Oil",
						"Well Fed (sp)",
						"Flask of Pure Death",
						"Wrath of Air Totem",
						"Totem of Wrath",
						"Curse of the Elements"
				)
				.map(this::getBuff)
				.collect(Collectors.toList());
	}

	protected Buff getBuff(String name) {
		return spellRepository.getBuff(name, PHASE).orElseThrow();
	}

	protected Talent getTalent(TalentId talentId, int rank) {
		return spellRepository.getTalent(CHARACTER_CLASS, talentId, rank, PHASE).orElseThrow();
	}

	protected CharacterInfo getCharacterInfo(SpellId spellId) {
		Spell spell = getSpell(spellId);

		Build build = new Build(
				BuildId.DESTRO_SHADOW,
				null,
				Map.of(),
				PveRole.CASTER_DPS,
				spell,
				List.of(spell),
				null,
				Map.of(BuffSetId.SELF_BUFFS, List.of())
		);

		BaseStatInfo baseStatInfo = characterRepository.getBaseStats(CHARACTER_CLASS, RACE, LEVEL, PHASE).orElseThrow();
		CombatRatingInfo combatRatingInfo = characterRepository.getCombatRatings(LEVEL, PHASE).orElseThrow();

		int maxLevel = PHASE.getGameVersion().getMaxLevel();

		CharacterProfessions professions = new CharacterProfessions(List.of(
				new CharacterProfession(TAILORING, maxLevel, SHADOWEAVE_TAILORING),
				new CharacterProfession(ENCHANTING, maxLevel, null)
		));

		return new CharacterInfo(CHARACTER_CLASS, RACE, LEVEL, build, professions, PHASE, baseStatInfo, combatRatingInfo);
	}

	protected EnemyInfo getEnemyInfo() {
		return new EnemyInfo(UNDEAD);
	}

	protected static final CharacterClass CHARACTER_CLASS = WARLOCK;
	protected static final Race RACE = ORC;
	protected static final Phase PHASE = TBC_P5;
	protected static final int LEVEL = PHASE.getGameVersion().getMaxLevel();

	protected static final Comparator<Double> ROUNDED_DOWN = Comparator.comparingDouble(Double::intValue);
	protected static final Offset<Double> PRECISION = Offset.offset(0.01);
}
