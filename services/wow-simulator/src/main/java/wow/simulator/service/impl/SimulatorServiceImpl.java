package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.asset.AssetExecution;
import wow.character.model.asset.AssetExecutionPlan;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.Duration;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.RngType;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.rng.RealRng;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Scheduler;
import wow.simulator.script.ScriptParams;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Service
@AllArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final CharacterService characterService;
	private final CharacterCalculationService characterCalculationService;
	private final AssetService assetService;
	private final SpellRepository spellRepository;

	private static final Time SUMMON_PHASE_END_TIME = Time.at(10);
	private static final Time BUFF_PHASE_END_TIME = Time.at(60);
	private static final Duration PREP_PHASE_DURATION = BUFF_PHASE_END_TIME.subtract(Time.ZERO);

	private static final String BONUS_HP5 = "Bonus Hp5";
	private static final String BONUS_MP5 = "Bonus Mp5";
	private static final String INFINITE_RESOURCES = "Infinite Resources";
	private static final String INFINITE_BUFFS = "Infinite Buffs";

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, RngType rngType, List<GameLogHandler> handlers) {
		var simulationContext = createSimulationContext(rngType);

		simulate(raid, target, duration, simulationContext, handlers);
	}

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, SimulationContext simulationContext, List<GameLogHandler> handlers) {
		var simulation = createSimulation(raid, target, simulationContext);

		simulation.addHandlers(handlers);

		executeAssets(raid);

		simulation.updateFor(PREP_PHASE_DURATION.add(duration));
		simulation.finish();
	}

	private Simulation createSimulation(Raid<Player> raid, Unit target, SimulationContext simulationContext) {
		var simulation = new Simulation(simulationContext);

		simulation.add(target);

		target.whenNoActionIdleForever();

		raid.forEach(raidMember -> {
			simulation.add(raidMember);
			raidMember.setTarget(target);
		});

		return simulation;
	}

	private void executeAssets(Raid<Player> raid) {
		applyTemporaryEffects(raid);

		var executionPlan = assetService.getAssetExecutionPlan(raid);

		for (var member : raid.getMembers()) {
			executeSummonPhase(
					member,
					executionPlan,
					() -> {
						if (executionPlan.hasSummonPhase()) {
							member.idleUntil(SUMMON_PHASE_END_TIME);

							var activePet = member.getActivePet();

							if (activePet != null) {
								activePet.idleUntil(BUFF_PHASE_END_TIME);
								activePet.immediateAction(this::finalizeBuffStage);
							}
						}

						executeBuffPhase(
								member,
								executionPlan,
								() -> {
									member.idleUntil(BUFF_PHASE_END_TIME);
									member.immediateAction(this::finalizeBuffStage);
								}
						);
					}
			);
		}
	}

	private void executeSummonPhase(Player player, AssetExecutionPlan<Player> executionPlan, Runnable endStep) {
		if (!executionPlan.hasSummonPhase()) {
			endStep.run();
			return;
		}

		var summonsByPlayer = executionPlan.summonsByPlayer();
		var summonExecutions = summonsByPlayer.get(player);

		execute(player, summonExecutions, endStep);
	}

	private void executeBuffPhase(Player player, AssetExecutionPlan<Player> executionPlan, Runnable endStep) {
		var buffsByPlayer = executionPlan.buffsByPlayer();
		var buffExecutions = buffsByPlayer.get(player);

		execute(player, buffExecutions, endStep);
	}

	private void execute(Player player, List<AssetExecution<Player>> summonExecutions, Runnable endStep) {
		if (summonExecutions == null) {
			endStep.run();
		} else {
			var params = new ScriptParams(player, null);
			var executor = new AssetExecutor(params, summonExecutions, endStep);

			executor.execute();
		}
	}

	private void applyTemporaryEffects(Raid<Player> raid) {
		raid.forEachMemberAndPet((Unit memberOrPet) -> {
			memberOrPet.addHiddenEffect(INFINITE_RESOURCES, 1);
			memberOrPet.addHiddenEffect(INFINITE_BUFFS, 1);
		});
	}

	private void finalizeBuffStage(Unit memberOrPet) {
		memberOrPet.removeEffect(INFINITE_RESOURCES);
		memberOrPet.removeEffect(INFINITE_BUFFS);

		memberOrPet.addHiddenEffect(BONUS_HP5, 5000);
		memberOrPet.addHiddenEffect(BONUS_MP5, 5000);

		memberOrPet.setHealthToMax();
		memberOrPet.setManaToMax();

		if (memberOrPet.getScript() != null) {
			memberOrPet.setupScript(null);
		} else {
			memberOrPet.whenNoActionIdleForever();
		}
	}

	private SimulationContext createSimulationContext(RngType rngType) {
		var clock = new Clock();
		var gameLog = new GameLog();
		var rngFactory = createRngFactory(rngType);
		var scheduler = new Scheduler(clock);

		return new SimulationContext(
				clock, gameLog, rngFactory, scheduler, characterService, characterCalculationService, spellRepository
		);
	}

	private RngFactory createRngFactory(RngType rngType) {
		return switch (rngType) {
			case REAL -> RealRng::new;
			case PREDETERMINED -> PredeterminedRng::new;
		};
	}
}
