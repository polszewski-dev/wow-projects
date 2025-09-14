package wow.simulator.log.handler;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.CooldownInstanceId;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
import wow.simulator.model.stats.AbilityTimeEntry;
import wow.simulator.model.stats.CooldownTimeEntry;
import wow.simulator.model.stats.EffectTimeEntry;
import wow.simulator.model.stats.Stats;
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
		if (currentCast != null) {
			currentCast.complete(now());
			stats.addCastTime(
					currentCast.getAbility(), currentCast.getElapsedTime(), currentCast.isFinished()
			);
		}
	}

	@Override
	public void endCast(CastSpellAction action) {
		if (action.getOwner() != player) {
			return;
		}
		currentCast.setEnd(now());
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
		stats.increaseNumHit((Ability) spell);
	}

	@Override
	public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (!(spell instanceof Ability ability)) {
			return;
		}

		if (ability.getAbilityId() == AbilityId.LIFE_TAP) {
			return;
		}

		if (target != player && type == HEALTH) {
			stats.addDamage(ability, amount, crit);
		}
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		effects.put(effect.getInstanceId(), new EffectTimeEntry(effect.getId(), now()));
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		var timeEntry = effects.remove(effect.getInstanceId());
		timeEntry.complete(now());
		stats.addEffectUptime(timeEntry.getEffectId(), timeEntry.getElapsedTime());
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		effectExpired(effect);
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		cooldowns.put(cooldown.getId(), new CooldownTimeEntry(cooldown.getCooldownId(), now()));
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		var timeEntry = cooldowns.remove(cooldown.getId());
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

		for (var timeEntry : effects.values()) {
			timeEntry.complete(now());
			stats.addEffectUptime(timeEntry.getEffectId(), timeEntry.getElapsedTime());
		}

		for (var timeEntry : cooldowns.values()) {
			timeEntry.complete(now());
			stats.addCooldownUptime(timeEntry.getCooldownId(), timeEntry.getElapsedTime());
		}
	}
}
