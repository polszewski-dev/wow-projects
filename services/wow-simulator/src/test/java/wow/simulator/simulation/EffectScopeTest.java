package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Player;
import wow.simulator.util.TestEventCollectingHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.simulator.simulation.EffectScopeTest.ExpectedResult.FIRST_REMOVED;

/**
 * User: POlszewski
 * Date: 2025-02-13
 */
class EffectScopeTest extends WowSimulatorSpringTest {
	@ParameterizedTest(name = "[{index}] {0}.{1} & {2}.{3} => {4}")
	@CsvSource({
			"Player, CURSE_OF_AGONY,        Player,        CURSE_OF_AGONY,        FIRST_REMOVED", // the same abilities
			"Player, CURSE_OF_AGONY,        Player,        CURSE_OF_DOOM,         FIRST_REMOVED", // personal -> personal
			"Player, CURSE_OF_AGONY,        Player,        CURSE_OF_THE_ELEMENTS, FIRST_REMOVED", // personal -> global
			"Player, CURSE_OF_THE_ELEMENTS, Player,        CURSE_OF_AGONY,        FIRST_REMOVED", // global -> personal
			"Player, CURSE_OF_WEAKNESS,     Player,        CURSE_OF_THE_ELEMENTS, FIRST_REMOVED", // global -> global

			"Player, CURSE_OF_AGONY,        AnotherPlayer, CURSE_OF_DOOM,         NONE_REMOVED", // personal -> personal
			"Player, CURSE_OF_AGONY,        AnotherPlayer, CURSE_OF_THE_ELEMENTS, NONE_REMOVED", // personal -> global
			"Player, CURSE_OF_THE_ELEMENTS, AnotherPlayer, CURSE_OF_AGONY,        NONE_REMOVED", // global -> personal
			"Player, CURSE_OF_WEAKNESS,     AnotherPlayer, CURSE_OF_THE_ELEMENTS, NONE_REMOVED", // global -> global

			"Player, CURSE_OF_AGONY,        AnotherPlayer, CURSE_OF_AGONY,        NONE_REMOVED", // the same abilities (personal -> personal)
			"Player, CURSE_OF_THE_ELEMENTS, AnotherPlayer, CURSE_OF_THE_ELEMENTS, FIRST_REMOVED", // the same abilities (global -> global)
	})
	void test(String firstPlayerName, AbilityId firstAbilityId, String secondPlayerName, AbilityId secondAbilityId, ExpectedResult expectedResult) {
		var firstPlayer = getPlayer(firstPlayerName);
		var secondPlayer = getPlayer(secondPlayerName);

		firstPlayer.cast(firstAbilityId);
		secondPlayer.cast(secondAbilityId);

		updateUntil(600);

		if (expectedResult == FIRST_REMOVED) {
			assertThat(effectEvents).isEqualTo(List.of(
				new Removed(firstPlayerName, firstAbilityId),
				new Expired(secondPlayerName, secondAbilityId)
			));
		} else {
			// effects can expire at different time
			assertThat(effectEvents).hasSameElementsAs(List.of(
					new Expired(firstPlayerName, firstAbilityId),
					new Expired(secondPlayerName, secondAbilityId)
			));
		}
	}

	private Player getPlayer(String name) {
		return Stream.of(player, anotherPlayer)
				.filter(x -> x.getName().equals(name))
				.findAny()
				.orElseThrow();
	}

	enum ExpectedResult {
		FIRST_REMOVED,
		NONE_REMOVED
	}

	Player anotherPlayer;

	@BeforeEach
	void setUp() {
		characterClassId = WARLOCK;

		setupTestObjects();

		handler = new TestEventCollectingHandler();
		simulation.addHandler(handler);

		anotherPlayer = getNakedPlayer(characterClassId, "AnotherPlayer");
		anotherPlayer.setTarget(target);

		simulation.add(player);
		simulation.add(target);
		simulation.add(anotherPlayer);

		simulation.addHandler(new EffectHandler());
	}

	interface EffectEvent {}

	record Expired(String ownerName, AbilityId abilityId) implements EffectEvent {}

	record Removed(String ownerName, AbilityId abilityId) implements EffectEvent {}

	List<EffectEvent> effectEvents = new ArrayList<>();

	class EffectHandler implements GameLogHandler {
		@Override
		public void effectExpired(EffectInstance effect) {
			effectEvents.add(new Expired(getName(effect), getAbilityId(effect)));
		}

		@Override
		public void effectRemoved(EffectInstance effect) {
			effectEvents.add(new Removed(getName(effect), getAbilityId(effect)));
		}

		static AbilityId getAbilityId(EffectInstance effect) {
			return ((Ability) effect.getSourceSpell()).getAbilityId();
		}

		static String getName(EffectInstance effect) {
			return effect.getOwner().getName();
		}
	}
}
