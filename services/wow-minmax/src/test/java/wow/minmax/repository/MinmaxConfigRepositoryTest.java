package wow.minmax.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.Player;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.*;
import static wow.minmax.model.config.CharacterFeature.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class MinmaxConfigRepositoryTest extends WowMinMaxSpringTest {
	@Autowired
	MinmaxConfigRepository underTest;

	@Test
	void getViewConfig() {
		ViewConfig config = underTest.getViewConfig(getCharacter()).orElseThrow();

		assertThat(config.relevantSpells()).hasSameElementsAs(List.of(
				SHADOW_BOLT, CURSE_OF_DOOM, CURSE_OF_AGONY, CORRUPTION, IMMOLATE, SHADOWBURN,
				UNSTABLE_AFFLICTION, SIPHON_LIFE, SEED_OF_CORRUPTION, DRAIN_LIFE, DRAIN_SOUL,
				CONFLAGRATE, INCINERATE, SEARING_PAIN, DEATH_COIL, HELLFIRE, RAIN_OF_FIRE
		));
	}

	@Test
	void getFeatures() {
		var features = underTest.getFeatures(getCharacter());

		assertThat(features).hasSameElementsAs(Set.of(
				COMBAT_RATINGS,
				GEMS,
				HEROICS
		));
	}

	@Test
	void hasFeature() {
		Player character = getCharacter();

		assertThat(underTest.hasFeature(character, COMBAT_RATINGS)).isTrue();
		assertThat(underTest.hasFeature(character, GEMS)).isTrue();
		assertThat(underTest.hasFeature(character, HEROICS)).isTrue();

		assertThat(underTest.hasFeature(character, WORLD_BUFFS)).isFalse();
		assertThat(underTest.hasFeature(character, GLYPHS)).isFalse();
	}

	@Test
	void getFindUpgradesConfig() {
		FindUpgradesConfig config = underTest.getFindUpgradesConfig(getCharacter()).orElseThrow();

		assertThat(config.enchantNames()).hasSameElementsAs(List.of(
				"Glyph of Power",
				"Greater Inscription of the Orb",
				"Greater Inscription of Discipline",
				"Enchant Cloak - Subtlety",
				"Enchant Chest - Exceptional Stats",
				"Enchant Bracer - Spellpower",
				"Enchant Gloves - Major Spellpower",
				"Enchant Gloves - Spell Strike",
				"Runic Spellthread",
				"Enchant Boots - Boar's Speed",
				"Enchant Weapon - Major Spellpower",
				"Enchant Weapon - Soulfrost",
				"Enchant Weapon - Sunfire",
				"Enchant Ring - Spellpower"
		));
	}
}