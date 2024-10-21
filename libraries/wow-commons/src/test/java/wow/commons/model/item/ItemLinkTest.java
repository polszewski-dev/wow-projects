package wow.commons.model.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.categorization.ItemRarity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
class ItemLinkTest {
	@Test
	void testParse() {
		ItemLink itemLink = ItemLink.parse("|cffa335ee|Hitem:34333::::::::68::::::::|h[Coif of Alleria]|h|r");

		assertThat(itemLink.itemId()).isEqualTo(34333);
		assertThat(itemLink.name()).isEqualTo("Coif of Alleria");
		assertThat(itemLink.rarity()).isEqualTo(ItemRarity.EPIC);
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"|cffa335ee|Hitem:34333::::::::68::::::::|h[Coif of Alleria]|h|r",
			"|cff0070dd|Hitem:24390::::::::60::::::::|h[Auslese's Light Channeler]|h|r",

	})
	void testToString(String itemLinkString) {
		ItemLink itemLink = ItemLink.parse(itemLinkString);

		assertThat(itemLink).hasToString(itemLinkString);
	}
}