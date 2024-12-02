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
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.EventCooldownId;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
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

	public static void fireStacksMaxed(EffectInstance effect) {
		var context = new EventContext(effect.getOwner(), effect.getTarget(), effect.getSourceSpell());

		for (var event : effect.getEvents()) {
			if (event.types().contains(STACKS_MAXED)) {
				var entry = new EventAndEffect(0, event, effect, effect.getTarget());
				context.performEventActions(entry, event, effect);
			}
		}
	}

	private void fireEvent(EventType eventType) {
		var eventEntries = collectEvents(eventType);

		for (var entry : eventEntries) {
			processEvent(entry);
		}
	}

	private List<EventAndEffect> collectEvents(EventType eventType) {
		var list = new ArrayList<EventAndEffect>();

		new EventCollector(eventType, caster, list).collectEffects();

		if (target != null && target != caster) {
			new EventCollector(eventType, target, list).collectEffects();
		}

		return list;
	}

	private void processEvent(EventAndEffect entry) {
		var event = entry.event();
		var effect = entry.effect();

		if (meetsAllConditions(entry)) {
			performEventActions(entry, event, effect);
		}
	}

	private void performEventActions(EventAndEffect entry, Event event, Effect effect) {
		if (event.hasCooldown()) {
			caster.triggerCooldown(entry.getEventCooldownId(), event.cooldown());
		}
		for (var action : event.actions()) {
			performEventAction(action, event, effect);
		}
	}

	private boolean meetsAllConditions(EventAndEffect entry) {
		var event = entry.event();
		var args = getConditionArgs(entry.effectTarget());

		if (!check(event.condition(), args)) {
			return false;
		}

		if (event.hasCooldown() && caster.isOnCooldown(entry.getEventCooldownId())) {
			return false;
		}

		return caster.getRng().eventRoll(event.chance(), event);
	}

	private AttributeConditionArgs getConditionArgs(Unit unit) {
		var args = unit == caster
				? AttributeConditionArgs.forSpell(caster, spell, target)
				: AttributeConditionArgs.forSpellTarget(target, spell);

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
		var targetResolver = TargetResolver.ofTarget(caster, target);
		var resolutionContext = new SpellResolutionContext(caster, spell, targetResolver);

		for (var directComponent : spell.getDirectComponents()) {
			resolutionContext.directComponentAction(directComponent, null);
		}

		if (spell.getEffectApplication() != null) {
			resolutionContext.applyEffect(effectSource);
		}
	}

	private record EventAndEffect(int idx, Event event, Effect effect, Unit effectTarget) {
		EventCooldownId getEventCooldownId() {
			return CooldownId.of(effect.getSource(), idx);
		}
	}

	private static class EventCollector extends AbstractEffectCollector {
		final EventType eventType;
		final Unit unit;
		final List<EventAndEffect> list;

		EventCollector(EventType eventType, Unit unit, List<EventAndEffect> list) {
			super(unit);
			this.eventType = eventType;
			this.unit = unit;
			this.list = list;
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			var events = effect.getEvents();
			for (int i = 0; i < events.size(); i++) {
				var event = events.get(i);
				if (event.types().contains(eventType)) {
					list.add(new EventAndEffect(i, event, effect, unit));
				}
			}
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			// void
		}
	}
}
