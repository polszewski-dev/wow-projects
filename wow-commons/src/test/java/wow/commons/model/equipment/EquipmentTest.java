package wow.commons.model.equipment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.WowCommonsSpringTest;
import wow.commons.model.item.Gem;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static wow.commons.model.categorization.ItemSlot.*;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class EquipmentTest extends WowCommonsSpringTest {
	@Test
	@DisplayName("Equipping null does nothing")
	void noItem() {
		Equipment equipment = new Equipment();

		assertThatNoException().isThrownBy(() -> equipment.set(null));
	}

	@Test
	@DisplayName("Equipping wrong item generates error")
	void wrongItem() {
		EquippableItem mainHand = getItem("Sunflare");

		Equipment equipment = new Equipment();

		assertThatThrownBy(() -> equipment.set(mainHand, HEAD))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Equipping off-hand while 2-hander is equipped generates error")
	void equippingWeapons() {
		EquippableItem twoHand = getItem("Grand Magister's Staff of Torrents");
		EquippableItem mainHand = getItem("Sunflare");
		EquippableItem offHand = getItem("Heart of the Pit");

		Equipment equipment = new Equipment();
		equipment.set(twoHand);

		assertThat(equipment.getMainHand()).isEqualTo(twoHand);

		assertThatThrownBy(() -> equipment.set(offHand))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Can't equip offhand while having 2-hander");

		assertThatNoException().isThrownBy(() -> {
			equipment.set(mainHand);
			equipment.set(offHand);
		});
	}

	@Test
	@DisplayName("Colored gems matchings are correct")
	void gemCounts() {
		Gem orangeGem = getGem("Reckless Pyrestone");
		Gem violetGem = getGem("Glowing Shadowsong Amethyst");

		EquippableItem item = getItem("Mantle of the Malefic");// blue + yellow socket

		Equipment equipment = new Equipment();

		equipment.set(item);

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

		equipment.set(head);
		equipment.set(shoulder);

		head.gem(metaGem, violetGem);
		shoulder.gem(violetGem, orangeGem);

		assertThat(equipment.allSocketsHaveMatchingGems(head)).isTrue();
		assertThat(equipment.allSocketsHaveMatchingGems(shoulder)).isTrue();
	}

	@Test
	@DisplayName("Placement of rings doesn't affect equals/hashCode")
	void ringPlacementDoesntMatter() {
		EquippableItem ring1 = getItem("Ring of Omnipotence");
		EquippableItem ring2 = getItem("Loop of Forged Power");

		Equipment equipment1 = new Equipment();
		Equipment equipment2 = new Equipment();

		equipment1.set(ring1, FINGER_1);
		equipment1.set(ring2, FINGER_2);
		equipment2.set(ring2, FINGER_1);
		equipment2.set(ring1, FINGER_2);

		assertThat(equipment2).isEqualTo(equipment1).hasSameHashCodeAs(equipment1);
	}

	@Test
	@DisplayName("Placement of trinkets doesn't affect equals/hashCode")
	void trinketPlacementDoesntMatter() {
		EquippableItem trinket1 = getItem("The Skull of Gul'dan");
		EquippableItem trinket2 = getItem("Shifting Naaru Sliver");

		Equipment equipment1 = new Equipment();
		Equipment equipment2 = new Equipment();

		equipment1.set(trinket1, TRINKET_1);
		equipment1.set(trinket2, TRINKET_2);
		equipment2.set(trinket2, TRINKET_1);
		equipment2.set(trinket1, TRINKET_2);

		assertThat(equipment2).isEqualTo(equipment1).hasSameHashCodeAs(equipment1);
	}

	@Test
	@DisplayName("Item diff")
	void itemDiff() {
		EquippableItem ring1 = getItem("Ring of Omnipotence");
		EquippableItem ring2 = getItem("Loop of Forged Power");
		EquippableItem trinket1 = getItem("The Skull of Gul'dan");
		EquippableItem trinket2 = getItem("Shifting Naaru Sliver");

		Equipment equipment1 = new Equipment();
		Equipment equipment2 = new Equipment();

		equipment1.set(ring1, FINGER_1);
		equipment1.set(ring2, FINGER_2);
		equipment1.set(trinket1, TRINKET_1);
		equipment1.set(trinket2, TRINKET_2);

		equipment2.set(ring2, FINGER_1);
		equipment2.set(ring1, FINGER_2);
		equipment2.set(trinket2, TRINKET_1);
		equipment2.set(trinket1, TRINKET_2);

		List<EquippableItem> itemDifference = equipment1.getItemDifference(equipment2);

		assertThat(itemDifference).isEmpty();
	}
}