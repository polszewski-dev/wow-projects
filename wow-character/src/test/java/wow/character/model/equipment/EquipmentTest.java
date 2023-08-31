package wow.character.model.equipment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.item.Gem;

import static org.assertj.core.api.Assertions.*;
import static wow.commons.model.categorization.ItemSlot.HEAD;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class EquipmentTest extends WowCharacterSpringTest {
	@Test
	@DisplayName("Equipping null does nothing")
	void noItem() {
		Equipment equipment = new Equipment();

		assertThatNoException().isThrownBy(() -> equipment.equip(null));
	}

	@Test
	@DisplayName("Equipping wrong item generates error")
	void wrongItem() {
		EquippableItem mainHand = getItem("Sunflare");

		Equipment equipment = new Equipment();

		assertThatThrownBy(() -> equipment.equip(mainHand, HEAD))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Equipping off-hand while 2-hander is equipped generates error")
	void equippingWeapons() {
		EquippableItem twoHand = getItem("Grand Magister's Staff of Torrents");
		EquippableItem mainHand = getItem("Sunflare");
		EquippableItem offHand = getItem("Heart of the Pit");

		Equipment equipment = new Equipment();
		equipment.equip(twoHand);

		assertThat(equipment.getMainHand()).isEqualTo(twoHand);

		assertThatThrownBy(() -> equipment.equip(offHand))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Can't equip offhand while having 2-hander");

		assertThatNoException().isThrownBy(() -> {
			equipment.equip(mainHand);
			equipment.equip(offHand);
		});
	}

	@Test
	@DisplayName("Colored gems matchings are correct")
	void gemCounts() {
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		EquippableItem item = getItem("Mantle of the Malefic");// blue + yellow socket

		Equipment equipment = new Equipment();

		equipment.equip(item);

		item.gem(violetGem, orangeGem);

		assertThat(equipment.allSocketsHaveMatchingGems(item)).isTrue();

		item.gem(orangeGem, violetGem);

		assertThat(equipment.allSocketsHaveMatchingGems(item)).isFalse();
	}

	@Test
	@DisplayName("Meta matching is correct")
	void meta() {
		Gem metaGem = getGem("Chaotic Skyfire Diamond");
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		EquippableItem head = getItem("Dark Conjuror's Collar");// meta + blue socket
		EquippableItem shoulder = getItem("Mantle of the Malefic");// blue + yellow socket

		Equipment equipment = new Equipment();

		equipment.equip(head);
		equipment.equip(shoulder);

		head.gem(metaGem, violetGem);
		shoulder.gem(violetGem, orangeGem);

		assertThat(equipment.allSocketsHaveMatchingGems(head)).isTrue();
		assertThat(equipment.allSocketsHaveMatchingGems(shoulder)).isTrue();
	}
}