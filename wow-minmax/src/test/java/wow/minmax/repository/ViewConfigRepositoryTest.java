package wow.minmax.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.ViewConfig;

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
class ViewConfigRepositoryTest extends WowMinMaxSpringTest {
	@Autowired
	ViewConfigRepository underTest;

	@Test
	void getViewConfig() {
		ViewConfig viewConfig = underTest.getViewConfig(WARLOCK, CASTER_DPS, TBC).orElseThrow();

		assertThat(viewConfig.getRelevantSpells()).hasSameElementsAs(List.of(
				SHADOW_BOLT, CURSE_OF_DOOM, CURSE_OF_AGONY, CORRUPTION, IMMOLATE, SHADOWBURN,
				UNSTABLE_AFFLICTION, SIPHON_LIFE, SEED_OF_CORRUPTION_DIRECT, DRAIN_LIFE,
				CONFLAGRATE, INCINERATE, SEARING_PAIN, DEATH_COIL, HELLFIRE, RAIN_OF_FIRE
		));
	}
}