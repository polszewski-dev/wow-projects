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

/**
 * User: POlszewski
 * Date: 2025-02-13
 */
class EffectScopeTest extends WowSimulatorSpringTest {
	@ParameterizedTest(name = "[{index}] {0}.{1} & {2}.{3} => {4}")
	@CsvSource({
			"Player, Curse of Agony,        Player,        Curse of Agony,        FIRST_REMOVED", // the same abilities
			"Player, Curse of Agony,        Player,        Curse of Doom,         FIRST_REMOVED", // personal -> personal
			"Player, Curse of Agony,        Player,        Curse of the Elements, FIRST_REMOVED", // personal -> global
			"Player, Curse of the Elements, Player,        Curse of Agony,        FIRST_REMOVED", // global -> personal
			"Player, Curse of Weakness,     Player,        Curse of the Elements, FIRST_REMOVED", // global -> global

			"Player, Curse of Agony,        AnotherPlayer, Curse of Doom,         NONE_REMOVED", // personal -> personal
			"Player, Curse of Agony,        AnotherPlayer, Curse of the Elements, NONE_REMOVED", // personal -> global
			"Player, Curse of the Elements, AnotherPlayer, Curse of Agony,        NONE_REMOVED", // global -> personal
			"Player, Curse of Weakness,     AnotherPlayer, Curse of the Elements, NONE_REMOVED", // global -> global

			"Player, Curse of Agony,        AnotherPlayer, Curse of Agony,        NONE_REMOVED", // the same abilities (personal -> personal)
			"Player, Curse of the Elements, AnotherPlayer, Curse of the Elements, FIRST_REMOVED", // the same abilities (global -> global)
	})
	void test(String firstPlayerName, AbilityId firstAbilityId, String secondPlayerName, AbilityId secondAbilityId, ExpectedResult expectedResult) {
		var firstPlayer = getPlayer(firstPlayerName);
		var secondPlayer = getPlayer(secondPlayerName);

		firstPlayer.cast(firstAbilityId);
		secondPlayer.cast(secondAbilityId);

		updateUntil(600);

		switch (expectedResult) {
			case FIRST_REMOVED ->

				assertThat(effectEvents).isEqualTo(List.of(
						new Removed(firstPlayerName, firstAbilityId),
						new Expired(secondPlayerName, secondAbilityId)
				));

			case NONE_REMOVED ->

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
