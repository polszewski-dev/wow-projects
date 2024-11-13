package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.GemDTO;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.GemColor;
import wow.commons.repository.item.GemRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class GemConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	GemConverter gemConverter;

	@Autowired
	GemRepository gemRepository;

	@Test
	void convertColoredGem() {
		var gem = gemRepository.getGem("Reckless Pyrestone", TBC_P5).orElseThrow();

		var converted = gemConverter.convert(gem);

		assertThat(converted).isEqualTo(
				new GemDTO(
						35760,
						"Reckless Pyrestone",
						GemColor.ORANGE,
						ItemRarity.EPIC,
						"Jewelcrafting",
						"Reckless Pyrestone",
						"inv_jewelcrafting_pyrestone_02",
						"""
						+5 Spell Haste Rating
						+6 Spell Damage"""
				)
		);
	}

	@Test
	void convertMetaGem() {
		var gem = gemRepository.getGem("Chaotic Skyfire Diamond", TBC_P5).orElseThrow();

		var converted = gemConverter.convert(gem);

		assertThat(converted).isEqualTo(
				new GemDTO(
						34220,
						"Chaotic Skyfire Diamond",
						GemColor.META,
						ItemRarity.RARE,
						"Jewelcrafting",
						"Chaotic Skyfire Diamond",
						"inv_misc_gem_diamond_07",
						"""
						+12 Spell Critical
						3% Increased Critical Damage
						____________________________________________________________
						
						Requires at least 2 Blue Gems"""
				)
		);
	}

	@Test
	void convertBack() {
		var gem = new GemDTO(35760, null, null, null, null, null, null, null);

		var converted = gemConverter.convertBack(gem, TBC_P5);

		assertThat(converted.getId()).isEqualTo(35760);
	}
}