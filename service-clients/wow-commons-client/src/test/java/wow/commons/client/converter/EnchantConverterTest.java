package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.EnchantDTO;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.repository.item.EnchantRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class EnchantConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	EnchantConverter enchantConverter;

	@Autowired
	EnchantRepository enchantRepository;

	@Test
	void convert() {
		var enchant = enchantRepository.getEnchant(27982, TBC_P5).orElseThrow();

		var converted = enchantConverter.convert(enchant);

		assertThat(converted).isEqualTo(
				new EnchantDTO(
						27982,
						"Enchant Weapon - Soulfrost",
						ItemRarity.UNSPECIFIED,
						"spell_holy_greaterheal",
						"Permanently enchant a Melee Weapon to add up to 54 damage to frost and shadow spells."
				)
		);
	}

	@Test
	void convertBack() {
		var enchant = new EnchantDTO(27982, null, null, null, null);

		var converted = enchantConverter.convertBack(enchant, TBC_P5);

		assertThat(converted.getId()).isEqualTo(27982);
	}
}