package wow.minmax.converter.dto.equipment;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.WowMinMaxSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ItemSourceConverterTest extends WowMinMaxSpringTest {
	@Autowired
	ItemSourceConverter itemSourceConverter;

	@Autowired
	ItemRepository itemRepository;

	@ParameterizedTest
	@CsvSource({
			"Hex Shrunken Head, ZA",
			"Carved Witch Doctor's Stick, BoJ",
			"Brutal Gladiator's Piercing Touch, PvP",
			"Loop of Forged Power, Jewelcrafting",
			"Scryer's Bloodgem, The Scryers",
			"Mantle of the Malefic, BT",
			"Elunarian Diadem, WorldDrop"
	})
	void getSources(String itemName, String expected) {
		var item = itemRepository.getItem(itemName, TBC_P5).orElseThrow();

		var converted = itemSourceConverter.getSources(item);

		assertThat(converted).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Hex Shrunken Head, ZA - Hex Lord Malacrass",
			"Carved Witch Doctor's Stick, BoJ",
			"Brutal Gladiator's Piercing Touch, PvP",
			"Loop of Forged Power, Jewelcrafting",
			"Scryer's Bloodgem, The Scryers",
			"Mantle of the Malefic, BT - Mother Shahraz",
			"Elunarian Diadem, WorldDrop"
	})
	void getDetailedSources(String itemName, String expected) {
		var item = itemRepository.getItem(itemName, TBC_P5).orElseThrow();

		var converted = itemSourceConverter.getDetailedSources(item);

		assertThat(converted).isEqualTo(expected);
	}
}