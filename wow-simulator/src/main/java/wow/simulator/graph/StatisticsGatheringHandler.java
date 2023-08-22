package wow.simulator.graph;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.cooldown.CooldownId;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.effect.EffectId;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;
import wow.simulator.simulation.TimeSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static wow.commons.model.spells.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@Getter
@Setter
public class StatisticsGatheringHandler implements GameLogHandler, TimeAware, TimeSource {
	private final Player player;
	private final Statistics statistics;
	private Clock clock;
	private Time simulationStart;
	private final LaneDefinitions laneDefinitions;

	private Map<ActionId, TimeEntry> casts = new LinkedHashMap<>();
	private Map<EffectId, TimeEntry> effects = new LinkedHashMap<>();
	private Map<CooldownId, TimeEntry> cooldowns = new LinkedHashMap<>();

	public StatisticsGatheringHandler(Player player, Statistics statistics) {
		this.player = player;
		this.statistics = statistics;
		this.laneDefinitions = LaneDefinitions.get(player.getCharacter().getCharacterClassId());
	}

	@Override
	public void beginGcd(UnitAction sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		endCast(sourceAction, timeEntry -> timeEntry.setGcdEnd(now()));
	}

	@Override
	public void beginCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}

		casts.put(action.getActionId(), new TimeEntry(action.getSpell().getSpellId(), now()));
	}

	@Override
	public void endCast(CastSpellAction action) {
		endCast(action, timeEntry -> timeEntry.setEnd(now()));
	}

	private void endCast(UnitAction action, Consumer<TimeEntry> consumer) {
		if (action.getOwner() != player) {
			return;
		}

		TimeEntry timeEntry = casts.get(action.getActionId());

		consumer.accept(timeEntry);

		if (timeEntry.isComplete()) {
			casts.remove(action.getActionId());
			statistics.addCastTime(timeEntry.getSpell(), timeEntry.getElapsedTime(), true);
		}
	}

	@Override
	public void canNotBeCasted(CastSpellAction action) {
		// ignored
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		endCast(action);
	}

	@Override
	public void spellResisted(CastSpellAction action) {
		// ignored
	}

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (spell.getSpellId() == SpellId.LIFE_TAP) {
			return;
		}
		if (target != player && type == HEALTH) {
			statistics.addDamage(spell.getSpellId(), amount);
		}
	}

	@Override
	public void effectApplied(Effect effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		effects.put(effect.getEffectId(), new TimeEntry(effect.getSourceSpell().getSpellId(), now()));
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
		TimeEntry timeEntry = effects.remove(effect.getEffectId());
		timeEntry.complete(now());
		statistics.addEffectUptime(timeEntry.getSpell(), timeEntry.getElapsedTime());
	}

	@Override
	public void effectRemoved(Effect effect) {
		effectExpired(effect);
	}

	@Override
	public void cooldownStarted(Cooldown cooldown) {
		cooldowns.put(cooldown.getCooldownId(), new TimeEntry(cooldown.getSpellId(), now()));
	}

	@Override
	public void cooldownExpired(Cooldown cooldown) {
		TimeEntry timeEntry = cooldowns.remove(cooldown.getCooldownId());
		timeEntry.complete(now());
		statistics.addCooldownUptime(timeEntry.getSpell(), timeEntry.getElapsedTime());
	}

	@Override
	public void simulationStarted() {
		statistics.setSimulationStart(now());
	}

	@Override
	public void simulationEnded() {
		statistics.setSimulationEnd(now());
		for (TimeEntry timeEntry : casts.values()) {
			timeEntry.complete(now());
			statistics.addCastTime(timeEntry.getSpell(), timeEntry.getElapsedTime(), false);
		}
		for (TimeEntry timeEntry : effects.values()) {
			timeEntry.complete(now());
			statistics.addEffectUptime(timeEntry.getSpell(), timeEntry.getElapsedTime());
		}
		for (TimeEntry timeEntry : cooldowns.values()) {
			timeEntry.complete(now());
			statistics.addCooldownUptime(timeEntry.getSpell(), timeEntry.getElapsedTime());
		}
	}
}
