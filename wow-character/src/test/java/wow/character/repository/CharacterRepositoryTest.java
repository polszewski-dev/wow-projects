package wow.character.repository;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.build.Talents;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.util.TalentLinkParser;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.*;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.Race;
import wow.commons.model.config.Described;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionType;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.racial.Racial;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.CharacterTemplateId.DESTRO_SHADOW;
import static wow.commons.model.buff.BuffId.*;
import static wow.commons.model.categorization.ItemType.ONE_HAND;
import static wow.commons.model.categorization.WeaponSubType.SWORD;
import static wow.commons.model.character.ArmorProfficiency.CLOTH;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.ExclusiveFaction.SCRYERS;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.character.WeaponProfficiency.*;
import static wow.commons.model.profession.ProfessionId.*;
import static wow.commons.model.profession.ProfessionProficiencyId.ARTISAN;
import static wow.commons.model.profession.ProfessionProficiencyId.MASTER;
import static wow.commons.model.profession.ProfessionSpecializationId.*;
import static wow.commons.model.pve.GameVersionId.*;
import static wow.commons.model.pve.PhaseId.*;
import static wow.commons.model.pve.Side.HORDE;
import static wow.commons.model.spell.AbilityId.*;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
class CharacterRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	CharacterRepository underTest;

	@Test
	@DisplayName("BaseStatInfo is read correctly")
	void baseStatInfoIsCorrect() {
		BaseStatInfo baseStatInfo = underTest.getBaseStatInfo(TBC, WARLOCK, ORC, 70).orElseThrow();

		assertThat(baseStatInfo.getLevel()).isEqualTo(70);
		assertThat(baseStatInfo.getCharacterClassId()).isEqualTo(WARLOCK);
		assertThat(baseStatInfo.getRaceId()).isEqualTo(ORC);
		assertThat(baseStatInfo.getBaseStrength()).isEqualTo(54);
		assertThat(baseStatInfo.getBaseAgility()).isEqualTo(55);
		assertThat(baseStatInfo.getBaseStamina()).isEqualTo(78);
		assertThat(baseStatInfo.getBaseIntellect()).isEqualTo(130);
		assertThat(baseStatInfo.getBaseSpirit()).isEqualTo(142);
		assertThat(baseStatInfo.getBaseHealth()).isEqualTo(4090);
		assertThat(baseStatInfo.getBaseMana()).isEqualTo(4285);
		assertThat(baseStatInfo.getBaseSpellCritPct().value()).isEqualTo(3.29, PRECISION);
		assertThat(baseStatInfo.getIntellectPerCritPct()).isEqualTo(80, PRECISION);
	}

	@Test
	@DisplayName("CombatRatingInfo is read correctly")
	void combatRatingInfoIsCorrect() {
		CombatRatingInfo combatRatingInfo = underTest.getCombatRatingInfo(TBC, 70).orElseThrow();

		assertThat(combatRatingInfo.getLevel()).isEqualTo(70);
		assertThat(combatRatingInfo.getSpellCrit()).isEqualTo(22.22, PRECISION);
		assertThat(combatRatingInfo.getSpellHit()).isEqualTo(12.62, PRECISION);
		assertThat(combatRatingInfo.getSpellHaste()).isEqualTo(15.76, PRECISION);
	}

	@Test
	@DisplayName("CharacterTemplate is read correctly")
	void characterTemplateIsCorrect() {
		Optional<CharacterTemplate> optionalCharacterTemplate = underTest.getCharacterTemplate(DESTRO_SHADOW, WARLOCK, 70, PhaseId.TBC_P5);

		assertThat(optionalCharacterTemplate).isPresent();

		CharacterTemplate characterTemplate = optionalCharacterTemplate.orElseThrow();

		assertThat(characterTemplate.getCharacterTemplateId()).isEqualTo(DESTRO_SHADOW);
		assertThat(characterTemplate.getLevel()).isEqualTo(70);
		assertThat(characterTemplate.getCharacterClassId()).isEqualTo(WARLOCK);
		var link = TalentLinkParser.parse("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025", talentRepository);
		assertThat(characterTemplate.getTalentLink()).isEqualTo(link);
		assertThat(characterTemplate.getRole()).isEqualTo(PveRole.CASTER_DPS);
		assertThat(characterTemplate.getDefaultRotationTemplate().getAbilityIds()).isEqualTo(List.of(CURSE_OF_DOOM, CORRUPTION, IMMOLATE, SHADOW_BOLT));
		assertThat(characterTemplate.getActivePet()).isNull();
		assertThat(characterTemplate.getDefaultBuffs()).hasSameElementsAs(List.of(
				FEL_ARMOR_IMPROVED, TOUCH_OF_SHADOW, BuffId.ARCANE_BRILLIANCE, BuffId.PRAYER_OF_FORTITUDE, BuffId.PRAYER_OF_SPIRIT,
				GIFT_OF_THE_WILD, GREATER_BLESSING_OF_KINGS, WRATH_OF_AIR_TOTEM,
				TOTEM_OF_WRATH, WELL_FED_SP, BRILLIANT_WIZARD_OIL, FLASK_OF_PURE_DEATH
		));
		assertThat(characterTemplate.getDefaultDebuffs()).hasSameElementsAs(List.of(
				BuffId.CURSE_OF_THE_ELEMENTS
		));
		assertThat(characterTemplate.getProfessions().get(0).getProfessionId()).isEqualTo(ENCHANTING);
		assertThat(characterTemplate.getProfessions().get(0).getSpecializationId()).isNull();
		assertThat(characterTemplate.getProfessions().get(1).getProfessionId()).isEqualTo(TAILORING);
		assertThat(characterTemplate.getProfessions().get(1).getSpecializationId()).isEqualTo(SHADOWEAVE_TAILORING);
		assertThat(characterTemplate.getExclusiveFactions()).hasSameElementsAs(List.of(SCRYERS));
		assertThat(characterTemplate.isDefault()).isTrue();
	}

	@Test
	void getDefaultCharacterTemplate() {
		var characterTemplate = underTest.getDefaultCharacterTemplate(WARLOCK, 70, PhaseId.TBC_P5).orElseThrow();

		assertThat(characterTemplate.getCharacterTemplateId()).isEqualTo(DESTRO_SHADOW);
		assertThat(characterTemplate.isDefault()).isTrue();
	}

	@ParameterizedTest(name = "[{index}] Can equip: slot = {0}, type = {1}, subType = {2}")
	@CsvSource({
			"HEAD, HEAD, CLOTH",
			"NECK, NECK, ",
			"SHOULDER, SHOULDER, CLOTH",
			"BACK, BACK, CLOTH",
			"CHEST, CHEST, CLOTH",
			"WRIST, WRIST, CLOTH",
			"HANDS, HANDS, CLOTH",
			"WAIST, WAIST, CLOTH",
			"LEGS, LEGS, CLOTH",
			"FEET, FEET, CLOTH",
			"FINGER_1, FINGER, ",
			"FINGER_2, FINGER, ",
			"TRINKET_1, TRINKET, ",
			"TRINKET_2, TRINKET, ",
			"MAIN_HAND, TWO_HAND, STAFF",
			"MAIN_HAND, ONE_HAND, SWORD",
			"MAIN_HAND, ONE_HAND, DAGGER",
			"MAIN_HAND, MAIN_HAND, SWORD",
			"MAIN_HAND, MAIN_HAND, DAGGER",
			"OFF_HAND, OFF_HAND, HELD IN OFF-HAND",
			"RANGED, RANGED, WAND",
	})
	void canEquip(ItemSlot itemSlot, ItemType itemType, String itemSubType) {
		CharacterClass warlock = getCharacterClass(WARLOCK);

		assertThat(warlock.canEquip(itemSlot, itemType, ItemSubType.parse(itemSubType))).isTrue();
	}

	private CharacterClass getCharacterClass(CharacterClassId characterClassId) {
		return underTest.getGameVersion(TBC).orElseThrow().getCharacterClass(characterClassId);
	}

	@ParameterizedTest(name = "[{index}] Can not equip: slot = {0}, type = {1}, subType = {2}")
	@CsvSource({
			"HEAD, HEAD, LEATHER",
			"HEAD, HEAD, MAIL",
			"HEAD, HEAD, PLATE",
			"SHOULDER, SHOULDER, LEATHER",
			"CHEST, CHEST, LEATHER",
			"WRIST, WRIST, LEATHER",
			"HANDS, HANDS, LEATHER",
			"WAIST, WAIST, LEATHER",
			"LEGS, LEGS, LEATHER",
			"FEET, FEET, LEATHER",
			"MAIN_HAND, TWO_HAND, AXE",
			"MAIN_HAND, TWO_HAND, SWORD",
			"MAIN_HAND, TWO_HAND, POLEARM",
			"MAIN_HAND, TWO_HAND, MACE",
			"MAIN_HAND, ONE_HAND, AXE",
			"MAIN_HAND, ONE_HAND, MACE",
			"MAIN_HAND, ONE_HAND, FIST WEAPON",
			"MAIN_HAND, MAIN_HAND, AXE",
			"MAIN_HAND, MAIN_HAND, MACE",
			"MAIN_HAND, MAIN_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, AXE",
			"OFF_HAND, ONE_HAND, MACE",
			"OFF_HAND, ONE_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, FIST WEAPON",
			"OFF_HAND, ONE_HAND, DAGGER",
			"OFF_HAND, ONE_HAND, SWORD",
			"OFF_HAND, OFF_HAND, SHIELD",
			"RANGED, RANGED, THROWN",
			"RANGED, RANGED, BOW",
			"RANGED, RANGED, CROSSBOW",
			"RANGED, RANGED, TOTEM",
			"RANGED, RANGED, LIBRAM",
			"RANGED, RANGED, IDOL",
			"RANGED, RANGED, IDOL",
			"RANGED, RANGED, IDOL",
	})
	void canNotEquip(ItemSlot itemSlot, ItemType itemType, String itemSubType) {
		CharacterClass warlock = getCharacterClass(WARLOCK);

		assertThat(warlock.isDualWield()).isFalse();
		assertThat(warlock.canEquip(itemSlot, itemType, ItemSubType.parse(itemSubType))).isFalse();
	}

	@Test
	void testDualWield() {
		CharacterClass warrior = getCharacterClass(WARRIOR);

		assertThat(warrior.isDualWield()).isTrue();
		assertThat(warrior.canEquip(ItemSlot.MAIN_HAND, ONE_HAND, SWORD)).isTrue();
		assertThat(warrior.canEquip(ItemSlot.OFF_HAND, ONE_HAND, SWORD)).isTrue();
	}

	@ParameterizedTest
	@EnumSource(CharacterClassId.class)
	void nobodyCanEquipThese(CharacterClassId characterClassId) {
		for (ItemType itemType : ItemType.values()) {
			ItemCategory category = itemType.getCategory();
			if (category != ItemCategory.ARMOR && category != ItemCategory.WEAPON && category != ItemCategory.ACCESSORY) {
				CharacterClass classInfo = getCharacterClass(characterClassId);
				assertThat(classInfo.canEquip(ItemSlot.CHEST, itemType, null)).isFalse();
			}
		}
	}

	@Test
	void vanillaGameVersionIsCorrect() {
		GameVersion gameVersion = underTest.getGameVersion(VANILLA).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(VANILLA);
		assertThat(gameVersion.getName()).isEqualTo("Vanilla");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(99.0);
	}

	@Test
	void tbcGameVersionIsCorrect() {
		GameVersion gameVersion = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(TBC);
		assertThat(gameVersion.getName()).isEqualTo("TBC");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(99.0);
	}

	@Test
	void wotlkGameVersionIsCorrect() {
		GameVersion gameVersion = underTest.getGameVersion(WOTLK).orElseThrow();

		assertThat(gameVersion.getGameVersionId()).isEqualTo(WOTLK);
		assertThat(gameVersion.getName()).isEqualTo("WotLK");
		assertThat(gameVersion.getBasePveSpellHitChancesPct()).isEqualTo(List.of(96.0, 95.0, 94.0, 83.0));
		assertThat(gameVersion.getMaxPveSpellHitChancePct()).isEqualTo(100.0);
	}

	@Test
	void gameVersionPhasesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getPhases().stream().map(Phase::getPhaseId).toList()).hasSameElementsAs(List.of(
				VANILLA_P1,
				VANILLA_P2,
				VANILLA_P2_5,
				VANILLA_P3,
				VANILLA_P4,
				VANILLA_P5,
				VANILLA_P6
		));

		assertThat(tbc.getPhases().stream().map(Phase::getPhaseId).toList()).hasSameElementsAs(List.of(
				TBC_P0,
				TBC_P1,
				TBC_P2,
				TBC_P3,
				TBC_P4,
				TBC_P5
		));
	}

	@Test
	void gameVersionClassesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getCharacterClasses().stream().map(CharacterClass::getCharacterClassId).toList()).hasSameElementsAs(List.of(
				MAGE,
				WARLOCK,
				PRIEST,
				DRUID,
				ROGUE,
				HUNTER,
				SHAMAN,
				PALADIN,
				WARRIOR
		));

		assertThat(tbc.getCharacterClasses().stream().map(CharacterClass::getCharacterClassId).toList()).hasSameElementsAs(List.of(
				MAGE,
				WARLOCK,
				PRIEST,
				DRUID,
				ROGUE,
				HUNTER,
				SHAMAN,
				PALADIN,
				WARRIOR
		));
	}

	@Test
	void gameVersionRacesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD,
				ORC,
				TROLL,
				TAUREN,
				HUMAN,
				DWARF,
				GNOME,
				NIGHT_ELF
		));

		assertThat(tbc.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD,
				ORC,
				TROLL,
				TAUREN,
				BLOOD_ELF,
				HUMAN,
				DWARF,
				GNOME,
				NIGHT_ELF,
				DRANEI
		));
	}

	@Test
	void gameVersionProfessionsAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		assertThat(vanilla.getProfessions().stream().map(Profession::getProfessionId).toList()).hasSameElementsAs(List.of(
				ENCHANTING,
				ALCHEMY,
				TAILORING,
				LEATHERWORKING,
				BLACKSMITHING,
				ENGINEERING,
				HERBALISM,
				MINING,
				SKINNING,
				COOKING,
				FISHING,
				FIRST_AID
		));

		assertThat(tbc.getProfessions().stream().map(Profession::getProfessionId).toList()).hasSameElementsAs(List.of(
				ENCHANTING,
				JEWELCRAFTING,
				ALCHEMY,
				TAILORING,
				LEATHERWORKING,
				BLACKSMITHING,
				ENGINEERING,
				HERBALISM,
				MINING,
				SKINNING,
				COOKING,
				FISHING,
				FIRST_AID
		));
	}

	@Test
	void vanillaPhasesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		Phase phase = vanilla.getPhase(VANILLA_P1);

		assertThat(phase.getPhaseId()).isEqualTo(VANILLA_P1);
		assertThat(phase.getName()).isEqualTo("Vanilla P1");
		assertThat(phase.getMaxLevel()).isEqualTo(60);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(ARTISAN);
	}

	@Test
	void tbcPrepatchIsCorrect() {
		GameVersion vanilla = underTest.getGameVersion(TBC).orElseThrow();
		Phase phase = vanilla.getPhase(TBC_P0);

		assertThat(phase.getPhaseId()).isEqualTo(TBC_P0);
		assertThat(phase.getName()).isEqualTo("TBC Pre-Patch");
		assertThat(phase.getMaxLevel()).isEqualTo(60);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(ARTISAN);
	}

	@Test
	void tbcPhasesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(TBC).orElseThrow();
		Phase phase = vanilla.getPhase(TBC_P1);

		assertThat(phase.getPhaseId()).isEqualTo(TBC_P1);
		assertThat(phase.getName()).isEqualTo("TBC P1");
		assertThat(phase.getMaxLevel()).isEqualTo(70);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(MASTER);
	}

	@Test
	void classesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();

		CharacterClass warlock = vanilla.getCharacterClass(WARLOCK);

		assertThat(warlock.getCharacterClassId()).isEqualTo(WARLOCK);
		assertThat(warlock.getName()).isEqualTo("Warlock");
		assertThat(warlock.getIcon()).isEqualTo("classicon_warlock");
		assertThat(warlock.getArmorProfficiencies()).hasSameElementsAs(List.of(CLOTH));

		assertThat(warlock.getWeaponProfficiencies()).hasSameElementsAs(List.of(
				MAIN_HAND_DAGGER, ONE_HAND_DAGGER, MAIN_HAND_SWORD, ONE_HAND_SWORD, STAFF, HELD_IN_OFF_HAND, WAND
		));

		assertThat(warlock.isDualWield()).isFalse();
		assertThat(warlock.getGameVersion().getGameVersionId()).isEqualTo(VANILLA);

		assertThat(warlock.getRaces().stream().map(Race::getRaceId).toList()).hasSameElementsAs(List.of(
				UNDEAD, ORC, HUMAN, GNOME
		));
	}

	@Disabled("doesn't work")
	@Test
	void racesAreCorrect() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		Race orc = vanilla.getRace(ORC);

		assertThat(orc.getRaceId()).isEqualTo(ORC);
		assertThat(orc.getName()).isEqualTo("Orc");
		assertThat(orc.getIcon()).isEqualTo("race_orc_male");
		assertThat(orc.getSide()).isEqualTo(HORDE);

		assertThat(orc.getRacials().stream().map(Described::getName).toList()).hasSameElementsAs(List.of(
				"Axe Specialization",
				"Blood Fury",
				"Command",
				"Hardiness"
		));

		assertThat(orc.getRacials(getVanillaWarlock()).stream().map(Described::getName).toList()).hasSameElementsAs(List.of(
				"Axe Specialization",
				"Blood Fury",
				"Command",
				"Hardiness"
		));
	}
	@Disabled("doesn't work")
	@Test
	void racialsWithClassConditionAreFilteredCorrectly() {
		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		Race vanillaOrc = vanilla.getRace(ORC);
		Race tbcOrc = tbc.getRace(ORC);

		assertThat(vanillaOrc.getRacials()).hasSize(4);
		assertThat(vanillaOrc.getRacials(getVanillaWarlock())).hasSize(4);

		assertThat(tbcOrc.getRacials()).hasSize(6);
		assertThat(tbcOrc.getRacials(getTbcWarlock())).hasSize(4);
	}
	@Disabled("doesn't work")
	@Test
	void racialsAreCorrect() {
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();
		Race orc = tbc.getRace(ORC);

		Racial racial = orc.getRacials().stream()
				.filter(x -> x.getName().equals("Blood Fury"))
				.filter(x -> x.isAvailableTo(getTbcWarlock()))
				.findFirst()
				.orElseThrow();

		assertThat(racial.getName()).isEqualTo("Blood Fury");
		assertThat(racial.getIcon()).isEqualTo("racial_orc_berserkerstrength");
		assertThat(racial.getTooltip()).isEqualTo("Increases your damage and healing from spells and effects by up to 143, but reduces healing effects on you by 50%.  Lasts 15 sec.");
		assertThat(racial.getCharacterRestriction().raceIds()).isEqualTo(List.of(ORC));
		assertThat(racial.getCharacterRestriction().characterClassIds()).hasSameElementsAs(List.of(WARLOCK));

		assertThat(racial.isAvailableTo(getTbcWarlock())).isTrue();
	}

	@Test
	void professionsAreCorrect() {
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		Profession tailoring = tbc.getProfession(TAILORING);

		assertThat(tailoring.getProfessionId()).isEqualTo(TAILORING);
		assertThat(tailoring.getName()).isEqualTo("Tailoring");
		assertThat(tailoring.getIcon()).isEqualTo("trade_tailoring");

		assertThat(tailoring.getSpecializations().stream().map(ProfessionSpecialization::getSpecializationId).toList()).hasSameElementsAs(List.of(
				SPELLFIRE_TAILORING,
				SHADOWEAVE_TAILORING,
				MOONCLOTH_TAILORING
		));

		GameVersion vanilla = underTest.getGameVersion(VANILLA).orElseThrow();

		assertThat(vanilla.getProfession(TAILORING).getSpecializations()).isEmpty();
	}

	@Test
	void professionSpecsAreCorrect() {
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		ProfessionSpecialization specialization = tbc.getProfession(TAILORING).getSpecialization(SPELLFIRE_TAILORING);

		assertThat(specialization.getSpecializationId()).isEqualTo(SPELLFIRE_TAILORING);
		assertThat(specialization.getName()).isEqualTo("Spellfire Tailoring");
		assertThat(specialization.getIcon()).isEqualTo("classic_spell_holy_blessingofprotection");

		assertThat(specialization.getProfession().getProfessionId()).isEqualTo(TAILORING);
	}

	@Test
	void professionProficienciesAreCorrect() {
		GameVersion tbc = underTest.getGameVersion(TBC).orElseThrow();

		ProfessionProficiency proficiency = tbc.getProficiency(MASTER);

		assertThat(proficiency.getProficiencyId()).isEqualTo(MASTER);
		assertThat(proficiency.getName()).isEqualTo("Master");
		assertThat(proficiency.getMaxSkilll()).isEqualTo(375);
		assertThat(proficiency.getReqLevel(ProfessionType.CRAFTING)).isEqualTo(50);
		assertThat(proficiency.getReqLevel(ProfessionType.GATHERING)).isEqualTo(40);
		assertThat(proficiency.getReqLevel(ProfessionType.SECONDARY)).isEqualTo(50);
	}

	PlayerCharacter getVanillaWarlock() {
		return getWarlock(VANILLA);
	}

	PlayerCharacter getTbcWarlock() {
		return getWarlock(TBC);
	}

	private PlayerCharacter getWarlock(GameVersionId gameVersionId) {
		GameVersion gameVersion = underTest.getGameVersion(gameVersionId).orElseThrow();
		CharacterClass warlock = gameVersion.getCharacterClass(WARLOCK);
		Race orc = gameVersion.getRace(ORC);
		Phase phase = gameVersion.getLastPhase();
		int level = phase.getMaxLevel();
		BaseStatInfo baseStatInfo = underTest.getBaseStatInfo(gameVersionId, warlock.getCharacterClassId(), orc.getRaceId(), level).orElseThrow();
		CombatRatingInfo combatRatingInfo = underTest.getCombatRatingInfo(gameVersionId, level).orElseThrow();

		return new PlayerCharacterImpl(phase, warlock, orc, level, baseStatInfo, combatRatingInfo, new Talents(List.of()));
	}

	static final Offset<Double> PRECISION = Offset.offset(0.01);
}
