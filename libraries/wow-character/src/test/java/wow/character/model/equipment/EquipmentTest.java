package wow.character.model.equipment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.item.Gem;

import static org.assertj.core.api.Assertions.*;
import static wow.commons.model.categorization.ItemSlot.HEAD;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class EquipmentTest extends WowCharacterSpringTest {
	@Test
	void equipping_null_does_nothing() {
		assertThatNoException().isThrownBy(() -> equipment.equip(null));
	}

	@Test
	void equipping_wrong_item_type_generates_error() {
		assertThatThrownBy(() -> equipment.equip(mainHand, HEAD))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void equipping_off_hand_while_2_hander_is_equipped_generates_error() {
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
	void colored_gems_matchings_are_correct() {
		equipment.equip(shoulder);

		shoulder.gem(violetGem, orangeGem);

		assertThat(equipment.allSocketsHaveMatchingGems(shoulder)).isTrue();

		shoulder.gem(orangeGem, violetGem);

		assertThat(equipment.allSocketsHaveMatchingGems(shoulder)).isFalse();
	}

	@Test
	void meta_matching_is_correct() {
		equipment.equip(head);
		equipment.equip(shoulder);

		head.gem(metaGem, violetGem);
		shoulder.gem(violetGem, orangeGem);

		assertThat(equipment.allSocketsHaveMatchingGems(head)).isTrue();
		assertThat(equipment.allSocketsHaveMatchingGems(shoulder)).isTrue();
	}

	PlayerCharacter player;
	Equipment equipment;

	EquippableItem head;// meta + blue socket
	EquippableItem shoulder;// blue + yellow socket
	EquippableItem chest;// yellow + yellow + blue  socket
	EquippableItem hands;// yellow socket
	EquippableItem legs;// yellow socket
	EquippableItem feet;// yellow socket

	EquippableItem twoHand;// red + red + red socket
	EquippableItem mainHand;
	EquippableItem offHand;

	Gem metaGem;
	Gem redGem;
	Gem orangeGem;
	Gem yellowGem;
	Gem greenGem;
	Gem blueGem;
	Gem violetGem;

	@BeforeEach
	void setup() {
		player = getPlayer();
		equipment = player.getEquipment();

		head = getItem("Dark Conjuror's Collar");
		shoulder = getItem("Mantle of the Malefic");
		chest = getItem("Robe of the Malefic");
		hands = getItem("Gloves of the Malefic");
		legs = getItem("Leggings of the Malefic");
		feet = getItem("Boots of the Malefic");

		twoHand = getItem("Grand Magister's Staff of Torrents");
		mainHand = getItem("Sunflare");
		offHand = getItem("Heart of the Pit");

		metaGem = getGem("Chaotic Skyfire Diamond");
		redGem = getGem(32196);
		orangeGem = getGem("Reckless Pyrestone");
		yellowGem = getGem("Quick Lionseye");
		greenGem = getGem("Forceful Seaspray Emerald");
		blueGem = getGem("Solid Empyrean Sapphire");
		violetGem  = getGem("Glowing Shadowsong Amethyst");
	}
}