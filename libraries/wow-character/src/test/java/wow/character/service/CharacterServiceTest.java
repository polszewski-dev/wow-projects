package wow.character.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.character.ExclusiveFaction;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.CharacterTemplateId.DESTRO_SHADOW;
import static wow.commons.model.categorization.ItemSlot.*;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.profession.ProfessionSpecializationId.SHADOWEAVE_TAILORING;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 29.09.2024
 */
class CharacterServiceTest extends WowCharacterSpringTest {
	@Autowired
	CharacterService underTest;

	@Test
	void createPlayerCharacter() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		assertThat(player.getCharacterClassId()).isEqualTo(WARLOCK);
		assertThat(player.getRaceId()).isEqualTo(ORC);
		assertThat(player.getLevel()).isEqualTo(70);
		assertThat(player.getPhaseId()).isEqualTo(TBC_P5);

		assertThat(player.getSpellbook().getAbilityById()).isEmpty();
		assertThat(player.getTalents().getList()).isEmpty();
		assertThat(player.getActivePetType()).isNull();
		assertThat(player.getBuffs().getList()).isEmpty();
		assertThat(player.getEquipment().toList()).isEmpty();
		assertThat(player.getProfessions().getList()).isEmpty();
		assertThat(player.getExclusiveFactions().getList()).isEmpty();
	}

	@Test
	void createNonPlayerCharacter() {
		var nonPlayer = underTest.createNonPlayerCharacter("Target", UNDEAD, 73, TBC_P5);

		assertThat(nonPlayer.getCreatureType()).isEqualTo(UNDEAD);
		assertThat(nonPlayer.getLevel()).isEqualTo(73);
		assertThat(nonPlayer.getPhaseId()).isEqualTo(TBC_P5);

		assertThat(nonPlayer.getSpellbook().getAbilityById()).isEmpty();
		assertThat(nonPlayer.getBuffs().getList()).isEmpty();
		assertThat(nonPlayer.getActivePetType()).isNull();
	}

	@Test
	void applyDefaultCharacterTemplate() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyDefaultCharacterTemplate(player);

		var abilities = player.getSpellbook().getAbilityById().values().stream()
				.flatMap(Collection::stream)
				.map(Object::toString);
		var talents = player.getTalents().getList().stream()
				.map(Object::toString);
		var buffs = player.getBuffs().getList().stream()
				.map(Object::toString);

		assertThat(abilities).hasSameElementsAs(ABILITIES);
		assertThat(talents).hasSameElementsAs(TALENTS);
		assertThat(player.getActivePetType()).isNull();
		assertThat(buffs).isEqualTo(BUFFS);
		assertThat(player.getEquipment().toList()).isEmpty();
		assertThat(player.hasProfession(TAILORING, 375)).isTrue();
		assertThat(player.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isTrue();
		assertThat(player.hasProfession(ENCHANTING, 375)).isTrue();
		assertThat(player.getExclusiveFactions().getList()).hasSameElementsAs(List.of(
				ExclusiveFaction.SCRYERS
		));
	}

	@Test
	void applyDefaultCharacterTemplateOnTarget() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);
		var nonPlayer = underTest.createNonPlayerCharacter("Target", UNDEAD, 73, TBC_P5);

		underTest.applyDefaultCharacterTemplate(player);

		player.setTarget(nonPlayer);

		var debuffs = nonPlayer.getBuffs().getList().stream()
				.map(Object::toString);

		assertThat(debuffs).hasSameElementsAs(DEBUFFS);
	}

	@Test
	void applyCharacterTemplate() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyCharacterTemplate(player, DESTRO_SHADOW);

		var abilities = player.getSpellbook().getAbilityById().values().stream()
				.flatMap(Collection::stream)
				.map(Object::toString);
		var talents = player.getTalents().getList().stream()
				.map(Object::toString);
		var buffs = player.getBuffs().getList().stream()
				.map(Object::toString);

		assertThat(abilities).hasSameElementsAs(ABILITIES);
		assertThat(talents).hasSameElementsAs(TALENTS);
		assertThat(player.getActivePetType()).isNull();
		assertThat(buffs).isEqualTo(BUFFS);
		assertThat(player.getEquipment().toList()).isEmpty();
		assertThat(player.hasProfession(TAILORING, 375)).isTrue();
		assertThat(player.hasProfessionSpecialization(SHADOWEAVE_TAILORING)).isTrue();
		assertThat(player.hasProfession(ENCHANTING, 375)).isTrue();
		assertThat(player.getExclusiveFactions().getList()).hasSameElementsAs(List.of(
				ExclusiveFaction.SCRYERS
		));
	}

	@Test
	void updateAfterRestrictionChange() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyDefaultCharacterTemplate(player);

		player.getTalents().reset();

		underTest.updateAfterRestrictionChange(player);

		var abilities = player.getSpellbook().getAbilityById().values().stream()
				.flatMap(Collection::stream)
				.map(Object::toString);
		var buffs = player.getBuffs().getList().stream()
				.map(Object::toString);

		assertThat(abilities).hasSameElementsAs(ABILITIES_AFTER_TALENT_RESET);
		assertThat(player.getActivePetType()).isNull();
		assertThat(buffs).isEqualTo(BUFFS_AFTER_TALENT_RESET);
	}

	@Test
	void updateAfterRestrictionChange_Item() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyDefaultCharacterTemplate(player);

		var item = itemRepository.getItem("Frozen Shadoweave Robe", player.getPhaseId()).orElseThrow();
		var itemToEquip = new EquippableItem(item);

		player.equip(itemToEquip);

		player.resetProfessions();

		underTest.updateAfterRestrictionChange(player);

		var equippedItem = player.getEquippedItem(CHEST);

		assertThat(equippedItem).isNull();
	}

	@Test
	void updateAfterRestrictionChange_Enchant() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyDefaultCharacterTemplate(player);

		player.resetProfessions();
		player.addProfession(TAILORING, SHADOWEAVE_TAILORING, 375);
		player.addProfession(ENCHANTING, 375);

		underTest.updateAfterRestrictionChange(player);

		var item = itemRepository.getItem("Loop of Forged Power", player.getPhaseId()).orElseThrow();
		var enchant = enchantRepository.getEnchant("Enchant Ring - Spellpower", player.getPhaseId()).orElseThrow();
		var itemToEquip = new EquippableItem(item).enchant(enchant);

		player.equip(itemToEquip, FINGER_1);

		player.resetProfessions();

		underTest.updateAfterRestrictionChange(player);

		var equippedItem = player.getEquippedItem(FINGER_1);

		assertThat(equippedItem.getEnchant()).isNull();
	}

	@Test
	void updateAfterRestrictionChange_Gems() {
		var player = underTest.createPlayerCharacter(
				"Player", WARLOCK, ORC, 70, TBC_P5
		);

		underTest.applyDefaultCharacterTemplate(player);

		player.resetProfessions();
		player.addProfession(TAILORING, SHADOWEAVE_TAILORING, 375);
		player.addProfession(JEWELCRAFTING, 375);

		underTest.updateAfterRestrictionChange(player);

		var item = itemRepository.getItem("Spellstrike Pants", player.getPhaseId()).orElseThrow();
		var gem = gemRepository.getGem("Don Julio's Heart", player.getPhaseId()).orElseThrow();
		var gem2 = gemRepository.getGem("Runed Living Ruby", player.getPhaseId()).orElseThrow();
		var itemToEquip = new EquippableItem(item).gem(gem, gem2, gem2);

		player.equip(itemToEquip);

		player.resetProfessions();

		underTest.updateAfterRestrictionChange(player);

		var equippedItem = player.getEquippedItem(LEGS);

		assertThat(equippedItem.getGem(0)).isNull();
		assertThat(equippedItem.getGem(1)).isEqualTo(gem2);
		assertThat(equippedItem.getGem(2)).isEqualTo(gem2);
	}

	static final List<String> ABILITIES = List.of(
			"Shoot",
			"Blood Fury",
			"Corruption (Rank 1)",
			"Corruption (Rank 2)",
			"Corruption (Rank 3)",
			"Corruption (Rank 4)",
			"Corruption (Rank 5)",
			"Corruption (Rank 6)",
			"Corruption (Rank 7)",
			"Corruption (Rank 8)",
			"Curse of Agony (Rank 1)",
			"Curse of Agony (Rank 2)",
			"Curse of Agony (Rank 3)",
			"Curse of Agony (Rank 4)",
			"Curse of Agony (Rank 5)",
			"Curse of Agony (Rank 6)",
			"Curse of Agony (Rank 7)",
			"Curse of Doom (Rank 1)",
			"Curse of Doom (Rank 2)",
			"Curse of Recklessness (Rank 1)",
			"Curse of Recklessness (Rank 2)",
			"Curse of Recklessness (Rank 3)",
			"Curse of Recklessness (Rank 4)",
			"Curse of Recklessness (Rank 5)",
			"Curse of Tongues (Rank 1)",
			"Curse of Tongues (Rank 2)",
			"Curse of Weakness (Rank 1)",
			"Curse of Weakness (Rank 2)",
			"Curse of Weakness (Rank 3)",
			"Curse of Weakness (Rank 4)",
			"Curse of Weakness (Rank 5)",
			"Curse of Weakness (Rank 6)",
			"Curse of Weakness (Rank 7)",
			"Curse of Weakness (Rank 8)",
			"Curse of the Elements (Rank 1)",
			"Curse of the Elements (Rank 2)",
			"Curse of the Elements (Rank 3)",
			"Curse of the Elements (Rank 4)",
			"Death Coil (Rank 1)",
			"Death Coil (Rank 2)",
			"Death Coil (Rank 3)",
			"Death Coil (Rank 4)",
			"Drain Life (Rank 1)",
			"Drain Life (Rank 2)",
			"Drain Life (Rank 3)",
			"Drain Life (Rank 4)",
			"Drain Life (Rank 5)",
			"Drain Life (Rank 6)",
			"Drain Life (Rank 7)",
			"Drain Life (Rank 8)",
			"Drain Mana (Rank 1)",
			"Drain Mana (Rank 2)",
			"Drain Mana (Rank 3)",
			"Drain Mana (Rank 4)",
			"Drain Mana (Rank 5)",
			"Drain Mana (Rank 6)",
			"Drain Soul (Rank 1)",
			"Drain Soul (Rank 2)",
			"Drain Soul (Rank 3)",
			"Drain Soul (Rank 4)",
			"Drain Soul (Rank 5)",
			"Fear (Rank 1)",
			"Fear (Rank 2)",
			"Fear (Rank 3)",
			"Howl of Terror (Rank 1)",
			"Howl of Terror (Rank 2)",
			"Life Tap (Rank 1)",
			"Life Tap (Rank 2)",
			"Life Tap (Rank 3)",
			"Life Tap (Rank 4)",
			"Life Tap (Rank 5)",
			"Life Tap (Rank 6)",
			"Life Tap (Rank 7)",
			"Seed of Corruption (Rank 1)",
			"Banish (Rank 1)",
			"Banish (Rank 2)",
			"Create Firestone (Rank 1)",
			"Create Firestone (Rank 2)",
			"Create Firestone (Rank 3)",
			"Create Firestone (Rank 4)",
			"Create Firestone (Rank 5)",
			"Create Healthstone (Rank 1)",
			"Create Healthstone (Rank 2)",
			"Create Healthstone (Rank 3)",
			"Create Healthstone (Rank 4)",
			"Create Healthstone (Rank 5)",
			"Create Healthstone (Rank 6)",
			"Create Soulstone (Rank 1)",
			"Create Soulstone (Rank 2)",
			"Create Soulstone (Rank 3)",
			"Create Soulstone (Rank 4)",
			"Create Soulstone (Rank 5)",
			"Create Soulstone (Rank 6)",
			"Create Spellstone (Rank 1)",
			"Create Spellstone (Rank 2)",
			"Create Spellstone (Rank 3)",
			"Create Spellstone (Rank 4)",
			"Demon Armor (Rank 1)",
			"Demon Armor (Rank 2)",
			"Demon Armor (Rank 3)",
			"Demon Armor (Rank 4)",
			"Demon Armor (Rank 5)",
			"Demon Armor (Rank 6)",
			"Demon Skin (Rank 1)",
			"Demon Skin (Rank 2)",
			"Demonic Sacrifice",
			"Detect Invisibility",
			"Enslave Demon (Rank 1)",
			"Enslave Demon (Rank 2)",
			"Enslave Demon (Rank 3)",
			"Eye of Kilrogg",
			"Fel Armor (Rank 1)",
			"Fel Armor (Rank 2)",
			"Fel Domination",
			"Health Funnel (Rank 1)",
			"Health Funnel (Rank 2)",
			"Health Funnel (Rank 3)",
			"Health Funnel (Rank 4)",
			"Health Funnel (Rank 5)",
			"Health Funnel (Rank 6)",
			"Health Funnel (Rank 7)",
			"Health Funnel (Rank 8)",
			"Inferno",
			"Ritual of Doom",
			"Ritual of Souls (Rank 1)",
			"Ritual of Summoning",
			"Sense Demons",
			"Shadow Ward (Rank 1)",
			"Shadow Ward (Rank 2)",
			"Shadow Ward (Rank 3)",
			"Shadow Ward (Rank 4)",
			"Soulshatter",
			"Summon Dreadsteed",
			"Summon Felhunter",
			"Summon Felsteed",
			"Summon Imp",
			"Summon Incubus",
			"Summon Succubus",
			"Summon Voidwalker",
			"Unending Breath",
			"Hellfire (Rank 1)",
			"Hellfire (Rank 2)",
			"Hellfire (Rank 3)",
			"Hellfire (Rank 4)",
			"Immolate (Rank 1)",
			"Immolate (Rank 2)",
			"Immolate (Rank 3)",
			"Immolate (Rank 4)",
			"Immolate (Rank 5)",
			"Immolate (Rank 6)",
			"Immolate (Rank 7)",
			"Immolate (Rank 8)",
			"Immolate (Rank 9)",
			"Incinerate (Rank 1)",
			"Incinerate (Rank 2)",
			"Rain of Fire (Rank 1)",
			"Rain of Fire (Rank 2)",
			"Rain of Fire (Rank 3)",
			"Rain of Fire (Rank 4)",
			"Rain of Fire (Rank 5)",
			"Searing Pain (Rank 1)",
			"Searing Pain (Rank 2)",
			"Searing Pain (Rank 3)",
			"Searing Pain (Rank 4)",
			"Searing Pain (Rank 5)",
			"Searing Pain (Rank 6)",
			"Searing Pain (Rank 7)",
			"Searing Pain (Rank 8)",
			"Shadow Bolt (Rank 1)",
			"Shadow Bolt (Rank 2)",
			"Shadow Bolt (Rank 3)",
			"Shadow Bolt (Rank 4)",
			"Shadow Bolt (Rank 5)",
			"Shadow Bolt (Rank 6)",
			"Shadow Bolt (Rank 7)",
			"Shadow Bolt (Rank 8)",
			"Shadow Bolt (Rank 9)",
			"Shadow Bolt (Rank 10)",
			"Shadow Bolt (Rank 11)",
			"Shadowburn (Rank 1)",
			"Shadowburn (Rank 2)",
			"Shadowburn (Rank 3)",
			"Shadowburn (Rank 4)",
			"Shadowburn (Rank 5)",
			"Shadowburn (Rank 6)",
			"Shadowburn (Rank 7)",
			"Shadowburn (Rank 8)",
			"Soul Fire (Rank 1)",
			"Soul Fire (Rank 2)",
			"Soul Fire (Rank 3)",
			"Soul Fire (Rank 4)"
	);

	static final List<String> TALENTS = List.of(
			"Improved Healthstone (Rank 2)",
			"Demonic Embrace (Rank 5)",
			"Improved Voidwalker (Rank 1)",
			"Fel Intellect (Rank 3)",
			"Fel Domination",
			"Fel Stamina (Rank 3)",
			"Demonic Aegis (Rank 3)",
			"Master Summoner (Rank 2)",
			"Demonic Sacrifice",
			"Improved Shadow Bolt (Rank 5)",
			"Cataclysm (Rank 5)",
			"Bane (Rank 5)",
			"Devastation (Rank 5)",
			"Shadowburn",
			"Intensity (Rank 2)",
			"Destructive Reach (Rank 2)",
			"Improved Searing Pain (Rank 1)",
			"Ruin",
			"Nether Protection (Rank 3)",
			"Backlash (Rank 3)",
			"Soul Leech (Rank 2)",
			"Shadow and Flame (Rank 5)"
	);

	static final List<String> BUFFS = List.of(
			"Fel Armor (improved)",
			"Touch of Shadow",
			"Arcane Brilliance",
			"Prayer of Fortitude",
			"Prayer of Spirit",
			"Gift of the Wild",
			"Greater Blessing of Kings",
			"Wrath of Air Totem",
			"Totem of Wrath",
			"Well Fed (sp)",
			"Brilliant Wizard Oil",
			"Flask of Pure Death"
	);

	static final List<String> DEBUFFS = List.of(
	);

	static final List<String> ABILITIES_AFTER_TALENT_RESET = List.of(
			"Shoot",
			"Blood Fury",
			"Corruption (Rank 1)",
			"Corruption (Rank 2)",
			"Corruption (Rank 3)",
			"Corruption (Rank 4)",
			"Corruption (Rank 5)",
			"Corruption (Rank 6)",
			"Corruption (Rank 7)",
			"Corruption (Rank 8)",
			"Curse of Agony (Rank 1)",
			"Curse of Agony (Rank 2)",
			"Curse of Agony (Rank 3)",
			"Curse of Agony (Rank 4)",
			"Curse of Agony (Rank 5)",
			"Curse of Agony (Rank 6)",
			"Curse of Agony (Rank 7)",
			"Curse of Doom (Rank 1)",
			"Curse of Doom (Rank 2)",
			"Curse of Recklessness (Rank 1)",
			"Curse of Recklessness (Rank 2)",
			"Curse of Recklessness (Rank 3)",
			"Curse of Recklessness (Rank 4)",
			"Curse of Recklessness (Rank 5)",
			"Curse of Tongues (Rank 1)",
			"Curse of Tongues (Rank 2)",
			"Curse of Weakness (Rank 1)",
			"Curse of Weakness (Rank 2)",
			"Curse of Weakness (Rank 3)",
			"Curse of Weakness (Rank 4)",
			"Curse of Weakness (Rank 5)",
			"Curse of Weakness (Rank 6)",
			"Curse of Weakness (Rank 7)",
			"Curse of Weakness (Rank 8)",
			"Curse of the Elements (Rank 1)",
			"Curse of the Elements (Rank 2)",
			"Curse of the Elements (Rank 3)",
			"Curse of the Elements (Rank 4)",
			"Death Coil (Rank 1)",
			"Death Coil (Rank 2)",
			"Death Coil (Rank 3)",
			"Death Coil (Rank 4)",
			"Drain Life (Rank 1)",
			"Drain Life (Rank 2)",
			"Drain Life (Rank 3)",
			"Drain Life (Rank 4)",
			"Drain Life (Rank 5)",
			"Drain Life (Rank 6)",
			"Drain Life (Rank 7)",
			"Drain Life (Rank 8)",
			"Drain Mana (Rank 1)",
			"Drain Mana (Rank 2)",
			"Drain Mana (Rank 3)",
			"Drain Mana (Rank 4)",
			"Drain Mana (Rank 5)",
			"Drain Mana (Rank 6)",
			"Drain Soul (Rank 1)",
			"Drain Soul (Rank 2)",
			"Drain Soul (Rank 3)",
			"Drain Soul (Rank 4)",
			"Drain Soul (Rank 5)",
			"Fear (Rank 1)",
			"Fear (Rank 2)",
			"Fear (Rank 3)",
			"Howl of Terror (Rank 1)",
			"Howl of Terror (Rank 2)",
			"Life Tap (Rank 1)",
			"Life Tap (Rank 2)",
			"Life Tap (Rank 3)",
			"Life Tap (Rank 4)",
			"Life Tap (Rank 5)",
			"Life Tap (Rank 6)",
			"Life Tap (Rank 7)",
			"Seed of Corruption (Rank 1)",
			"Banish (Rank 1)",
			"Banish (Rank 2)",
			"Create Firestone (Rank 1)",
			"Create Firestone (Rank 2)",
			"Create Firestone (Rank 3)",
			"Create Firestone (Rank 4)",
			"Create Firestone (Rank 5)",
			"Create Healthstone (Rank 1)",
			"Create Healthstone (Rank 2)",
			"Create Healthstone (Rank 3)",
			"Create Healthstone (Rank 4)",
			"Create Healthstone (Rank 5)",
			"Create Healthstone (Rank 6)",
			"Create Soulstone (Rank 1)",
			"Create Soulstone (Rank 2)",
			"Create Soulstone (Rank 3)",
			"Create Soulstone (Rank 4)",
			"Create Soulstone (Rank 5)",
			"Create Soulstone (Rank 6)",
			"Create Spellstone (Rank 1)",
			"Create Spellstone (Rank 2)",
			"Create Spellstone (Rank 3)",
			"Create Spellstone (Rank 4)",
			"Demon Armor (Rank 1)",
			"Demon Armor (Rank 2)",
			"Demon Armor (Rank 3)",
			"Demon Armor (Rank 4)",
			"Demon Armor (Rank 5)",
			"Demon Armor (Rank 6)",
			"Demon Skin (Rank 1)",
			"Demon Skin (Rank 2)",
			"Detect Invisibility",
			"Enslave Demon (Rank 1)",
			"Enslave Demon (Rank 2)",
			"Enslave Demon (Rank 3)",
			"Eye of Kilrogg",
			"Fel Armor (Rank 1)",
			"Fel Armor (Rank 2)",
			"Health Funnel (Rank 1)",
			"Health Funnel (Rank 2)",
			"Health Funnel (Rank 3)",
			"Health Funnel (Rank 4)",
			"Health Funnel (Rank 5)",
			"Health Funnel (Rank 6)",
			"Health Funnel (Rank 7)",
			"Health Funnel (Rank 8)",
			"Inferno",
			"Ritual of Doom",
			"Ritual of Souls (Rank 1)",
			"Ritual of Summoning",
			"Sense Demons",
			"Shadow Ward (Rank 1)",
			"Shadow Ward (Rank 2)",
			"Shadow Ward (Rank 3)",
			"Shadow Ward (Rank 4)",
			"Soulshatter",
			"Summon Dreadsteed",
			"Summon Felhunter",
			"Summon Felsteed",
			"Summon Imp",
			"Summon Incubus",
			"Summon Succubus",
			"Summon Voidwalker",
			"Unending Breath",
			"Hellfire (Rank 1)",
			"Hellfire (Rank 2)",
			"Hellfire (Rank 3)",
			"Hellfire (Rank 4)",
			"Immolate (Rank 1)",
			"Immolate (Rank 2)",
			"Immolate (Rank 3)",
			"Immolate (Rank 4)",
			"Immolate (Rank 5)",
			"Immolate (Rank 6)",
			"Immolate (Rank 7)",
			"Immolate (Rank 8)",
			"Immolate (Rank 9)",
			"Incinerate (Rank 1)",
			"Incinerate (Rank 2)",
			"Rain of Fire (Rank 1)",
			"Rain of Fire (Rank 2)",
			"Rain of Fire (Rank 3)",
			"Rain of Fire (Rank 4)",
			"Rain of Fire (Rank 5)",
			"Searing Pain (Rank 1)",
			"Searing Pain (Rank 2)",
			"Searing Pain (Rank 3)",
			"Searing Pain (Rank 4)",
			"Searing Pain (Rank 5)",
			"Searing Pain (Rank 6)",
			"Searing Pain (Rank 7)",
			"Searing Pain (Rank 8)",
			"Shadow Bolt (Rank 1)",
			"Shadow Bolt (Rank 2)",
			"Shadow Bolt (Rank 3)",
			"Shadow Bolt (Rank 4)",
			"Shadow Bolt (Rank 5)",
			"Shadow Bolt (Rank 6)",
			"Shadow Bolt (Rank 7)",
			"Shadow Bolt (Rank 8)",
			"Shadow Bolt (Rank 9)",
			"Shadow Bolt (Rank 10)",
			"Shadow Bolt (Rank 11)",
			"Soul Fire (Rank 1)",
			"Soul Fire (Rank 2)",
			"Soul Fire (Rank 3)",
			"Soul Fire (Rank 4)"
	);

	static final List<String> BUFFS_AFTER_TALENT_RESET = List.of(
			"Arcane Brilliance",
			"Prayer of Fortitude",
			"Prayer of Spirit",
			"Gift of the Wild",
			"Greater Blessing of Kings",
			"Wrath of Air Totem",
			"Totem of Wrath",
			"Well Fed (sp)",
			"Brilliant Wizard Oil",
			"Flask of Pure Death"
	);
}