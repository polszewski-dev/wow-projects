package wow.simulator.log.handler;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.CooldownInstanceId;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
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
import java.util.function.Consumer;

import static wow.commons.model.spell.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2023-08-20
 */
@Getter
@Setter
public class StatisticsGatheringHandler extends DefaultGameLogHandler implements TimeAware, TimeSource {
	private final Player player;
	private final Stats stats;
	private Clock clock;
	private Time simulationStart;

	private final Map<ActionId, AbilityTimeEntry> casts = new LinkedHashMap<>();
	private final Map<EffectInstanceId, EffectTimeEntry> effects = new LinkedHashMap<>();
	private final Map<CooldownInstanceId, CooldownTimeEntry> cooldowns = new LinkedHashMap<>();

	public StatisticsGatheringHandler(Player player, Stats stats) {
		this.player = player;
		this.stats = stats;
	}

	@Override
	public void endGcd(UnitAction sourceAction) {
		endCast(sourceAction, timeEntry -> timeEntry.setGcdEnd(now()));
	}

	@Override
	public void beginCast(CastSpellAction action) {
		beginCast(action, action.getAbility());
	}

	@Override
	public void endCast(CastSpellAction action) {
		var ability = action.getAbility();
		endCast(action, ability);
	}

	@Override
	public void beginChannel(ChannelSpellAction action) {
		beginCast(action, action.getAbility());
	}

	@Override
	public void endChannel(ChannelSpellAction action) {
		endCast(action, action.getAbility());
	}

	private void beginCast(UnitAction action, Ability ability) {
		if (action.getOwner() != player) {
			return;
		}

		casts.put(action.getActionId(), new AbilityTimeEntry(ability, now()));
	}

	private void endCast(UnitAction action, Ability ability) {
		endCast(action, timeEntry -> {
			if (ability.getCastInfo().ignoresGcd()) {
				timeEntry.complete(now());
			} else {
				timeEntry.setEnd(now());
			}
		});
	}

	private void endCast(UnitAction action, Consumer<TimeEntry> consumer) {
		if (action.getOwner() != player) {
			return;
		}

		var timeEntry = casts.get(action.getActionId());

		consumer.accept(timeEntry);

		if (timeEntry.isComplete()) {
			casts.remove(action.getActionId());
			stats.addCastTime(timeEntry.getAbility(), timeEntry.getElapsedTime(), true);
		}
	}

	@Override
	public void spellHit(CastSpellAction action, Unit target) {
		stats.increaseNumHit(action.getAbility());
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
		effects.put(effect.getId(), new EffectTimeEntry(effect.getEffectId(), now()));
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		var timeEntry = effects.remove(effect.getId());
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
		for (var timeEntry : casts.values()) {
			timeEntry.complete(now());
			stats.addCastTime(timeEntry.getAbility(), timeEntry.getElapsedTime(), false);
		}
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
