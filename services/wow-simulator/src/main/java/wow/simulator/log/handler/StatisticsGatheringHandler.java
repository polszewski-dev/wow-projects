package wow.simulator.log.handler;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.CooldownInstanceId;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
import wow.simulator.model.effect.impl.EffectInstanceImpl;
import wow.simulator.model.stats.*;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.ChannelSpellAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.TimeAware;
import wow.simulator.simulation.TimeSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static wow.commons.model.spell.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@Getter
@Setter
public class StatisticsGatheringHandler implements GameLogHandler, TimeAware, TimeSource {
	private final Player player;
	private final Stats stats;
	private Clock clock;
	private Time simulationStart;

	private AbilityTimeEntry currentCast;

	private final Map<EffectInstanceId, EffectTimeEntry> effects = new LinkedHashMap<>();
	private final Map<CooldownInstanceId, CooldownTimeEntry> cooldowns = new LinkedHashMap<>();

	public StatisticsGatheringHandler(Player player, Stats stats) {
		this.player = player;
		this.stats = stats;
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		if (sourceAction.getOwner() != player) {
			return;
		}
		currentCast.setGcdEnd(now());
	}

	@Override
	public void beginCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		finishCurrentCast();
		currentCast = new AbilityTimeEntry(action.getAbility(), now());
	}

	private void finishCurrentCast() {
		if (currentCast == null) {
			return;
		}

		currentCast.complete(now());
		stats.addCastTime(
				currentCast.getAbility(), currentCast.getElapsedTime(), currentCast.isFinished()
		);
		currentCast = null;
	}

	@Override
	public void endCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		currentCast.setEnd(now());
	}

	@Override
	public void castInterrupted(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		endCast(action);
		finishCurrentCast();
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		currentCast.setChannelBegin(now());
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		currentCast.setChannelEnd(now());
	}

	@Override
	public void spellHit(Unit caster, Unit target, Spell spell) {
		if (caster != player) {
			return;
		}
		stats.increaseNumHit((Ability) spell);
	}

	@Override
	public void spellResisted(Unit caster, Unit target, Spell spell) {
		if (caster != player) {
			return;
		}
		stats.increaseNumResisted((Ability) spell);
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (!(spell instanceof Ability ability)) {
			return;
		}

		if (target != player && type == HEALTH) {
			stats.addDamage(ability, amount, crit);
		}
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		if (effect.getOwner() != player) {
			return;
		}
		addTimeEntry(effect);
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		if (effect.getOwner() != player) {
			return;
		}
		var timeEntry = effects.remove(effect.getInstanceId());
		completeEffect(timeEntry);
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		if (effect.getOwner() != player) {
			return;
		}

		var stackedEffectInstanceId = ((EffectInstanceImpl) effect).getStackedEffectInstanceId();
		var existingTimeEntry = effects.remove(stackedEffectInstanceId);

		Objects.requireNonNull(existingTimeEntry);

		if (existingTimeEntry.getEffectId().equals(effect.getId())) {
			effects.put(effect.getInstanceId(), existingTimeEntry);
		} else {
			completeEffect(existingTimeEntry);
			addTimeEntry(effect);
		}
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		effectExpired(effect);
	}

	private void addTimeEntry(EffectInstance effect) {
		effects.put(effect.getInstanceId(), new EffectTimeEntry(effect, now()));
	}

	private void completeEffect(EffectTimeEntry timeEntry) {
		timeEntry.complete(now());
		stats.addEffectUptime(timeEntry.getEffect(), timeEntry.getElapsedTime());
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		if (cooldown.getOwner() != player || !CooldownStats.isSupported(cooldown)) {
			return;
		}
		cooldowns.put(cooldown.getId(), new CooldownTimeEntry(cooldown.getCooldownId(), now()));
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		if (cooldown.getOwner() != player || !CooldownStats.isSupported(cooldown)) {
			return;
		}
		var timeEntry = cooldowns.remove(cooldown.getId());
		completeCooldown(timeEntry);
	}

	private void completeCooldown(CooldownTimeEntry timeEntry) {
		timeEntry.complete(now());
		stats.addCooldownUptime(timeEntry.getCooldownId(), timeEntry.getElapsedTime());
	}

	@Override
	public void simulationStarted() {
		stats.setSimulationStart(now());
	}

	@Override
	public void simulationEnded() {
		stats.setSimulationEnd(now());

		finishCurrentCast();
	}
}
