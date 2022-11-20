package wow.minmax.controller;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.*;
import wow.commons.model.talents.TalentTree;
import wow.commons.model.unit.*;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
abstract class ControllerTest {
	Item chest;
	Item trinket;
	Enchant enchant;
	Gem redGem;
	Gem yellowGem;
	Gem blueGem;
	Buff buff;
	Spell spell;
	Build build;
	PlayerProfile profile;

	void createMockObjects() {
		createItems();
		createEnchants();
		createGems();
		createBuffs();
		createSpells();
		createBuild();
		createProfile();
	}

	private void createItems() {
		ItemSocketSpecification socketSpecification = new ItemSocketSpecification(List.of(SocketType.RED, SocketType.YELLOW, SocketType.BLUE), Attributes.EMPTY);
		chest = new Item(101, "Test Chest Item", ItemRarity.EPIC, ItemType.CHEST, ArmorSubType.CLOTH, Set.of(), socketSpecification, Attributes.EMPTY, null);

		SpecialAbility specialAbility = SpecialAbility.onUse(Attributes.of(SPELL_DAMAGE, 100), Duration.seconds(15), Duration.seconds(60), "...");
		trinket = new Item(102, "Test Trinket", ItemRarity.EPIC, ItemType.TRINKET, null, Set.of(), ItemSocketSpecification.EMPTY, Attributes.of(specialAbility), null);
	}

	private void createGems() {
		redGem = new Gem(301, "Test Red Gem", ItemRarity.RARE, Set.of(), GemColor.RED, List.of(), Attributes.of(SPELL_DAMAGE, 20));
		yellowGem = new Gem(302, "Test Yellow Gem", ItemRarity.RARE, Set.of(), GemColor.RED, List.of(), Attributes.of(SPELL_HIT_RATING, 15));
		blueGem = new Gem(303, "Test Blue Gem", ItemRarity.RARE, Set.of(), GemColor.RED, List.of(), Attributes.of(STAMINA, 10));
	}

	private void createEnchants() {
		enchant = new Enchant(201, "Test Chest Enchant", List.of(ItemType.CHEST), Attributes.of(BASE_STATS_INCREASE, 12));
	}

	private void createBuffs() {
		buff = new Buff(401, "Test Buff", 1, BuffType.SELF_BUFF, BuffExclusionGroup.SELF_BUFF, Attributes.of(SPELL_DAMAGE, 100), SpellId.FEL_ARMOR, Duration.INFINITE, Duration.ZERO, "...");
	}

	private void createSpells() {
		SpellInfo spellInfo = new SpellInfo(SpellId.SHADOW_BOLT, TalentTree.DESTRUCTION, SpellSchool.SHADOW, Percent._100, Percent.ZERO, Duration.ZERO, false, null, true, null, null, null, null, null);
		SpellRankInfo spellRankInfo = new SpellRankInfo(SpellId.SHADOW_BOLT, 10, 70, 100, Duration.seconds(3), false, 500, 600, 0, 0, 0, 0, null, null, null, null);
		spell = new Spell(spellInfo, spellRankInfo);
	}

	private void createBuild() {
		build = new Build(BuildIds.DESTRO_SHADOW_BUILD);
		build.setTalentInfos(Map.of());
		build.setRole(PVERole.CASTER_DPS);
		build.setDamagingSpell(spell);
		build.setRelevantSpells(List.of(spell));
		build.setActivePet(null);
		build.setBuffSets(Map.of(Build.BuffSetId.SELF_BUFFS, List.of(buff)));
		build.setBuffs(List.of(buff));
	}

	private void createProfile() {
		CharacterInfo characterInfo = new CharacterInfo(CharacterClass.WARLOCK, Race.ORC, 70, List.of());

		profile = new PlayerProfile(UUID.fromString("88cc7c80-523a-11ed-bdc3-0242ac120002"), "test#1", characterInfo, CreatureType.DEMON, Phase.TBC_P5);
		profile.setBuild(build);
		profile.setEquipment(new Equipment());
		profile.getEquipment().set(new EquippableItem(chest).enchant(enchant).gem(redGem, yellowGem, blueGem));
		profile.getEquipment().set(new EquippableItem(trinket), ItemSlot.TRINKET_1);
	}
}
