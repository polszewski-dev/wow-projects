package wow.minmax.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.PveRole.CASTER_DPS;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.spells.SpellId.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class MinmaxConfigRepositoryTest extends WowMinMaxSpringTest {
	@Autowired
	MinmaxConfigRepository underTest;

	@Test
	void getViewConfig() {
		ViewConfig config = underTest.getViewConfig(WARLOCK, CASTER_DPS, TBC).orElseThrow();

		assertThat(config.getRelevantSpells()).hasSameElementsAs(List.of(
				SHADOW_BOLT, CURSE_OF_DOOM, CURSE_OF_AGONY, CORRUPTION, IMMOLATE, SHADOWBURN,
				UNSTABLE_AFFLICTION, SIPHON_LIFE, SEED_OF_CORRUPTION_DIRECT, DRAIN_LIFE,
				CONFLAGRATE, INCINERATE, SEARING_PAIN, DEATH_COIL, HELLFIRE, RAIN_OF_FIRE
		));
	}

	@Test
	void getFindUpgradesConfig() {
		FindUpgradesConfig config = underTest.getFindUpgradesConfig(WARLOCK, CASTER_DPS, TBC).orElseThrow();

		assertThat(config.getEnchantNames()).hasSameElementsAs(List.of(
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