package wow.simulator.model.context;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.util.EventConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

import java.util.List;
import java.util.Objects;

import static wow.character.util.EventConditionChecker.check;
import static wow.commons.model.effect.EffectSource.*;

/**
 * User: POlszewski
 * Date: 2024-11-15
 */
@RequiredArgsConstructor
@Setter
public class EventContext {
	private final Unit caster;
	private final Unit target;
	private final Spell spell;
	private final Context parentContext;
	private final List<EventAndEffect> eventEntries;
	private boolean damage;
	private boolean directDamage;
	private boolean heal;
	private boolean directHeal;
	private boolean critRoll;

	public void fireEvent() {
		for (var entry : eventEntries) {
			processEvent(entry);
		}
	}

	private void processEvent(EventAndEffect entry) {
		var event = entry.event();
		var effect = entry.effect();

		if (meetsAllConditions(entry) && !isOnCooldown(event) && eventRoll(event)) {
			performEventActions(event, effect);
		}
	}

	private void performEventActions(Event event, Effect effect) {
		for (var action : event.actions()) {
			performEventAction(action, event, effect);
		}
	}

	private boolean meetsAllConditions(EventAndEffect entry) {
		var event = entry.event();
		var args = getConditionArgs(entry.effectTarget());

		return check(event.condition(), args);
	}

	private boolean isOnCooldown(Event event) {
		var triggeredSpell = event.triggeredSpell();

		if (triggeredSpell == null || !triggeredSpell.hasCooldown()) {
			return false;
		}

		var cooldownId = CooldownId.of(triggeredSpell);

		return caster.isOnCooldown(cooldownId);
	}

	private boolean eventRoll(Event event) {
		return caster.getRng().eventRoll(event.chance(), event);
	}

	private EventConditionArgs getConditionArgs(Unit unit) {
		var args = unit == caster
				? EventConditionArgs.forSpell(caster, spell, target)
				: EventConditionArgs.forSpellTarget(target, spell);

		args.setHostileSpell(target != null && Unit.areHostile(caster, target));

		if (spell.hasDamagingComponent()) {
			args.setPowerType(PowerType.SPELL_DAMAGE);
		}

		if (damage) {
			args.setPowerType(PowerType.SPELL_DAMAGE);
			args.setDirect(directDamage);
			args.setPeriodic(!directDamage);
			args.setCanCrit(directDamage);
			args.setHadCrit(critRoll);
		}

		if (heal) {
			args.setPowerType(PowerType.HEALING);
			args.setDirect(directHeal);
			args.setPeriodic(!directHeal);
			args.setCanCrit(directHeal);
			args.setHadCrit(critRoll);
		}

		return args;
	}

	private void performEventAction(EventAction action, Event event, Effect effect) {
		switch (action) {
			case TRIGGER_SPELL ->
					triggerSpell(event, effect, false);
			case REMOVE ->
					((EffectInstance) effect).removeSelf();
			case ADD_STACK ->
					((EffectInstance) effect).addStack();
			case REMOVE_STACK ->
					((EffectInstance) effect).removeStack();
			case REMOVE_CHARGE ->
					((EffectInstance) effect).removeCharge();
			case REMOVE_CHARGE_AND_TRIGGER_SPELL ->
					triggerSpell(event, effect, true);
			case INCREASE_THIS_EFFECT_BY_PCT ->
					increaseThisEffect(event, (EffectInstance) effect);
			case INCREASE_COUNTERS_BY_LAST_DAMAGE_DONE ->
					increaseCountersByLastDamageDone((EffectInstance) effect);
		}
	}

	private void triggerSpell(Event event, Effect effect, boolean removeCharge) {
		var triggeredSpell = event.triggeredSpell();

		if (triggeredSpell.hasCooldown()) {
			var cooldownId = CooldownId.of(triggeredSpell);

			caster.triggerCooldown(cooldownId, triggeredSpell.getCooldown());
		}

		if (removeCharge) {
			((EffectInstance) effect).removeCharge();
		}

		var targetResolver = TargetResolver.ofTarget(caster, target);
		var resolutionContext = new SpellResolutionContext(caster, triggeredSpell, targetResolver, parentContext, null);

		resolutionContext.setSourceSpellOverride(getSourceSpellOverride(effect, triggeredSpell));
		resolutionContext.setValueParam(event.actionParameters().value());
		resolutionContext.resolveTriggeredSpell(effect);
	}

	private void increaseCountersByLastDamageDone(EffectInstance effect) {
		var lastDamageDone = parentContext.getLastDamageDone();

		effect.addCounters(lastDamageDone);
	}

	private void increaseThisEffect(Event event, EffectInstance effect) {
		var effectIncreasePct = Objects.requireNonNull(event.actionParameters().value());
		var abilityId = event.actionParameters().abilityId();

		if (abilityId != null && !effect.matches(abilityId)) {
			return;
		}

		effect.increaseEffect(effectIncreasePct);
	}

	private Spell getSourceSpellOverride(Effect effect, Spell triggeredSpell) {
		return switch (effect.getSource()) {
			case AbilitySource(var ability) -> ability;
			case TalentSource ignored -> triggeredSpell;
			case ItemSource ignored -> triggeredSpell;
			case null, default -> null;
		};
	}
}
