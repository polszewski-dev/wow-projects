package wow.simulator.model.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.util.EventConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.context.Context;
import wow.simulator.model.context.EventAndEffect;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.unit.Unit;

import java.util.List;

import static wow.character.util.EventConditionChecker.check;

/**
 * User: POlszewski
 * Date: 2024-11-15
 */
@RequiredArgsConstructor
@Setter
public class EventExecutor {
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

	public void fireEvents() {
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
		var eventContext = new EventContext(event, effect, caster, target, spell, parentContext);

		eventContext.fireEvent();
	}

	private boolean meetsAllConditions(EventAndEffect entry) {
		var event = entry.event();
		var effectOwner = entry.effectOwner();
		var args = getConditionArgs(effectOwner);

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

	private EventConditionArgs getConditionArgs(Unit effectOwner) {
		var args = EventConditionArgs.forSpell(caster, spell, target);

		args.setEffectOwner(effectOwner);
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
}
