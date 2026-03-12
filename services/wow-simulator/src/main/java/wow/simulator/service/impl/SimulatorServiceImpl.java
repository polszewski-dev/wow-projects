package wow.simulator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Raid;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.Duration;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.RngType;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.rng.RealRng;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Scheduler;
import wow.simulator.script.ScriptExecutor;
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
	private final CharacterCalculationService characterCalculationService;
	private final SpellRepository spellRepository;

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, RngType rngType, List<GameLogHandler> handlers) {
		var simulationContext = createSimulationContext(rngType);

		simulate(raid, target, duration, simulationContext, handlers);
	}

	@Override
	public void simulate(Raid<Player> raid, Unit target, Duration duration, SimulationContext simulationContext, List<GameLogHandler> handlers) {
		var simulation = createSimulation(raid, target, simulationContext);

		simulation.addHandlers(handlers);

		simulation.updateFor(duration);
		simulation.finish();
	}

	private Simulation createSimulation(Raid<Player> raid, Unit target, SimulationContext simulationContext) {
		var mainPlayer = raid.getFirstMember();
		var scriptExecutor = new ScriptExecutor(mainPlayer, mainPlayer);
		var simulation = new Simulation(simulationContext);

		scriptExecutor.setupPlayer();

		target.whenNoActionIdleForever();

		simulation.add(target);

		raid.forEach(raidMember -> {
				raidMember.setTarget(target);
				simulation.add(raidMember);
				raidMember.addHiddenEffect("Bonus Hp5", 5000);
				raidMember.addHiddenEffect("Bonus Mp5", 5000);
		});

		return simulation;
	}

	private SimulationContext createSimulationContext(RngType rngType) {
		var clock = new Clock();
		var gameLog = new GameLog();
		var rngFactory = createRngFactory(rngType);
		var scheduler = new Scheduler(clock);

		return new SimulationContext(
				clock, gameLog, rngFactory, scheduler, characterCalculationService, spellRepository
		);
	}

	private RngFactory createRngFactory(RngType rngType) {
		return switch (rngType) {
			case REAL -> RealRng::new;
			case PREDETERMINED -> PredeterminedRng::new;
		};
	}
}
