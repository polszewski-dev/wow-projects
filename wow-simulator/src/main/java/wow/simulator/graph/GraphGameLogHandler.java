package wow.simulator.graph;

import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.time.Clock;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
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
		this.laneDefinitions = LaneDefinitions.get(player.getCharacter().getCharacterClassId());
	}

	@Override
	public void beginGcd(Unit caster, Action sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(Unit caster, Action sourceAction) {
		if (caster != player) {
			return;
		}
		graph.gcdEndSegment(castLane, sourceAction.getActionId(), now());
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, Action action) {
		if (caster != player) {
			return;
		}
		Color color = laneDefinitions.getSpellColor(spell);
		graph.beginSegment(castLane, action.getActionId(), color, now());
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, Action action) {
		if (caster != player) {
			return;
		}
		graph.endSegment(castLane, action.getActionId(), now());
	}

	@Override
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action) {
		// ignored
	}

	@Override
	public void castInterrupted(Unit caster, Spell spell, Unit target, Action action) {
		endCast(caster, spell, target, action);
	}

	@Override
	public void spellResisted(Unit caster, Spell spell, Unit target, Action action) {
		graph.addResistedMark(castLane, action.getActionId());
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void effectApplied(Effect effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		Lane lane = laneDefinitions.getEffectLane(effect.getSourceSpell().getSpellId());
		graph.beginSegment(lane, effect.getEffectId(), null, now());
	}

	@Override
	public void effectStacked(Effect effect) {
		// ignored
	}

	@Override
	public void effectExpired(Effect effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		Lane lane = laneDefinitions.getEffectLane(effect.getSourceSpell().getSpellId());
		graph.endSegment(lane, effect.getEffectId(), now());
	}

	@Override
	public void effectRemoved(Effect effect) {
		effectExpired(effect);
	}

	@Override
	public void cooldownStarted(Cooldown cooldown) {
		Lane lane = laneDefinitions.getCooldownLane(cooldown.getSpellId());
		graph.beginSegment(lane, cooldown.getCooldownId(), null, now());
	}

	@Override
	public void cooldownExpired(Cooldown cooldown) {
		Lane lane = laneDefinitions.getCooldownLane(cooldown.getSpellId());
		graph.endSegment(lane, cooldown.getCooldownId(), now());
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
