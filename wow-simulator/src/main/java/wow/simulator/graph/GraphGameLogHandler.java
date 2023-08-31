package wow.simulator.graph;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.UnitEffect;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;
import wow.simulator.simulation.TimeSource;

import java.awt.*;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
public class GraphGameLogHandler implements GameLogHandler, TimeAware, TimeSource {
	private final Player player;
	private final TimeGraph graph;
	private Clock clock;

	private final LaneDefinitions laneDefinitions;
	private final Lane castLane = new Lane(1, null, "Spell Casts", Color.WHITE);

	public GraphGameLogHandler(Player player, TimeGraph graph) {
		this.player = player;
		this.graph = graph;
		this.laneDefinitions = LaneDefinitions.get(player.getCharacterClassId());
	}

	@Override
	public void beginGcd(UnitAction sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		if (sourceAction.getOwner() != player) {
			return;
		}
		graph.gcdEndSegment(castLane, sourceAction.getActionId(), now());
	}

	@Override
	public void beginCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		Color color = laneDefinitions.getSpellColor(action.getAbility());
		graph.beginSegment(castLane, action.getActionId(), color, now());
	}

	@Override
	public void endCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		graph.endSegment(castLane, action.getActionId(), now());
	}

	@Override
	public void beginChannel(CastSpellAction action) {
		beginCast(action);
	}

	@Override
	public void endChannel(CastSpellAction action) {
		endCast(action);
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		// ignored
	}

	@Override
	public void spellResisted(CastSpellAction action, Unit target) {
		graph.addResistedMark(castLane, action.getActionId());
	}

	@Override
	public void increasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void decreasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void effectApplied(UnitEffect effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		Lane lane = laneDefinitions.getEffectLane(effect.getSourceAbilityId());
		graph.beginSegment(lane, effect.getId(), null, now());
	}

	@Override
	public void effectStacked(UnitEffect effect) {
		// ignored
	}

	@Override
	public void effectExpired(UnitEffect effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		Lane lane = laneDefinitions.getEffectLane(effect.getSourceAbilityId());
		graph.endSegment(lane, effect.getId(), now());
	}

	@Override
	public void effectRemoved(UnitEffect effect) {
		effectExpired(effect);
	}

	@Override
	public void cooldownStarted(Cooldown cooldown) {
		Lane lane = laneDefinitions.getCooldownLane(cooldown.getAbilityId());
		graph.beginSegment(lane, cooldown.getId(), null, now());
	}

	@Override
	public void cooldownExpired(Cooldown cooldown) {
		Lane lane = laneDefinitions.getCooldownLane(cooldown.getAbilityId());
		graph.endSegment(lane, cooldown.getId(), now());
	}

	@Override
	public void simulationStarted() {
		graph.setStartTime(now());
	}

	@Override
	public void simulationEnded() {
		graph.endUnfinishedSegments(now());
	}

	@Override
	public void setClock(Clock clock) {
		this.clock = clock;
	}

	@Override
	public Clock getClock() {
		return clock;
	}
}
