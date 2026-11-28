package wow.character.model.equipment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.TestEvent;
import wow.character.model.character.TestPlayerCharacter;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.ItemSetBonus;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static wow.character.model.character.TestEvent.*;
import static wow.commons.model.categorization.ItemSlot.*;

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

	@Test
	void equip_item_set_pieces() {
		equipment.equip(shoulder);
		equipment.equip(chest);
		equipment.equip(hands);
		equipment.equip(legs);
		equipment.equip(feet);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new ItemStatsAdded(chest),
				new ItemSetBonusAdded(malefic2PBonus),
				new ItemStatsAdded(hands),
				new ItemStatsAdded(legs),
				new ItemSetBonusAdded(malefic4PBonus),
				new ItemStatsAdded(feet)
		);
	}

	@Test
	void unequip_item_set_pieces() {
		equipment.equip(shoulder);
		equipment.equip(chest);
		equipment.equip(hands);
		equipment.equip(legs);
		equipment.equip(feet);

		resetEvents();

		equipment.equip(null, SHOULDER);
		equipment.equip(null, CHEST);
		equipment.equip(null, HANDS);
		equipment.equip(null, LEGS);
		equipment.equip(null, FEET);

		assertEvents(
				new ItemStatsRemoved(shoulder),
				new ItemStatsRemoved(chest),
				new ItemSetBonusRemoved(malefic4PBonus),
				new ItemStatsRemoved(hands),
				new ItemStatsRemoved(legs),
				new ItemSetBonusRemoved(malefic2PBonus),
				new ItemStatsRemoved(feet)
		);
	}

	@Test
	void equip_item_with_enchant() {
		shoulder.enchant(shoulderEnchant);
		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new EnchantStatsAdded(shoulderEnchant)
		);
	}

	@Test
	void equip_item_and_enchant_it() {
		equipment.equip(shoulder);
		shoulder.enchant(shoulderEnchant);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new EnchantStatsAdded(shoulderEnchant)
		);
	}

	@Test
	void unequip_item_with_enchant() {
		shoulder.enchant(shoulderEnchant);
		equipment.equip(shoulder);

		resetEvents();

		equipment.equip(null, SHOULDER);

		assertEvents(
				new ItemStatsRemoved(shoulder),
				new EnchantStatsRemoved(shoulderEnchant)
		);
	}

	@Test
	void equip_item_no_sockets() {
		equipment.equip(offHand);

		assertEvents(
				new ItemStatsAdded(offHand)
		);
	}

	@Test
	void unequip_item_no_sockets() {
		equipment.equip(offHand);
		resetEvents();

		equipment.equip(null, OFF_HAND);

		assertEvents(
				new ItemStatsRemoved(offHand)
		);
	}

	@Test
	void equip_item_empty_sockets() {
		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder)
		);
	}

	@Test
	void unequip_item_empty_sockets() {
		equipment.equip(shoulder);
		resetEvents();

		equipment.equip(null, SHOULDER);

		assertEvents(
				new ItemStatsRemoved(shoulder)
		);
	}

	@Test
	void equip_item_with_matching_gems() {
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(yellowGem),
				new SocketBonusAdded(shoulderSocketBonus)
		);
	}

	@Test
	void equip_item_and_insert_matching_gems() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(yellowGem),
				new SocketBonusAdded(shoulderSocketBonus)
		);
	}

	@Test
	void unequip_item_with_matching_gems() {
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		resetEvents();

		equipment.equip(null, SHOULDER);

		assertEvents(
				new ItemStatsRemoved(shoulder),
				new GemStatsRemoved(blueGem),
				new SocketBonusRemoved(shoulderSocketBonus),
				new GemStatsRemoved(yellowGem)
		);
	}

	@Test
	void equip_item_with_mismatched_gems() {
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, blueGem);
		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(yellowGem),
				new GemStatsAdded(blueGem)
		);
	}

	@Test
	void equip_item_and_insert_mismatched_gems() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, blueGem);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(yellowGem),
				new GemStatsAdded(blueGem)
		);
	}

	@Test
	void unequip_item_with_mismatched_gems() {
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, blueGem);
		equipment.equip(shoulder);

		resetEvents();

		equipment.equip(null, SHOULDER);

		assertEvents(
				new ItemStatsRemoved(shoulder),
				new GemStatsRemoved(yellowGem),
				new GemStatsRemoved(blueGem)
		);
	}

	@Test
	void equip_item_with_meta_gem__enabler_condition_is_true() {
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		resetEvents();

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(metaGem),
				new SocketBonusAdded(headSocketBonus)
		);
	}

	@Test
	void equip_item_and_insert_meta_gem__enabler_condition_is_true() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		resetEvents();

		equipment.equip(head);
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(metaGem),
				new SocketBonusAdded(headSocketBonus)
		);
	}

	@Test
	void unequip_item_with_meta_gem__enabler_condition_is_true() {
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		resetEvents();

		equipment.equip(null, HEAD);

		assertEvents(
				new ItemStatsRemoved(head),
				new GemStatsRemoved(metaGem),
				new SocketBonusRemoved(headSocketBonus),
				new GemStatsRemoved(blueGem)
		);
	}

	@Test
	void equip_item_with_meta_gem__enabler_condition_is_false() {
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		resetEvents();

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem)
		);
	}

	@Test
	void equip_item_and_insert_meta_gem__enabler_condition_is_false() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);

		resetEvents();

		equipment.equip(head);
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem)
		);
	}

	@Test
	void unequip_item_with_meta_gem__enabler_condition_is_false() {
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		resetEvents();

		equipment.equip(null, HEAD);

		assertEvents(
				new ItemStatsRemoved(head),
				new GemStatsRemoved(blueGem)
		);
	}

	@Test
	void meta_gem_activates_when_two_blue_gems_are_inserted() {
		equipment.equip(head);
		equipment.equip(shoulder);

		head.insertGem(0, metaGem);
		head.insertGem(1, redGem);
		head.insertGem(1, blueGem);

		assertEvents(
				new ItemStatsAdded(head),
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(redGem),
				new GemStatsRemoved(redGem),
				new GemStatsAdded(blueGem)
		);

		resetEvents();

		shoulder.insertGem(0, redGem);
		shoulder.insertGem(1, redGem);
		shoulder.insertGem(1, yellowGem);
		shoulder.insertGem(0, blueGem);

		assertEvents(
				new GemStatsAdded(redGem),
				new GemStatsAdded(redGem),
				new GemStatsRemoved(redGem),
				new GemStatsAdded(yellowGem),
				new GemStatsRemoved(redGem),
				new GemStatsAdded(blueGem),
				new SocketBonusAdded(shoulderSocketBonus),
				new GemStatsAdded(metaGem),
				new SocketBonusAdded(headSocketBonus)
		);
	}

	@Test
	void reset_items_with_meta_gem__enabler_condition_is_true() {
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		resetEvents();

		equipment.reset();

		assertEvents(
				new ItemStatsRemoved(head),
				new GemStatsRemoved(metaGem),
				new SocketBonusRemoved(headSocketBonus),
				new GemStatsRemoved(blueGem),
				new ItemStatsRemoved(shoulder),
				new GemStatsRemoved(blueGem),
				new SocketBonusRemoved(shoulderSocketBonus),
				new GemStatsRemoved(yellowGem)
		);
	}

	@Test
	void reset_items_with_meta_gem__enabler_condition_is_false() {
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);
		equipment.equip(shoulder);

		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		resetEvents();

		equipment.reset();

		assertEvents(
				new ItemStatsRemoved(head),
				new GemStatsRemoved(blueGem),
				new ItemStatsRemoved(shoulder),
				new GemStatsRemoved(yellowGem),
				new GemStatsRemoved(yellowGem)
		);
	}

	@Test
	void enabling_and_disabling_socket_bonus() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		resetEvents();

		shoulder.insertGem(0, yellowGem);

		assertEvents(
				new GemStatsRemoved(blueGem),
				new SocketBonusRemoved(shoulderSocketBonus),
				new GemStatsAdded(yellowGem)
		);

		resetEvents();

		shoulder.insertGem(0, blueGem);

		assertEvents(
				new GemStatsRemoved(yellowGem),
				new GemStatsAdded(blueGem),
				new SocketBonusAdded(shoulderSocketBonus)
		);

		resetEvents();

		shoulder.insertGem(0, yellowGem);

		assertEvents(
				new GemStatsRemoved(blueGem),
				new SocketBonusRemoved(shoulderSocketBonus),
				new GemStatsAdded(yellowGem)
		);
	}

	@Test
	void reequipping_item_with_matching_gems() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, SHOULDER);

		resetEvents();

		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(yellowGem),
				new SocketBonusAdded(shoulderSocketBonus)
		);
	}

	@Test
	void reequipping_item_with_non_matching_gems() {
		equipment.equip(shoulder);
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, SHOULDER);

		resetEvents();

		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(yellowGem),
				new GemStatsAdded(yellowGem)
		);
	}

	@Test
	void reequipping_item_with_matching_gems_and_meta_present() {
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, SHOULDER);

		resetEvents();

		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(metaGem),
				new SocketBonusAdded(headSocketBonus),
				new GemStatsAdded(yellowGem),
				new SocketBonusAdded(shoulderSocketBonus)
		);
	}

	@Test
	void reequipping_item_with_non_matching_gems_and_meta_present() {
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		equipment.equip(shoulder);
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, SHOULDER);

		resetEvents();

		equipment.equip(shoulder);

		assertEvents(
				new ItemStatsAdded(shoulder),
				new GemStatsAdded(yellowGem),
				new GemStatsAdded(yellowGem)
		);
	}


	@Test
	void reequipping_item_with_enabled_meta() {
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		equipment.equip(shoulder);
		shoulder.insertGem(0, blueGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, HEAD);

		resetEvents();

		equipment.equip(head);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem),
				new GemStatsAdded(metaGem),
				new SocketBonusAdded(headSocketBonus)
		);
	}

	@Test
	void reequipping_item_with_disabled_meta() {
		head.insertGem(0, metaGem);
		head.insertGem(1, blueGem);
		equipment.equip(head);

		equipment.equip(shoulder);
		shoulder.insertGem(0, yellowGem);
		shoulder.insertGem(1, yellowGem);

		equipment.equip(null, HEAD);

		resetEvents();

		equipment.equip(head);

		assertEvents(
				new ItemStatsAdded(head),
				new GemStatsAdded(blueGem)
		);
	}

	TestPlayerCharacter player;
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

	ItemSetBonus malefic2PBonus;
	ItemSetBonus malefic4PBonus;

	Enchant shoulderEnchant;

	Gem metaGem;
	Gem redGem;
	Gem orangeGem;
	Gem yellowGem;
	Gem greenGem;
	Gem blueGem;
	Gem violetGem;

	Effect headSocketBonus;
	Effect shoulderSocketBonus;

	@BeforeEach
	void setup() {
		player = getPlayer();
		equipment = player.getEquipment();

		resetEvents();//remove talents events etc

		head = getItem("Dark Conjuror's Collar");
		shoulder = getItem("Mantle of the Malefic");
		chest = getItem("Robe of the Malefic");
		hands = getItem("Gloves of the Malefic");
		legs = getItem("Leggings of the Malefic");
		feet = getItem("Boots of the Malefic");

		twoHand = getItem("Grand Magister's Staff of Torrents");
		mainHand = getItem("Sunflare");
		offHand = getItem("Heart of the Pit");

		malefic2PBonus = shoulder.getItemSet().getItemSetBonuses(2).getFirst();
		malefic4PBonus = shoulder.getItemSet().getItemSetBonuses(4).getFirst();

		shoulderEnchant = getEnchant("Greater Inscription of the Orb");

		metaGem = getGem("Chaotic Skyfire Diamond");
		redGem = getGem(32196);
		orangeGem = getGem("Reckless Pyrestone");
		yellowGem = getGem("Quick Lionseye");
		greenGem = getGem("Forceful Seaspray Emerald");
		blueGem = getGem("Solid Empyrean Sapphire");
		violetGem  = getGem("Glowing Shadowsong Amethyst");

		headSocketBonus = head.getSocketBonus();
		shoulderSocketBonus = shoulder.getSocketBonus();
	}

	void assertEvents(TestEvent... expected) {
		assertThat(player.events).isEqualTo(List.of(expected));
	}

	void resetEvents() {
		player.events.clear();
	}
}