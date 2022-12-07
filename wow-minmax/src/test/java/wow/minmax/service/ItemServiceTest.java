package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class ItemServiceTest extends ServiceTest {
	@Autowired
	@Qualifier("itemService")
	ItemService underTest;

	@Test
	void getItem() {
		Item item = underTest.getItem(34364, playerProfile.getPhase());
		assertThat(item.getId()).isEqualTo(34364);
	}

	@Test
	void getItemsBySlot() {
		List<Item> itemsBySlot = underTest.getItemsBySlot(playerProfile, ItemSlot.TRINKET_1).stream()
				.sorted(Comparator.comparing(Item::getName))
				.collect(Collectors.toList());

		List<String> names = itemsBySlot.stream().map(Item::getName).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Shifting Naaru Sliver",
				"The Skull of Gul'dan"
		));
	}

	@Test
	void getHandEnchants() {
		List<Enchant> enchants = underTest.getEnchants(playerProfile, ItemType.HANDS).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.collect(Collectors.toList());

		List<String> names = enchants.stream().map(Enchant::getName).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Gloves - Major Spellpower",
				"Enchant Gloves - Spell Strike"
		));
	}

	@Test
	void getChestEnchants() {
		List<Enchant> enchants = underTest.getEnchants(playerProfile, ItemType.CHEST).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.collect(Collectors.toList());

		List<String> names = enchants.stream().map(Enchant::getName).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Chest - Exceptional Stats"
		));
	}

	@Test
	void getFeetEnchants() {
		List<Enchant> enchants = underTest.getEnchants(playerProfile, ItemType.FEET).stream()
				.sorted(Comparator.comparing(Enchant::getName))
				.collect(Collectors.toList());

		List<String> names = enchants.stream().map(Enchant::getName).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Enchant Boots - Boar's Speed"
		));
	}

	@Test
	void getGems() {
		List<Gem> metaGems = underTest.getGems(playerProfile, SocketType.META, true);
		List<Gem> coloredGems = underTest.getGems(playerProfile, SocketType.YELLOW, true);

		List<String> metaGemNames = metaGems.stream().map(Gem::getName).collect(Collectors.toList());

		assertThat(metaGemNames).hasSameElementsAs(List.of(
				"Chaotic Skyfire Diamond"
		));

		List<String> coloredGemNames = coloredGems.stream().map(Gem::getName).collect(Collectors.toList());

		assertThat(coloredGemNames).hasSameElementsAs(List.of(
				"Runed Crimson Spinel",
				"Glowing Shadowsong Amethyst",
				"Forceful Seaspray Emerald",
				"Reckless Pyrestone",
				"Veiled Pyrestone",
				"Potent Pyrestone",
				"Quick Lionseye"
		));
	}

	@Test
	void testGetGems() {
		List<Gem> coloredGems = underTest.getGems(playerProfile, getItem("Sunfire Robe").getSocketType(1), false);

		List<String> coloredGemNames = coloredGems.stream().map(Gem::getName).collect(Collectors.toList());

		assertThat(coloredGemNames).hasSameElementsAs(List.of(
				"Runed Crimson Spinel",
				"Glowing Shadowsong Amethyst",
				"Forceful Seaspray Emerald",
				"Reckless Pyrestone",
				"Veiled Pyrestone",
				"Potent Pyrestone",
				"Quick Lionseye"
		));
	}

	@Test
	void getGemCombos() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(playerProfile, getItem("Bracers of the Malefic").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Runed Crimson Spinel",
				"Glowing Shadowsong Amethyst",
				"Forceful Seaspray Emerald",
				"Reckless Pyrestone",
				"Veiled Pyrestone",
				"Potent Pyrestone",
				"Quick Lionseye"
		));
	}

	@Test
	void getGemCombos2() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(playerProfile, getItem("Dark Conjuror's Collar").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).collect(Collectors.toList());

		assertThat(names).hasSameElementsAs(List.of(
				"Chaotic Skyfire Diamond,Runed Crimson Spinel",
				"Chaotic Skyfire Diamond,Glowing Shadowsong Amethyst",
				"Chaotic Skyfire Diamond,Forceful Seaspray Emerald",
				"Chaotic Skyfire Diamond,Reckless Pyrestone",
				"Chaotic Skyfire Diamond,Veiled Pyrestone",
				"Chaotic Skyfire Diamond,Potent Pyrestone",
				"Chaotic Skyfire Diamond,Quick Lionseye"
		));
	}

	@Test
	void getGemCombos3() {
		List<Gem[]> gemCombos = underTest.getBestGemCombos(playerProfile, getItem("Sunfire Robe").getItem());

		List<String> names = gemCombos.stream().map(x -> Stream.of(x).map(Gem::getName).collect(Collectors.joining(","))).collect(Collectors.toList());

		assertThat(names).hasSize(74);
	}
}