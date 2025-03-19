package wow.evaluator.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActivatedAbility;
import wow.evaluator.model.*;
import wow.evaluator.repository.ProcInfoRepository;

import static wow.character.util.AttributeConditionChecker.check;
import static wow.commons.model.attribute.AttributeId.CRIT_COEFF_PCT;
import static wow.commons.model.effect.component.EventType.*;

/**
 * User: POlszewski
 * Date: 2023-11-20
 */
@Component
@AllArgsConstructor
public class SpecialAbilitySolver {
	private final ProcInfoRepository procInfoRepository;

	public boolean solveAbilities(Snapshot snapshot, AccumulatedDamagingAbilityStats abilityStats, AccumulatedRotationStats rotationStats, Player player) {
		boolean recalculate = false;

		for (int i = 0; i < rotationStats.getNonModifierEffectCount(); ++i) {
			var effect = rotationStats.getNonModifierEffect(i);
			var stackCount = rotationStats.getNonModifierStackCount(i);

			recalculate |= solveNonModifierEffect(effect, stackCount, snapshot, abilityStats, player);
		}

		for (var activatedAbility : rotationStats.getActivatedAbilities()) {
			recalculate |= solveActivatedAbility(activatedAbility, abilityStats);
		}

		return recalculate;
	}

	private boolean solveNonModifierEffect(Effect effect, int stackCount, Snapshot snapshot, AccumulatedDamagingAbilityStats abilityStats, Player player) {
		return switch (effect.getEffectId()) {
			case -17793 -> solveImprovedShadowBoltProc(1, snapshot, abilityStats);
			case -17796 -> solveImprovedShadowBoltProc(2, snapshot, abilityStats);
			case -17801 -> solveImprovedShadowBoltProc(3, snapshot, abilityStats);
			case -17802 -> solveImprovedShadowBoltProc(4, snapshot, abilityStats);
			case -17803 -> solveImprovedShadowBoltProc(5, snapshot, abilityStats);
			default -> solveProc(effect, snapshot, abilityStats, player);
		};
	}

	private boolean solveProc(Effect effect, Snapshot snapshot, AccumulatedDamagingAbilityStats abilityStats, Player player) {
		var procEvent = getProcEvent(effect, snapshot.getAbility(), player);

		if (procEvent == null) {
			return false;
		}

		var triggeredSpell = procEvent.triggeredSpell();

		if (triggeredSpell == null) {
			return false;
		}

		var effectApplication = triggeredSpell.getEffectApplication();

		if (effectApplication == null) {
			return false;
		}

		var appliedEffect = effectApplication.effect();

		if (!appliedEffect.hasModifierComponent()) {
			return false;
		}

		var modifierAttributeList = appliedEffect.getModifierAttributeList();
		var procChance = getProcChance(procEvent, snapshot);

		if (procChance == 0) {
			return false;
		}

		var uptime = getProcUptime(procEvent, snapshot, procChance);

		abilityStats.accumulateAttributes(modifierAttributeList, appliedEffect.getMaxStacks() * uptime);

		return true;
	}

	private Event getProcEvent(Effect effect, Ability ability, Player player) {
		var events = effect.getEvents().stream().filter(x -> hasDamagingSpellEvent(x) && isConditionMet(x, ability, player)).toList();

		return switch (events.size()) {
			case 0 -> null;
			case 1 -> events.getFirst();
			default -> throw new IllegalArgumentException(effect.getName());
		};
	}

	private boolean isConditionMet(Event event, Ability ability, Player player) {
		if (event.condition().isEmpty()) {
			return true;
		}
		var args = AttributeConditionArgs.forSpell(player, ability, null);
		return check(event.condition(), args);
	}

	private boolean hasDamagingSpellEvent(Event event) {
		return event.types().stream().anyMatch(this::isDamagingSpellEvent);
	}

	private boolean isDamagingSpellEvent(EventType eventType) {
		return eventType == SPELL_CAST ||
				eventType == SPELL_HIT ||
				eventType == SPELL_CRIT ||
				eventType == SPELL_DAMAGE ||
				eventType == SPELL_RESISTED;
	}

