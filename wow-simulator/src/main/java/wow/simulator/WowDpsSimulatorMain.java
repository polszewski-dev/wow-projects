package wow.simulator;

import lombok.Getter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Target;
import wow.simulator.scripts.AIScript;
import wow.simulator.scripts.warlock.WarlockPriorityScript;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.BEAST;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class WowDpsSimulatorMain implements SimulatorContextSource {
	public static void main(String[] args) {
		new WowDpsSimulatorMain().run();
	}

	private final SimulatorContext simulatorContext;

	private WowDpsSimulatorMain() {
		var context = new AnnotationConfigApplicationContext("wow.simulator");
		this.simulatorContext = context.getBean(SimulatorContext.class);
	}

	private void run() {
		AIScript aiScript = new WarlockPriorityScript(this);

		Character character = getCharacterService().createCharacter(WARLOCK, ORC, 70, TBC_P5);
		Enemy enemy = new Enemy(BEAST, 3);

		Player player = new Player("Player", character);
		Target target = new Target("Target", enemy);

		character.setTargetEnemy(enemy);
		player.setTarget(target);

		player.setOnPendingActionQueueEmpty(x -> aiScript.execute(player));
		target.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));

		aiScript.setupPlayer(player);

		Simulation simulation = getSimulation();

		simulation.addHandler(new ConsoleGameLogHandler());

		simulation.add(player);
		simulation.add(target);

		simulation.updateUntil(Time.at(180));
	}

	private Simulation getSimulation() {
		Clock clock = new Clock();
		GameLog gameLog = new GameLog();
		SimulationContext simulationContext = new SimulationContext(
				clock, gameLog, getCharacterCalculationService()
		);
		return new Simulation(simulationContext);
	}
}
