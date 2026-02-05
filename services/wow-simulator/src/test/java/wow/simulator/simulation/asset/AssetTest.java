package wow.simulator.simulation.asset;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.Duration;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulatorService;
import wow.simulator.util.TestEventCollectingHandler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public abstract class AssetTest extends WowSimulatorSpringTest {
	@Autowired
	SimulatorService simulatorService;

	protected CharacterClassId assetPlayerClassId;
	protected RaceId assetPlayerRaceId;
	protected String assetPlayerTalentLink;
	protected List<String> enabledAssets;

	protected Player partyAsset;

	private static final Duration SIMULATION_DURATION = Duration.seconds(90 + 60);
	private static final Time SIMULATION_END_TIME = Time.at(SIMULATION_DURATION.getSeconds());

	@BeforeEach
	void setUp() {
		setDefaults();
		beforeSetUp();

		setupTestObjects();

		player.getBuild().setScript("warlock-shadow-bolt-spam");

		partyAsset = getNakedPlayer(assetPlayerClassId, assetPlayerRaceId, "Asset");
		partyAsset.setTarget(player.getTarget());

		if (assetPlayerTalentLink != null) {
			partyAsset.getTalents().loadFromTalentLink(assetPlayerTalentLink);
			getCharacterService().updateAfterRestrictionChange(partyAsset);
		}

		partyAsset.getAssets().setNames(enabledAssets);

		player.getParty().add(partyAsset);

		handler = new TestEventCollectingHandler();

		afterSetUp();

		simulatorService.simulate(
				player.getRaid(),
				player.getTarget(),
				SIMULATION_DURATION,
				simulationContext,
				List.of(handler)
		);
	}

	protected void setDefaults() {}

	protected void beforeSetUp() {}

	protected void afterSetUp() {}

	protected void assertSpellCast(double time, String abilityName, Unit target) {
		var event = handler.getBeginCastEvents(abilityName, partyAsset, target)
				.filter(x -> x.time().secondsSinceZero() == time)
				.findFirst();

		assertThat(event).isPresent();
	}

	protected void assertHasEffectForEntireSimulation(Unit unit, String effectName) {
		assertHasEffectForEntireSimulation(unit, effectName, 1);
	}

	protected void assertHasEffectForEntireSimulation(Unit unit, String effectName, int numStacks) {
		assertEffectApplied(unit, effectName, numStacks);
		assertEffectNotRemoved(unit, effectName);
	}

	private void assertEffectApplied(Unit unit, String effectName, int numStacks) {
		if (numStacks == 1) {
			var event = handler.getEffectAppliedEvents(effectName, unit).findFirst();

			assertThat(event).isPresent();
		} else {
			var event = handler.getEffectStackedEvents(effectName, unit)
					.filter(x -> x.numStacks() == numStacks)
					.findFirst();

			assertThat(event).isPresent();
		}
	}

	private void assertEffectNotRemoved(Unit unit, String effectName) {
		var effectExpiredEvents = handler.getEffectExpiredEvents(effectName, unit).filter(x -> x.time().before(SIMULATION_END_TIME));
		var effectRemovedEvents = handler.getEffectRemovedEvents(effectName, unit).filter(x -> x.time().before(SIMULATION_END_TIME));

		assertThat(effectExpiredEvents).isEmpty();
		assertThat(effectRemovedEvents).isEmpty();
	}

	protected void setAssetParams(CharacterClassId characterClassId, RaceId raceId, String talentLink, String... enabledAssets) {
		this.assetPlayerClassId = characterClassId;
		this.assetPlayerRaceId = raceId;
		this.assetPlayerTalentLink = talentLink;
		this.enabledAssets = List.of(enabledAssets);
	}
}
