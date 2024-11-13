package wow.minmax.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.client.dto.EnchantDTO;
import wow.commons.model.categorization.ArmorSubType;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class EnchantOptionsDTOTest {
	@Test
	void withEnchants() {
		var dto = new EnchantOptionsDTO(ItemType.HEAD, ArmorSubType.CLOTH, List.of());
		var enchant = new EnchantDTO(1, "name", ItemRarity.RARE, "icon", "tooltip");
		var changed = dto.withEnchants(List.of(enchant));

		assertThat(changed).isEqualTo(new EnchantOptionsDTO(
				ItemType.HEAD, ArmorSubType.CLOTH, List.of(enchant)
		));
	}
}