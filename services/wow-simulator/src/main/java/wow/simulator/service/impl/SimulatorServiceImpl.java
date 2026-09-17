package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wow.character.model.asset.AssetExecution;
import wow.character.model.asset.AssetExecutionPlan;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;
import wow.commons.model.Duration;
import wow.commons.model.categorization.ItemSlot;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.service.SimulationCallback;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

import java.util.List;
import java.util.Map;

import static wow.simulator.constant.HiddenEffectNames.INFINITE_BUFFS;
import static wow.simulator.constant.HiddenEffectNames.INFINITE_RESOURCES;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Service
@AllArgsConstructor
public class SimulatorServiceImpl implements SimulatorService {
	private final AssetService assetService;

	@Value("#{${buff.items.by.slot}}")
	private final Map<ItemSlot, List<String>> buffItemsBySlot;

	private static final Time SUMMON_PHASE_START_TIME = Time.ZERO;
	private static final Time SUMMON_PHASE_END_TIME = Time.at(10);
	private static final Time BUFF_PHASE_END_TIME = Time.at(60);
	private static final Time PREPARATION_PHASE_START_TIME = SUMMON_PHASE_START_TIME;
	private static final Time PREPARATION_PHASE_END_TIME = BUFF_PHASE_END_TIME;

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, SimulationContext simulationContext, List<GameLogHandler> handlers, SimulationCallback callback) {
		var simulation = createSimulation(raid, target, simulationContext);

		simulation.addHandlers(handlers);

		executePreparationPhase(raid, simulation, callback);

		simulation.updateFor(duration);
		simulation.finish();
	}

	private Simulation createSimulation(Raid<Player> raid, Unit target, SimulationContext simulationContext) {
		var simulation = new Simulation(simulationContext);

		simulation.add(target);
		raid.forEach(simulation::add);

		return simulation;
	}

	private void executePreparationPhase(Raid<Player> raid, Simulation simulation, SimulationCallback callback) {
		simulation.runAt(PREPARATION_PHASE_START_TIME, () -> {
			callback.beforePreparationPhaseStarts();
			applyTemporaryEffects(raid);
		});

		var executionPlan = assetService.getAssetExecutionPlan(raid);

		executeSummonPhase(raid, executionPlan, simulation);
		executeBuffPhase(raid, executionPlan, simulation);

		simulation.runAt(PREPARATION_PHASE_END_TIME, () -> {
			removeTemporaryEffects(raid);
			callback.afterPreparationPhaseEnds();
			activateAllRaidMembersAndPets(raid);
		});
	}

	private void executeSummonPhase(Raid<Player> raid, AssetExecutionPlan<Player> executionPlan, Simulation simulation) {
		if (!executionPlan.hasSummonPhase()) {
			return;
		}

		var summonsByPlayer = executionPlan.summonsByPlayer();

		for (var member : raid.getMembers()) {
			var summonExecutions = summonsByPlayer.get(member);

			execute(member, summonExecutions);
		}

		simulation.updateUntil(SUMMON_PHASE_END_TIME);
	}

	private void executeBuffPhase(Raid<Player> raid, AssetExecutionPlan<Player> executionPlan, Simulation simulation) {
		var buffsByPlayer = executionPlan.buffsByPlayer();

		for (var member : raid.getMembers()) {
			var buffExecutions = buffsByPlayer.get(member);

			execute(member, buffExecutions);
			activateBuffItems(member);
		}

		simulation.updateUntil(BUFF_PHASE_END_TIME);
	}

	private void execute(Player player, List<AssetExecution<Player>> assetExecutions) {
		if (assetExecutions == null) {
			return;
		}

		var params = new ScriptParams(player);
		var executor = new AssetExecutor(params, assetExecutions);

		executor.execute();
	}

	private void activateBuffItems(Player member) {
		for (var entry : buffItemsBySlot.entrySet()) {
			var slot = entry.getKey();
			var itemNames = entry.getValue();
			var equippedItemName = member.getEquippedItemName(slot);

			if (itemNames.contains(equippedItemName)) {
				member.cast(equippedItemName);
			}
		}
	}

	private void applyTemporaryEffects(Raid<Player> raid) {
		raid.forEach(this::applyTemporaryEffects);
	}

	private void removeTemporaryEffects(Raid<Player> raid) {
		raid.forEach(this::removeTemporaryEffects);
	}

	private void applyTemporaryEffects(Unit memberOrPet) {
		memberOrPet.addHiddenEffect(INFINITE_RESOURCES, 1);
		memberOrPet.addHiddenEffect(INFINITE_BUFFS, 1);
	}

	private void removeTemporaryEffects(Unit memberOrPet) {
		memberOrPet.removeEffect(INFINITE_RESOURCES);
		memberOrPet.removeEffect(INFINITE_BUFFS);
	}

	private void activateAllRaidMembersAndPets(Raid<Player> raid) {
		raid.forEachMemberAndPet((Unit memberOrPet) -> memberOrPet.setActive());
	}
}
