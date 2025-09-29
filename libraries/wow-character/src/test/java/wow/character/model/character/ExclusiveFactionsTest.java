package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.pve.Faction;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.test.commons.ExclusiveFactionNames.ALDOR;
import static wow.test.commons.ExclusiveFactionNames.SCRYERS;

/**
 * User: POlszewski
 * Date: 2025-09-28
 */
class ExclusiveFactionsTest extends WowCharacterSpringTest {
	@Test
	void enable() {
		exclusiveFactions.enable(ALDOR);

		assertThat(exclusiveFactions.has(ALDOR)).isTrue();
		assertThat(exclusiveFactions.has(SCRYERS)).isFalse();

		exclusiveFactions.enable(SCRYERS);

		assertThat(exclusiveFactions.has(ALDOR)).isFalse();
		assertThat(exclusiveFactions.has(SCRYERS)).isTrue();
	}

	@Test
	void set() {
		exclusiveFactions.set(List.of(ALDOR));

		assertThat(exclusiveFactions.has(ALDOR)).isTrue();
		assertThat(exclusiveFactions.has(SCRYERS)).isFalse();

		exclusiveFactions.set(List.of(SCRYERS));

		assertThat(exclusiveFactions.has(ALDOR)).isFalse();
		assertThat(exclusiveFactions.has(SCRYERS)).isTrue();
	}

	@Test
	void copy() {
		exclusiveFactions.enable(ALDOR);

		var copy = exclusiveFactions.copy();

		exclusiveFactions.enable(SCRYERS);

		assertThat(copy.has(ALDOR)).isTrue();
		assertThat(copy.has(SCRYERS)).isFalse();

		assertThat(exclusiveFactions.has(ALDOR)).isFalse();
		assertThat(exclusiveFactions.has(SCRYERS)).isTrue();
	}

	@Test
	void getAvailable() {
		var available = exclusiveFactions.getAvailable();

		var names = available
				.stream()
				.map(Faction::getName)
				.toList();

		assertThat(names).hasSameElementsAs(List.of(
				ALDOR, SCRYERS
		));
	}

	ExclusiveFactions exclusiveFactions;

	@BeforeEach
	void setup() {
		exclusiveFactions = new ExclusiveFactions(factionRepository.getAvailableExclusiveFactions(TBC));
	}
}