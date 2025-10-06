package wow.commons.repository.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;
import wow.commons.constant.AttributeConditions;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.EnchantId;
import wow.commons.model.item.EnchantSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.attribute.AttributeId.STAMINA;
import static wow.commons.model.categorization.ItemRarity.EPIC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class EnchantRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	EnchantRepository enchantRepository;

	@Test
	void basicEnchantInfo() {
		var enchantId = EnchantId.of(31372);

		var enchant = enchantRepository.getEnchant(enchantId, TBC_P5).orElseThrow();

		assertThat(enchant.getId()).isEqualTo(enchantId);
		assertThat(enchant.getName()).isEqualTo("Runic Spellthread");
		assertThat(enchant.getItemTypes()).hasSameElementsAs(List.of(ItemType.LEGS));
		assertThat(enchant.getItemSubTypes()).isEmpty();
		assertThat(enchant.getRarity()).isEqualTo(EPIC);
	}

	@Test
	void enchantStats() {
		var enchantId = EnchantId.of(31372);

		var enchant = enchantRepository.getEnchant(enchantId, TBC_P5).orElseThrow();

		assertEffect(
				enchant.getEffect(),
				Attributes.of(
						Attribute.of(POWER, 35, AttributeConditions.SPELL),
						Attribute.of(STAMINA, 20)
				),
				"Use: Permanently embroiders spellthread into pants, increasing spell damage and healing by up to 35 and Stamina by 20.",
				new EnchantSource(enchant)
		);
	}
}
