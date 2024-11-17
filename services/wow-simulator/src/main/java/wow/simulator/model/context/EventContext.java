package wow.simulator.model.context;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

import static wow.character.util.AttributeConditionChecker.check;
import static wow.commons.model.effect.component.EventType.*;

/**
 * User: POlszewski
 * Date: 2024-11-15
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventContext {
	private final Unit caster;
	private final Unit target;
	private final Spell spell;
	private boolean damage;
	private boolean directDamage;
	private boolean critRoll;

	public static void fireSpellHitEvent(Unit caster, Unit target, Spell spell) {
		var context = new EventContext(caster, target, spell);

		context.fireEvent(SPELL_HIT);
	}

	public static void fireSpellResistedEvent(Unit caster, Unit target, Spell spell) {
		var context = new EventContext(caster, target, spell);

		context.fireEvent(SPELL_RESISTED);
	}

	public static void fireSpellCastEvent(Unit caster, Unit target, Spell spell) {
		var context = new EventContext(caster, target, spell);

		context.fireEvent(SPELL_CAST);
	}

	public static void fireSpellDamageEvent(Unit caster, Unit target, Spell spell, boolean directDamage, boolean critRoll) {
		var context = new EventContext(caster, target, spell);

		context.damage = true;
		context.directDamage = directDamage;
		context.critRoll = critRoll;

		context.fireEvent(SPELL_DAMAGE);

		if (critRoll) {
			context.fireEvent(SPELL_CRIT);
		}
	}

	private void fireEvent(EventType eventType) {
		fireEvent(eventType, caster);

		if (target != null && target != caster) {
			fireEvent(eventType, target);
		}
	}

	private void fireEvent(EventType eventType, Unit unit) {
		var eventCollector = new EventCollector(eventType, unit);

		eventCollector.collectEffects();

		for (var entry : eventCollector.list) {
			processEvent(entry.event, entry.effect, unit);
		}
	}

	private void processEvent(Event event, Effect effect, Unit unit) {
		var args = getConditionArgs(unit);

		if (check(event.condition(), args)) {
			var eventRoll = caster.getRng().eventRoll(event.chance().value(), event);

			if (eventRoll) {
				for (var action : event.actions()) {
					performEventAction(action, event, effect);
				}
				//todo event.cooldown();
			}
		}
	}

	private AttributeConditionArgs getConditionArgs(Unit unit) {
		var args = unit == caster
				? AttributeConditionArgs.forSpell(caster, spell, target)
				: AttributeConditionArgs.forSpellTarget(target, spell);

		if (damage) {
			args.setPowerType(PowerType.SPELL_DAMAGE);
			args.setHostileSpell(true);
			args.setDirect(directDamage);
			args.setPeriodic(!directDamage);
			args.setCanCrit(directDamage);
			args.setHadCrit(critRoll);
		}

		return args;
	}

	private void performEventAction(EventAction action, Event event, Effect effect) {
		switch (action) {
			case TRIGGER_SPELL ->
					triggerSpell(event.triggeredSpell(), effect.getSource());
			case REMOVE ->
					((EffectInstance) effect).removeSelf();
			case ADD_STACK ->
					((EffectInstance) effect).addStack();
			case REMOVE_STACK ->
					((EffectInstance) effect).removeStack();
			case REMOVE_CHARGE ->
					((EffectInstance) effect).removeCharge();
		}
	}

	private void triggerSpell(Spell spell, EffectSource effectSource) {
		var resolutionContext = new SpellResolutionContext(caster, spell, target);

		for (var directComponent : spell.getDirectComponents()) {
			resolutionContext.directComponentAction(directComponent, null);
		}

		resolutionContext.applyEffect(effectSource);
	}

	private record EventAndEffect(Event event, Effect effect) {}

	private static class EventCollector extends AbstractEffectCollector {
		final EventType eventType;
		final List<EventAndEffect> list = new ArrayList<>();

		EventCollector(EventType eventType, Unit unit) {
			super(unit);
			this.eventType = eventType;
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			for (var event : effect.getEvents()) {
				if (event.types().contains(eventType)) {
					list.add(new EventAndEffect(event, effect));
				}
			}
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			// void
		}
	}
}
