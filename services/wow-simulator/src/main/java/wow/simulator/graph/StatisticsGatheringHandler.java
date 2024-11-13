package wow.simulator.graph;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ResourceType;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.ActionId;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.CooldownInstanceId;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
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

import static wow.commons.model.spell.ResourceType.HEALTH;

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
	private Map<EffectInstanceId, TimeEntry> effects = new LinkedHashMap<>();
	private Map<CooldownInstanceId, TimeEntry> cooldowns = new LinkedHashMap<>();

	public StatisticsGatheringHandler(Player player, Statistics statistics) {
		this.player = player;
		this.statistics = statistics;
		this.laneDefinitions = LaneDefinitions.get(player.getCharacterClassId());
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

		casts.put(action.getActionId(), new TimeEntry(action.getAbility().getAbilityId(), now()));
	}

	@Override
	public void endCast(CastSpellAction action) {
		endCast(action, timeEntry -> timeEntry.setEnd(now()));
	}

	@Override
	public void beginChannel(CastSpellAction action) {
		beginCast(action);
	}

	@Override
	public void endChannel(CastSpellAction action) {
		endCast(action);
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
	public void spellResisted(CastSpellAction action, Unit target) {
		// ignored
	}

	@Override
	public void increasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		// ignored
	}

	@Override
	public void decreasedResource(ResourceType type, Ability ability, Unit target, int amount, int current, int previous, boolean crit) {
		if (ability.getAbilityId() == AbilityId.LIFE_TAP) {
			return;
		}
		if (target != player && type == HEALTH) {
			statistics.addDamage(ability.getAbilityId(), amount);
		}
	}

	@Override
	public void effectApplied(EffectInstance effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		effects.put(effect.getId(), new TimeEntry(effect.getSourceAbilityId(), now()));
	}

	@Override
	public void effectStacked(EffectInstance effect) {
		// ignored
	}

	@Override
	public void effectExpired(EffectInstance effect) {
		if (laneDefinitions.isIgnored(effect)) {
			return;
		}
		TimeEntry timeEntry = effects.remove(effect.getId());
		timeEntry.complete(now());
		statistics.addEffectUptime(timeEntry.getSpell(), timeEntry.getElapsedTime());
	}

	@Override
	public void effectRemoved(EffectInstance effect) {
		effectExpired(effect);
	}

	@Override
	public void cooldownStarted(CooldownInstance cooldown) {
		cooldowns.put(cooldown.getId(), new TimeEntry(cooldown.getAbilityId(), now()));
	}

	@Override
	public void cooldownExpired(CooldownInstance cooldown) {
		TimeEntry timeEntry = cooldowns.remove(cooldown.getId());
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
