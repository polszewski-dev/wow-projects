package wow.commons.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wow.commons.WowCommonsTestConfig;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemDataRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WowCommonsTestConfig.class)
class AttributeEvaluatorTest {
	@Autowired
	ItemDataRepository itemDataRepository;

	@Test
	@DisplayName("nothingToSolve returns empty attributes if none were added")
	void empty() {
		Attributes attributes = AttributeEvaluator.of()
				.nothingToSolve()
				.getAttributes();

		assertThat(attributes.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("nothingToSolve sums item attributes, leaves special abilities/sockets/set pieces")
	void nothingToSolveSumsElementsAndLeavesSpecialAbilities() {
		Equipment equipment = getEquipment();

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(equipment)
				.nothingToSolve()
				.getAttributes();

		assertThat(attributes.getSpecialAbilities()).hasSize(2);
		assertThat(attributes.getList(ComplexAttributeId.SOCKETS)).hasSize(12);
		assertThat(attributes.getList(ComplexAttributeId.SET_PIECES)).hasSize(4);
	}

	@Test
	@DisplayName("solveAllLeaveAbilities sums item attributes/sockets/set pieces, leaves special abilities")
	void solveAllLeaveAbilitiesSumsElementsAndLeavesSpecialAbilities() {
		Equipment equipment = getEquipment();

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(equipment)
				.solveAllLeaveAbilities()
				.getAttributes();

		assertThat(attributes.getSpecialAbilities()).hasSize(4);
		assertThat(attributes.getList(ComplexAttributeId.SOCKETS)).isEmpty();
		assertThat(attributes.getList(ComplexAttributeId.SET_PIECES)).isEmpty();

	}

	private Equipment getEquipment() {
		Equipment equipment = new Equipment();

		Gem metaGem = getGem("Chaotic Skyfire Diamond").orElseThrow();
		Gem orangeGem = getGem("Reckless Pyrestone").orElseThrow();
		Gem violetGem = getGem("Glowing Shadowsong Amethyst").orElseThrow();

		equipment.set(getItem("Dark Conjuror's Collar").enchant(getEnchant("Glyph of Power")).gem(metaGem, violetGem));
		equipment.set(getItem("Pendant of Sunfire").gem(orangeGem));
		equipment.set(getItem("Mantle of the Malefic").enchant(getEnchant("Greater Inscription of the Orb")).gem(violetGem, orangeGem));
		equipment.set(getItem("Tattered Cape of Antonidas").enchant(getEnchant("Enchant Cloak - Subtlety")).gem(orangeGem));
		equipment.set(getItem("Sunfire Robe").enchant(getEnchant("Enchant Chest - Exceptional Stats")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Bracers of the Malefic").enchant(getEnchant("Enchant Bracer – Spellpower")).gem(orangeGem));
		equipment.set(getItem("Sunfire Handwraps").enchant(getEnchant("Enchant Gloves - Major Spellpower")).gem(orangeGem, orangeGem));
		equipment.set(getItem("Belt of the Malefic").gem(orangeGem));
		equipment.set(getItem("Leggings of Calamity").enchant(getEnchant("Runic Spellthread")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Boots of the Malefic").enchant(getEnchant("Enchant Boots - Boar's Speed")).gem(orangeGem));
		equipment.set(getItem("Ring of Omnipotence").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_1);
		equipment.set(getItem("Loop of Forged Power").enchant(getEnchant("Enchant Ring – Spellpower")), ItemSlot.FINGER_2);
		equipment.set(getItem("The Skull of Gul'dan"), ItemSlot.TRINKET_1);
		equipment.set(getItem("Shifting Naaru Sliver"), ItemSlot.TRINKET_2);
		equipment.set(getItem("Grand Magister's Staff of Torrents").enchant(getEnchant("Enchant Weapon – Soulfrost")).gem(orangeGem, orangeGem, orangeGem));
		equipment.set(getItem("Wand of the Demonsoul").gem(orangeGem));

		return equipment;
	}

	private Optional<Gem> getGem(String name) {
		return itemDataRepository.getGem(name);
	}

	private EquippableItem getItem(String name) {
		return new EquippableItem(itemDataRepository.getItem(name).orElseThrow());
	}

	private Enchant getEnchant(String name) {
		return itemDataRepository.getEnchant(name).orElseThrow();
	}
}