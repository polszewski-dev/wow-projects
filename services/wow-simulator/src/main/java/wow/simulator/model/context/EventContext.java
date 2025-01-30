package wow.simulator.model.context;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentSource;
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
	private final Context parentContext;
	private boolean damage;
	private boolean directDamage;
	private boolean heal;
	private boolean directHeal;
	private boolean critRoll;

	public static void fireSpellHitEvent(Unit caster, Unit target, Spell spell, Context parentContext) {
		var context = new EventContext(caster, target, spell, parentContext);

		context.fireEvent(SPELL_HIT);
	}

	public static void fireSpellResistedEvent(Unit caster, Unit target, Spell spell, Context parentContext) {
		var context = new EventContext(caster, target, spell, parentContext);

		context.fireEvent(SPELL_RESISTED);
	}

	public static void fireSpellCastEvent(Unit caster, Unit target, Spell spell, Context parentContext) {
		var context = new EventContext(caster, target, spell, parentContext);

		context.fireEvent(SPELL_CAST);
	}

	public static void fireSpellDamageEvent(Unit caster, Unit target, Spell spell, boolean directDamage, boolean critRoll, Context parentContext) {
		var context = new EventContext(caster, target, spell, parentContext);

		context.damage = true;
		context.directDamage = directDamage;
		context.critRoll = critRoll;

		context.fireEvent(SPELL_DAMAGE);

		if (critRoll) {
			context.fireEvent(SPELL_CRIT);
		}
	}

	public static void fireSpellHealEvent(Unit caster, Unit target, Spell spell, boolean directHeal, boolean critRoll, Context parentContext) {
		var context = new EventContext(caster, target, spell, parentContext);

		context.heal = true;
		context.directHeal = directHeal;
		context.critRoll = critRoll;

		context.fireEvent(SPELL_HEAL);

		if (critRoll) {
			context.fireEvent(SPELL_CRIT);
		}
	}

	public static void fireStacksMaxed(EffectInstance effect, Context parentContext) {
		var context = new EventContext(effect.getOwner(), effect.getTarget(), effect.getSourceSpell(), parentContext);

		for (var event : effect.getEvents()) {
			if (event.types().contains(STACKS_MAXED)) {
				context.performEventActions(event, effect);
			}
		}
	}

	public static void fireEffectEnded(EffectInstance effect, Context parentContext) {
		var context = new EventContext(effect.getOwner(), effect.getTarget(), effect.getSourceSpell(), parentContext);

		context.fireEvent(EFFECT_ENDED);
	}

	private void fireEvent(EventType eventType) {
		var eventEntries = collectEvents(eventType);

		for (var entry : eventEntries) {
			processEvent(entry);
		}
	}

	private List<EventAndEffect> collectEvents(EventType eventType) {
		var list = new ArrayList<EventAndEffect>();

		new EventCollector(eventType, caster, list).solveAll();

		if (target != null && target != caster) {
			new EventCollector(eventType, target, list).solveAll();
		}

		return list;
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
		var spell = event.triggeredSpell();

		if (spell == null || !spell.hasCooldown()) {
			return false;
		}

		var cooldownId = CooldownId.of(spell);

		return caster.isOnCooldown(cooldownId);
	}

	private boolean eventRoll(Event event) {
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
					triggerSpell(event.triggeredSpell(), effect, false);
			case REMOVE ->
					((EffectInstance) effect).removeSelf();
			case ADD_STACK ->
					((EffectInstance) effect).addStack();
			case REMOVE_STACK ->
					((EffectInstance) effect).removeStack();
			case REMOVE_CHARGE ->
					((EffectInstance) effect).removeCharge();
			case REMOVE_CHARGE_AND_TRIGGER_SPELL ->
					triggerSpell(event.triggeredSpell(), effect, true);
		}
	}

	private void triggerSpell(Spell spell, Effect effect, boolean removeCharge) {
		if (spell.hasCooldown()) {
			var cooldownId = CooldownId.of(spell);

			caster.triggerCooldown(cooldownId, spell.getCooldown());
		}

		if (removeCharge) {
			((EffectInstance) effect).removeCharge();
		}

		var targetResolver = TargetResolver.ofTarget(caster, target);
		var resolutionContext = new SpellResolutionContext(caster, spell, targetResolver, parentContext);

		resolutionContext.setSourceSpellOverride(getSourceSpellOverride(effect, spell));

		for (var directComponent : spell.getDirectComponents()) {
			resolutionContext.directComponentAction(directComponent, null);
		}

		if (spell.getEffectApplication() != null) {
			resolutionContext.applyEffect(effect.getSource());
		}
	}

	private Spell getSourceSpellOverride(Effect effect, Spell triggeredSpell) {
		return switch (effect.getSource()) {
			case AbilitySource s -> s.ability();
			case TalentSource ignored -> triggeredSpell;
			case ItemSource ignored -> triggeredSpell;
			default -> null;
		};
	}

	private record EventAndEffect(Event event, Effect effect, Unit effectTarget) {}

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
			for (var event : events) {
				if (event.types().contains(eventType)) {
					list.add(new EventAndEffect(event, effect, unit));
				}
			}
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			// void
		}
	}
}