	private boolean solveActivatedAbility(ActivatedAbility activatedAbility, AccumulatedDamagingAbilityStats abilityStats) {
		var effectApplication = activatedAbility.getEffectApplication();

		if (effectApplication == null) {
			return false;
		}

		var modifierAttributeList = effectApplication.effect().getModifierAttributeList();

		if (modifierAttributeList == null) {
			return false;
		}

		var duration = effectApplication.duration().getSeconds();
		var cooldown = activatedAbility.getCooldown().getSeconds();
		var uptime = duration / cooldown;

		abilityStats.accumulateAttributes(modifierAttributeList, effectApplication.numStacks() * uptime);

		return true;
	}

	private boolean solveImprovedShadowBoltProc(int rank, Snapshot snapshot, AccumulatedDamagingAbilityStats abilityStats) {
		if (snapshot.getAbility().getAbilityId() != AbilityId.SHADOW_BOLT) {
			return false;
		}

		var critPct = snapshot.getCritPct();
		var extraCritCoeff = getExtraCritCoeff(rank, critPct);

		if (extraCritCoeff == 0) {
			return false;
		}

		abilityStats.accumulateAttribute(CRIT_COEFF_PCT, extraCritCoeff, AttributeCondition.EMPTY);

		return true;
	}

	private double getExtraCritCoeff(int rank, double critPct) {
		var c = critPct / 100;
		var n = 1 - c;
		return rank * 0.04 * (2 * c + n) * (1 + n + n * n + n * n * n);
	}

	private double getProcChance(Event procEvent, Snapshot snapshot) {
		if (procEvent.types().size() != 1) {
			throw new IllegalArgumentException();
		}

		var eventChance = getEventChance(procEvent, snapshot);
		var triggerChance = procEvent.chance().value() / 100;

		return eventChance * triggerChance;
	}

	private double getEventChance(Event procEvent, Snapshot snapshot) {
		var hitChance = snapshot.getHitPct() / 100;
		var critChance = snapshot.getCritPct() / 100;

		return switch (procEvent.types().get(0)) {
			case SPELL_CAST -> 1;
			case SPELL_HIT -> hitChance;
			case SPELL_CRIT -> critChance;
			case SPELL_DAMAGE -> hitChance;
			case SPELL_RESISTED -> 1 - hitChance;
			default -> 0;
		};
	}

	private double getProcUptime(Event procEvent, Snapshot snapshot, double procChance) {
		var duration = procEvent.triggeredSpell().getEffectApplication().duration();
		var cooldown = procEvent.triggeredSpell().getCooldown().getSeconds();

		if (duration.isInfinite() && cooldown == 0) {
			return 1;
		}

		var castTime = snapshot.getEffectiveCastTime();

		return getProcUptime(procChance, duration.getSeconds(), cooldown, castTime);
	}

	private double getProcUptime(double procChance, double duration, double internalCooldown, double castTime) {
		var procChancePct = 100 * procChance;

		var p = procChancePct * ProcInfo.CHANCE_RESOLUTION;
		var t = castTime * ProcInfo.CAST_TIME_RESOLUTION;

		var modP = p % 1;
		var modT = t % 1;

		return getProcUptime((int) p, (int) t, modP, modT, (int) duration, (int) internalCooldown);
	}

	private double getProcUptime(int p, int t, double modP, double modT, int duration, int internalCooldown) {
		var procUptime0 = procInfoRepository.getAverageUptime(p, t, duration, internalCooldown);

		if (modP != 0 && modT != 0) {
			var procUptime1 = procInfoRepository.getAverageUptime(p + 1, t, duration, internalCooldown);
			var procUptime2 = procInfoRepository.getAverageUptime(p, t + 1, duration, internalCooldown);
			var procUptime3 = procInfoRepository.getAverageUptime(p + 1, t + 1, duration, internalCooldown);

			var v1 = interpolate(procUptime0, procUptime1, modP);
			var v2 = interpolate(procUptime2, procUptime3, modP);

			return interpolate(v1, v2, modT);
		}

		if (modP != 0) {
			var procUptime1 = procInfoRepository.getAverageUptime(p + 1, t, duration, internalCooldown);

			return interpolate(procUptime0, procUptime1, modP);
		}

		if (modT != 0) {
			var procUptime1 = procInfoRepository.getAverageUptime(p, t + 1, duration, internalCooldown);

			return interpolate(procUptime0, procUptime1, modT);
		}

		return procUptime0;
	}

	private static double interpolate(double v0, double v1, double t) {
		return v0 + (v1 - v0) * t;
	}
}
