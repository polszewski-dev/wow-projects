package wow.character.repository;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.WowCharacterSpringTest;
import wow.character.model.asset.Asset;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.pve.GameVersionId.VANILLA;

/**
 * User: POlszewski
 * Date: 2026-02-13
 */
class AssetRepositoryTest extends WowCharacterSpringTest {
	@Autowired
	AssetRepository assetRepository;

	@ParameterizedTest
	@MethodSource("getAvailableOptionData")
	void assets_available_to_each_class_are_correct(AvailableAssetsData data) {
		assertOptions(
				data.characterClassId(),
				data.gameVersionId().getLastPhase(),
				data.expected()
		);
	}

	static List<AvailableAssetsData> getAvailableOptionData() {
		return List.of(
				new AvailableAssetsData(
						MAGE,
						VANILLA,
						List.of(
								"Arcane Intellect",
								"Arcane Brilliance",
								"Mage Armor",
								"Improved Scorch"
						)
				),
				new AvailableAssetsData(
						MAGE,
						TBC,
						List.of(
								"Arcane Intellect",
								"Arcane Brilliance",
								"Mage Armor",
								"Molten Armor",
								"Improved Scorch"
						)
				),

				new AvailableAssetsData(
						WARLOCK,
						VANILLA,
						List.of(
								"Demon Skin",
								"Demon Armor",
								"Curse of Shadow",
								"Curse of the Elements",
								"Touch of Shadow",
								"Burning Wish",
								"Imp"
						)
				),
				new AvailableAssetsData(
						WARLOCK,
						TBC,
						List.of(
								"Demon Skin",
								"Demon Armor",
								"Fel Armor",
								"Curse of the Elements",
								"Touch of Shadow",
								"Burning Wish",
								"Imp"
						)
				),

				new AvailableAssetsData(
						PRIEST,
						VANILLA,
						List.of(
								"Power Word: Fortitude",
								"Prayer of Fortitude",
								"Divine Spirit",
								"Prayer of Spirit",
								"Shadowform",
								"Shadow Weaving"
						)
				),
				new AvailableAssetsData(
						PRIEST,
						TBC,
						List.of(
								"Power Word: Fortitude",
								"Prayer of Fortitude",
								"Divine Spirit",
								"Prayer of Spirit",
								"Shadowform",
								"Shadow Weaving",
								"Misery"
						)
				),

				new AvailableAssetsData(
						DRUID,
						VANILLA,
						List.of(
								"Mark of the Wild",
								"Gift of the Wild",
								"Moonkin Aura"
						)
				),
				new AvailableAssetsData(
						DRUID,
						TBC,
						List.of(
								"Mark of the Wild",
								"Gift of the Wild",
								"Moonkin Aura"
						)
				),

				new AvailableAssetsData(
						SHAMAN,
						VANILLA,
						List.of(
								"Mana Spring Totem",
								"Mana Tide Totem"
						)
				),
				new AvailableAssetsData(
						SHAMAN,
						TBC,
						List.of(
								"Totem of Wrath",
								"Wrath of Air Totem",
								"Mana Spring Totem",
								"Mana Tide Totem"
						)
				),

				new AvailableAssetsData(
						PALADIN,
						VANILLA,
						List.of(
								"Blessing of Wisdom",
								"Greater Blessing of Wisdom",
								"Blessing of Kings",
								"Greater Blessing of Kings",
								"Sanctity Aura"
						)
				),
				new AvailableAssetsData(
						PALADIN,
						TBC,
						List.of(
								"Blessing of Wisdom",
								"Greater Blessing of Wisdom",
								"Blessing of Kings",
								"Greater Blessing of Kings",
								"Sanctity Aura"
						)
				)
		);
	}

	record AvailableAssetsData(CharacterClassId characterClassId, GameVersionId gameVersionId, List<String> expected) {}

	void assertOptions(CharacterClassId characterClassId, PhaseId phaseId, List<String> expected) {
		var actual = assetRepository.getAvailableAssets(phaseId).stream()
				.filter(asset -> asset.getRequiredCharacterClassIds().contains(characterClassId))
				.sorted(Comparator.comparing(Asset::id))
				.map(Asset::name)
				.toList();

		assertThat(actual).isEqualTo(expected);
	}
}