package wow.evaluator.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.equipment.ItemSockets;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
import wow.evaluator.WowEvaluatorSpringTest;
import wow.evaluator.service.ItemService;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.categorization.ItemSlotGroup.HEAD;

/**
 * User: POlszewski
 * Date: 2023-10-26
 */
class GemComboFinderTest extends WowEvaluatorSpringTest {
	@Autowired
	ItemService itemService;

	@ParameterizedTest
	@CsvSource({
			"RED,                  10, 5",
			"YELLOW,               10, 8",
			"BLUE,                 10, 3",
			"META,                 5,  5",

			"RED;RED,              55, 15",
			"RED;YELLOW,           55, 37",
			"RED;BLUE,             55, 15",
			"YELLOW;YELLOW,        55, 36",
			"YELLOW;BLUE,          55, 23",
			"BLUE;BLUE,            55, 6",

			"META;RED,             50, 25",
			"META;YELLOW,          50, 40",
			"META;BLUE,            50, 15",

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

		var finder = new GemComboFinder(character, specification, HEAD, false, itemService);

		var gemCombos = finder.getGemCombos();
		var matchingCount = gemCombos.stream().filter(sockets::matchesSockets).count();

		assertThat(gemCombos).hasSize(expectedComboCount);
		assertThat(matchingCount).isEqualTo(expectedMatchingComboCount);
	}

	@ParameterizedTest
	@CsvSource({
			"RED,                  17, 8",
			"YELLOW,               17, 14",
			"BLUE,                 17, 8",
			"META,                 6,  6",

			"RED;RED,              146, 33",
			"RED;YELLOW,           146, 100",
			"RED;BLUE,             146, 62",
			"YELLOW;YELLOW,        146, 99",
			"YELLOW;BLUE,          146, 93",
			"BLUE;BLUE,            146, 31",

			"META;RED,             102, 48",
			"META;YELLOW,          102, 84",
			"META;BLUE,            102, 48",

			"RED;RED;RED,          850, 96",
			"RED;RED;YELLOW,       850, 386",
			"RED;RED;BLUE,         850, 250",
			"RED;YELLOW;YELLOW,    850, 644",
			"RED;YELLOW;BLUE,      850, 560",
			"RED;BLUE;BLUE,        850, 234",
			"YELLOW;YELLOW;YELLOW, 850, 476",
			"YELLOW;YELLOW;BLUE,   850, 570",
			"YELLOW;BLUE;BLUE,     850, 326",
			"BLUE;BLUE;BLUE,       850, 80",
	})
	void getGemCombosWithUniqueGems(String colorStr, int expectedComboCount, int expectedMatchingComboCount) {
		var socketTypes = Stream.of(colorStr.split(";")).map(SocketType::valueOf).toList();
		var character = getCharacter();
		var specification = new ItemSocketSpecification(socketTypes, Effect.EMPTY);
		var sockets = ItemSockets.create(specification);

		var finder = new GemComboFinder(character, specification, HEAD, true, itemService);

		var gemCombos = finder.getGemCombos();
		var matchingCount = gemCombos.stream().filter(sockets::matchesSockets).count();

		assertThat(gemCombos).hasSize(expectedComboCount);
		assertThat(matchingCount).isEqualTo(expectedMatchingComboCount);
	}
}