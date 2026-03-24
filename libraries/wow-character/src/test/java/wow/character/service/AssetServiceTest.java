package wow.character.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.test.commons.AssetNames.*;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
class AssetServiceTest extends WowCharacterSpringTest {
	@Autowired
	AssetService assetService;

	@ParameterizedTest
	@MethodSource("getTestData")
	void execution_plan_is_correct(TestData data) {
		var raid = new Raid<PlayerCharacter>();

		for (var memberData : data.members) {
			var member = getPartyMember(memberData);
			raid.getParty(memberData.partyIdx).add(member);
		}

		var executionPlan = assetService.getAssetExecutionPlan(raid);

		var actual = executionPlan.stream()
				.map(x -> new Cast(x.player().getName(), x.name()))
				.toList();

		var expected = data.expected;

		assertThat(actual).isEqualTo(expected);
	}

	PlayerCharacter getPartyMember(PartyMemberData data) {
		var player = getPlayer(data.name, data.characterClassId, data.raceId);

		if (data.talentLink != null) {
			player.getTalents().loadFromTalentLink(data.talentLink);
			characterService.updateAfterRestrictionChange(player);
		}

		player.getAssets().reset();
		player.getAssets().setNames(data.enabledAssets);

		return player;
	}

	static final List<TestData> TEST_DATA = List.of(
			testData(
					List.of(
							fireMage(0, "Fire Mage", List.of(MOLTEN_ARMOR, ARCANE_BRILLIANCE, IMPROVED_SCORCH))
					),
					List.of(
							cast("Fire Mage", MOLTEN_ARMOR),
							cast("Fire Mage", ARCANE_BRILLIANCE),
							cast("Fire Mage", IMPROVED_SCORCH)
					)
			),

			testData(
					List.of(
							shadowPriest(0, "Shadow Priest", List.of(SHADOWFORM, PRAYER_OF_FORTITUDE, SHADOW_WEAVING, MISERY))
					),
					List.of(
							cast("Shadow Priest", SHADOWFORM),
							cast("Shadow Priest", PRAYER_OF_FORTITUDE),
							cast("Shadow Priest", SHADOW_WEAVING),
							cast("Shadow Priest", MISERY)
					)
			),

			testData(
					List.of(
							disciplinePriest(0, "Discipline Priest", List.of(PRAYER_OF_FORTITUDE, PRAYER_OF_SPIRIT))
					),
					List.of(
							cast("Discipline Priest", PRAYER_OF_FORTITUDE),
							cast("Discipline Priest", PRAYER_OF_SPIRIT)
					)
			),

			testData(
					List.of(
							destroWarlock(0, "Destruction Warlock", List.of(FEL_ARMOR, TOUCH_OF_SHADOW))
					),
					List.of(
							cast("Destruction Warlock", FEL_ARMOR),
							cast("Destruction Warlock", TOUCH_OF_SHADOW)
					)
			),

			testData(
					List.of(
							afflictionWarlock(0, "Affliction Warlock", List.of(FEL_ARMOR, CURSE_OF_THE_ELEMENTS))
					),
					List.of(
							cast("Affliction Warlock", FEL_ARMOR),
							cast("Affliction Warlock", CURSE_OF_THE_ELEMENTS)
					)
			),

			testData(
					List.of(
							balanceDruid(0, "Balance Druid", List.of(MOONKIN_AURA, GIFT_OF_THE_WILD))
					),
					List.of(
							cast("Balance Druid", MOONKIN_AURA),
							cast("Balance Druid", GIFT_OF_THE_WILD)
					)
			),

			testData(
					List.of(
							elementalShaman(0, "Elemental Shaman", List.of(TOTEM_OF_WRATH, WRATH_OF_AIR_TOTEM, MANA_SPRING_TOTEM))
					),
					List.of(
							cast("Elemental Shaman", TOTEM_OF_WRATH),
							cast("Elemental Shaman", WRATH_OF_AIR_TOTEM),
							cast("Elemental Shaman", MANA_SPRING_TOTEM)
					)
			),

			testData(
					List.of(
							holyPaladin(0, "Holy Paladin", List.of(GREATER_BLESSING_OF_WISDOM))
					),
					List.of(
							cast("Holy Paladin", GREATER_BLESSING_OF_WISDOM)
					)
			),

			testData(
					List.of(
							retributionPaladin(0, "Retribution Paladin", List.of(GREATER_BLESSING_OF_KINGS, SANCTITY_AURA))
					),
					List.of(
							cast("Retribution Paladin", SANCTITY_AURA),
							cast("Retribution Paladin", GREATER_BLESSING_OF_KINGS)
					)
			),

			testData(
					List.of(
							fireMage(0, "Fire Mage #1", List.of(MOLTEN_ARMOR, ARCANE_BRILLIANCE, IMPROVED_SCORCH)),
							fireMage(0, "Fire Mage #2", List.of(MOLTEN_ARMOR, ARCANE_BRILLIANCE, IMPROVED_SCORCH))
					),
					List.of(
							cast("Fire Mage #1", MOLTEN_ARMOR),
							cast("Fire Mage #2", MOLTEN_ARMOR),
							cast("Fire Mage #1", ARCANE_BRILLIANCE),
							cast("Fire Mage #1", IMPROVED_SCORCH)
					)
			),

			testData(
					List.of(
							fireMage(0, "Fire Mage #1", List.of(MOLTEN_ARMOR, ARCANE_BRILLIANCE, IMPROVED_SCORCH)),
							fireMage(1, "Fire Mage #2", List.of(MOLTEN_ARMOR, ARCANE_BRILLIANCE, IMPROVED_SCORCH))
					),
					List.of(
							cast("Fire Mage #1", MOLTEN_ARMOR),
							cast("Fire Mage #2", MOLTEN_ARMOR),
							cast("Fire Mage #1", ARCANE_BRILLIANCE),
							cast("Fire Mage #1", IMPROVED_SCORCH)
					)
			),

			testData(
					List.of(
							shadowPriest(0, "Shadow Priest #1", List.of(SHADOWFORM, PRAYER_OF_FORTITUDE, SHADOW_WEAVING, MISERY)),
							shadowPriest(1, "Shadow Priest #2", List.of(SHADOWFORM, PRAYER_OF_FORTITUDE, SHADOW_WEAVING, MISERY))
					),
					List.of(
							cast("Shadow Priest #1", SHADOWFORM),
							cast("Shadow Priest #2", SHADOWFORM),
							cast("Shadow Priest #1", PRAYER_OF_FORTITUDE),
							cast("Shadow Priest #1", SHADOW_WEAVING),
							cast("Shadow Priest #1", MISERY)
					)
			),

			testData(
					List.of(
							disciplinePriest(0, "Discipline Priest #1", List.of(PRAYER_OF_FORTITUDE, PRAYER_OF_SPIRIT)),
							disciplinePriest(1, "Discipline Priest #2", List.of(PRAYER_OF_FORTITUDE, PRAYER_OF_SPIRIT))
					),
					List.of(
							cast("Discipline Priest #1", PRAYER_OF_FORTITUDE),
							cast("Discipline Priest #1", PRAYER_OF_SPIRIT)
					)
			),

			testData(
					List.of(
							destroWarlock(0, "Destruction Warlock #1", List.of(FEL_ARMOR, TOUCH_OF_SHADOW)),
							destroWarlock(1, "Destruction Warlock #2", List.of(FEL_ARMOR, TOUCH_OF_SHADOW))
					),
					List.of(
							cast("Destruction Warlock #1", FEL_ARMOR),
							cast("Destruction Warlock #1", TOUCH_OF_SHADOW),
							cast("Destruction Warlock #2", FEL_ARMOR),
							cast("Destruction Warlock #2", TOUCH_OF_SHADOW)
					)
			),

			testData(
					List.of(
							afflictionWarlock(0, "Affliction Warlock #1", List.of(FEL_ARMOR, CURSE_OF_THE_ELEMENTS)),
							afflictionWarlock(1, "Affliction Warlock #2", List.of(FEL_ARMOR, CURSE_OF_THE_ELEMENTS))
					),
					List.of(
							cast("Affliction Warlock #1", FEL_ARMOR),
							cast("Affliction Warlock #2", FEL_ARMOR),
							cast("Affliction Warlock #1", CURSE_OF_THE_ELEMENTS)
					)
			),

			testData(
					List.of(
							destroWarlock(0, "Destruction Warlock", List.of(FEL_ARMOR, TOUCH_OF_SHADOW, CURSE_OF_THE_ELEMENTS)),
							afflictionWarlock(0, "Affliction Warlock", List.of(FEL_ARMOR, CURSE_OF_THE_ELEMENTS))
					),
					List.of(
							cast("Destruction Warlock", FEL_ARMOR),
							cast("Destruction Warlock", TOUCH_OF_SHADOW),
							cast("Affliction Warlock", FEL_ARMOR),
							cast("Affliction Warlock", CURSE_OF_THE_ELEMENTS)
					)
			),

			testData(
					List.of(
							balanceDruid(0, "Balance Druid #1", List.of(MOONKIN_AURA, GIFT_OF_THE_WILD)),
							balanceDruid(1, "Balance Druid #2", List.of(MOONKIN_AURA, GIFT_OF_THE_WILD))
					),
					List.of(
							cast("Balance Druid #1", MOONKIN_AURA),
							cast("Balance Druid #2", MOONKIN_AURA),
							cast("Balance Druid #1", GIFT_OF_THE_WILD)
					)
			),

			testData(
					List.of(
							elementalShaman(0, "Elemental Shaman #1", List.of(TOTEM_OF_WRATH, WRATH_OF_AIR_TOTEM, MANA_SPRING_TOTEM)),
							elementalShaman(0, "Elemental Shaman #2", List.of(TOTEM_OF_WRATH, WRATH_OF_AIR_TOTEM, MANA_SPRING_TOTEM))
					),
					List.of(
							cast("Elemental Shaman #1", TOTEM_OF_WRATH),
							cast("Elemental Shaman #1", WRATH_OF_AIR_TOTEM),
							cast("Elemental Shaman #1", MANA_SPRING_TOTEM)
					)
			),

			testData(
					List.of(
							elementalShaman(0, "Elemental Shaman #1", List.of(TOTEM_OF_WRATH, WRATH_OF_AIR_TOTEM, MANA_SPRING_TOTEM)),
							elementalShaman(1, "Elemental Shaman #2", List.of(TOTEM_OF_WRATH, WRATH_OF_AIR_TOTEM, MANA_SPRING_TOTEM))
					),
					List.of(
							cast("Elemental Shaman #1", TOTEM_OF_WRATH),
							cast("Elemental Shaman #1", WRATH_OF_AIR_TOTEM),
							cast("Elemental Shaman #1", MANA_SPRING_TOTEM),
							cast("Elemental Shaman #2", TOTEM_OF_WRATH),
							cast("Elemental Shaman #2", WRATH_OF_AIR_TOTEM),
							cast("Elemental Shaman #2", MANA_SPRING_TOTEM)
					)
			),

			testData(
					List.of(
							holyPaladin(0, "Holy Paladin #1", List.of(GREATER_BLESSING_OF_WISDOM)),
							holyPaladin(1, "Holy Paladin #2", List.of(GREATER_BLESSING_OF_WISDOM))
					),
					List.of(
							cast("Holy Paladin #1", GREATER_BLESSING_OF_WISDOM)
					)
			),

			testData(
					List.of(
							retributionPaladin(0, "Retribution Paladin #1", List.of(GREATER_BLESSING_OF_KINGS, SANCTITY_AURA)),
							retributionPaladin(0, "Retribution Paladin #2", List.of(GREATER_BLESSING_OF_KINGS, SANCTITY_AURA))
					),
					List.of(
							cast("Retribution Paladin #1", SANCTITY_AURA),
							cast("Retribution Paladin #1", GREATER_BLESSING_OF_KINGS)
					)
			),

			testData(
					List.of(
							retributionPaladin(0, "Retribution Paladin #1", List.of(GREATER_BLESSING_OF_KINGS, SANCTITY_AURA)),
							retributionPaladin(1, "Retribution Paladin #2", List.of(GREATER_BLESSING_OF_KINGS, SANCTITY_AURA))
					),
					List.of(
							cast("Retribution Paladin #1", SANCTITY_AURA),
							cast("Retribution Paladin #2", SANCTITY_AURA),
							cast("Retribution Paladin #1", GREATER_BLESSING_OF_KINGS)
					)
			),

			testData(
					List.of(
							retributionPaladin(0, "Retribution Paladin", List.of(GREATER_BLESSING_OF_WISDOM, SANCTITY_AURA)),
							holyPaladin(1, "Holy Paladin", List.of(GREATER_BLESSING_OF_WISDOM))
					),
					List.of(
							cast("Retribution Paladin", SANCTITY_AURA),
							cast("Holy Paladin", GREATER_BLESSING_OF_WISDOM)
					)
			),

			testData(
					List.of(
							holyPaladin(0, "Holy Paladin", List.of(GREATER_BLESSING_OF_WISDOM)),
							retributionPaladin(1, "Retribution Paladin", List.of(GREATER_BLESSING_OF_WISDOM, SANCTITY_AURA))
					),
					List.of(
							cast("Retribution Paladin", SANCTITY_AURA),
							cast("Holy Paladin", GREATER_BLESSING_OF_WISDOM)
					)
			)
	);

