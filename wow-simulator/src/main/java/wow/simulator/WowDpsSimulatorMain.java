package wow.simulator;

import lombok.Getter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.graph.GraphGameLogHandler;
import wow.simulator.graph.Statistics;
import wow.simulator.graph.StatisticsGatheringHandler;
import wow.simulator.graph.TimeGraph;
import wow.simulator.log.GameLog;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.model.rng.PredeterminedRng;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.NonPlayerImpl;
import wow.simulator.model.unit.impl.PlayerImpl;
import wow.simulator.script.warlock.WarlockPriorityScript;
import wow.simulator.simulation.Simulation;
import wow.simulator.simulation.SimulationContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	private Simulation simulation;

	private Player player;
	private Unit target;

	private TimeGraph graph;
	private Statistics statistics;

	private WowDpsSimulatorMain() {
		var context = new AnnotationConfigApplicationContext("wow.simulator");
		this.simulatorContext = context.getBean(SimulatorContext.class);
	}

	private void run() {
		createSimulation();
		createUnits();
		createGraph();

		simulation.updateUntil(Time.at(180));

		saveGraph();
	}

	private void createGraph() {
		graph = new TimeGraph();

		statistics = new Statistics();

		simulation.addHandler(new StatisticsGatheringHandler(player, statistics));
		simulation.addHandler(new GraphGameLogHandler(player, graph));
	}

	private void createSimulation() {
		Clock clock = new Clock();
		GameLog gameLog = new GameLog();
		SimulationContext simulationContext = new SimulationContext(
				clock, gameLog, PredeterminedRng::new, getCharacterCalculationService()
		);

		simulation = new Simulation(simulationContext);
		simulation.addHandler(new ConsoleGameLogHandler());
	}

	private void createUnits() {
		var aiScript = new WarlockPriorityScript(this);

		player = createPlayer();
		target = createTarget();

		player.setTarget(target);

		player.setOnPendingActionQueueEmpty(x -> aiScript.execute(player));
		target.setOnPendingActionQueueEmpty(x -> x.idleUntil(Time.INFINITY));

		aiScript.setupPlayer(player);

		simulation.add(player);
		simulation.add(target);
	}

	private Player createPlayer() {
		var character = getCharacterService().createPlayerCharacter(WARLOCK, ORC, 70, TBC_P5);
		return new PlayerImpl("Player", character);
	}

	private NonPlayer createTarget() {
		var enemy = getCharacterService().createNonPlayerCharacter(BEAST, 73, TBC_P5);
		return new NonPlayerImpl("Target", enemy);
	}

	private void saveGraph() {
		String path = "%s/graph_%s.png".formatted("../graphs", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

		graph.addSummary(player, statistics);
		graph.saveToFile(new File(path));
	}
}
