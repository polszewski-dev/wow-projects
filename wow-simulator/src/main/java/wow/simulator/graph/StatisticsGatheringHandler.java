package wow.simulator.graph;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spells.ResourceType;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.cooldown.Cooldown;
import wow.simulator.model.cooldown.CooldownId;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.effect.EffectId;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
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
	public void beginGcd(Unit caster, Action sourceAction) {
		// ignored
	}

	@Override
	public void endGcd(Unit caster, Action sourceAction) {
		endCast(caster, sourceAction, timeEntry -> timeEntry.setGcdEnd(now()));
	}

	@Override
	public void beginCast(Unit caster, Spell spell, Unit target, Action action) {
		if (caster != player) {
			return;
		}

		casts.put(action.getActionId(), new TimeEntry(spell.getSpellId(), now()));
	}

	@Override
	public void endCast(Unit caster, Spell spell, Unit target, Action action) {
		endCast(caster, action, timeEntry -> timeEntry.setEnd(now()));
	}

	private void endCast(Unit caster, Action action, Consumer<TimeEntry> consumer) {
		if (caster != player) {
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
	public void canNotBeCasted(Unit caster, Spell spell, Unit target, Action action) {
		// ignored
	}

	@Override
	public void castInterrupted(Unit caster, Spell spell, Unit target, Action action) {
		endCast(caster, spell, target, action);
	}

	@Override
	public void spellResisted(Unit caster, Spell spell, Unit target, Action action) {
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
