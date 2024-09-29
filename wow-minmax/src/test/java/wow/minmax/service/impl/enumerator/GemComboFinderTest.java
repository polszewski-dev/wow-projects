package wow.minmax.service.impl.enumerator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.service.ItemService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-10-26
 */
class GemComboFinderTest extends WowMinMaxSpringTest {
	@Autowired
	ItemService itemService;

	@ParameterizedTest
	@CsvSource({
			"RED,                  10, 5",
			"YELLOW,               10, 8",
			"BLUE,                 10, 3",
			"META,                 4,  4",

			"RED;RED,              55, 15",
			"RED;YELLOW,           55, 37",
			"RED;BLUE,             55, 15",
			"YELLOW;YELLOW,        55, 36",
			"YELLOW;BLUE,          55, 23",
			"BLUE;BLUE,            55, 6",

			"META;RED,             40, 20",
			"META;YELLOW,          40, 32",
			"META;BLUE,            40, 12",

			"RED;RED;RED,          220, 35",
			"RED;RED;YELLOW,       220, 106",
			"RED;RED;BLUE,         220, 45",
			"RED;YELLOW;YELLOW,    220, 157",
			"RED;YELLOW;BLUE,      220, 100",
			"RED;BLUE;BLUE,        220, 30",
			"YELLOW;YELLOW;YELLOW, 220, 120",
			"YELLOW;YELLOW;BLUE,   220, 100",
			"YELLOW;BLUE;BLUE,     220, 45",
			"BLUE;BLUE;BLUE,       220, 10",
	})
	void getGemCombos(String colorStr, int expectedComboCount, int expectedMatchingComboCount) {
		var socketTypes = Stream.of(colorStr.split(";")).map(SocketType::valueOf).toList();
		var character = getCharacter();
		var specification = new ItemSocketSpecification(socketTypes, Effect.EMPTY);
		var sockets = ItemSockets.create(specification);

		var finder = new GemComboFinder(character, specification, itemService);

		var gemCombos = finder.getGemCombos();
		var matchingCount = gemCombos.stream().filter(sockets::matchesSockets).count();

		assertThat(gemCombos).hasSize(expectedComboCount);
		assertThat(matchingCount).isEqualTo(expectedMatchingComboCount);
	}
}