	static List<TestData> getTestData() {
		return TEST_DATA;
	}

	record PartyMemberData(int partyIdx, String name, CharacterClassId characterClassId, RaceId raceId, String talentLink, List<String> enabledAssets) {}

	record TestData(List<PartyMemberData> members, List<Cast> expected) {}

	record Cast(String player, String assetName) {}

	static TestData testData(List<PartyMemberData> members, List<Cast> expected) {
		return new TestData(members, expected);
	}

	static PartyMemberData fireMage(int partyIdx, String name, List<String> enabledAssets) {
		return mage(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001", enabledAssets);
	}

	static PartyMemberData shadowPriest(int partyIdx, String name, List<String> enabledAssets) {
		return priest(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/priest/500230013--503250510240103051451", enabledAssets);
	}

	static PartyMemberData disciplinePriest(int partyIdx, String name, List<String> enabledAssets) {
		return priest(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/priest/5002303130505120001551-2330500303", enabledAssets);
	}

	static PartyMemberData destroWarlock(int partyIdx, String name, List<String> enabledAssets) {
		return warlock(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025", enabledAssets);
	}

	static PartyMemberData afflictionWarlock(int partyIdx, String name, List<String> enabledAssets) {
		return warlock(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/warlock/55022000102351055103--50500051220001", enabledAssets);
	}

	static PartyMemberData balanceDruid(int partyIdx, String name, List<String> enabledAssets) {
		return druid(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/druid/510022312503135231351--500233", enabledAssets);
	}

	static PartyMemberData elementalShaman(int partyIdx, String name, List<String> enabledAssets) {
		return shaman(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/shaman/55003105100213351051--05105301005", enabledAssets);
	}

	static PartyMemberData holyPaladin(int partyIdx, String name, List<String> enabledAssets) {
		return paladin(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/paladin/05503121520132531051-500231-5", enabledAssets);
	}

	static PartyMemberData retributionPaladin(int partyIdx, String name, List<String> enabledAssets) {
		return paladin(partyIdx, name, "https://www.wowhead.com/tbc/talent-calc/paladin/5-053201-0523005120033125331051", enabledAssets);
	}

	static PartyMemberData mage(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, MAGE, UNDEAD, talentLink, enabledAssets);
	}

	static PartyMemberData priest(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, PRIEST, UNDEAD, talentLink, enabledAssets);
	}

	static PartyMemberData warlock(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, WARLOCK, UNDEAD, talentLink, enabledAssets);
	}

	static PartyMemberData druid(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, DRUID, TAUREN, talentLink, enabledAssets);
	}

	static PartyMemberData shaman(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, SHAMAN, TAUREN, talentLink, enabledAssets);
	}

	static PartyMemberData paladin(int partyIdx, String name, String talentLink, List<String> enabledAssets) {
		return member(partyIdx, name, PALADIN, BLOOD_ELF, talentLink, enabledAssets);
	}

	static PartyMemberData member(int partyIdx, String name, CharacterClassId characterClassId, RaceId raceId, String talentLink, List<String> enabledAssets) {
		return new PartyMemberData(partyIdx, name, characterClassId, raceId, talentLink, enabledAssets);
	}

	static Cast cast(String player, String assetName) {
		return new Cast(player, assetName);
	}
}
