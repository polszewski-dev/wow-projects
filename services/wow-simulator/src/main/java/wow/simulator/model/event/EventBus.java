package wow.simulator.model.event;

import wow.character.util.AbstractEffectCollector;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.EventType;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.Spell;
import wow.commons.util.CollectionUtil;
import wow.simulator.log.GameLog;
import wow.simulator.model.context.Context;
import wow.simulator.model.context.EventAndEffect;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.model.effect.component.EventType.*;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2026-09-23
 */
public class EventBus {
	private final Unit owner;

	public EventBus(Unit owner) {
		this.owner = owner;
	}

	public void spellCast(Ability ability, Unit target, Context parentContext) {
		fireSpellCastEvent(target, ability, parentContext);
	}

	public void spellHit(Spell spell, Unit target, Context parentContext) {
		getGameLog().spellHit(owner, target, spell);

		fireSpellHitEvent(target, spell, parentContext);
	}

	public void spellResisted(Spell spell, Unit target, Context parentContext) {
		getGameLog().spellResisted(owner, target, spell);

		fireSpellResistedEvent(target, spell, parentContext);
	}

	public void costPaid(Ability ability, Cost cost, Context parentContext) {
		var actualAmount = cost.amount();

		if (actualAmount > 0) {
			var type = cost.resourceType();

			getGameLog().decreasedResource(type, ability, owner, actualAmount, true, false, owner);
		}
	}

	public void spellHeal(Spell spell, Unit target, boolean direct, boolean crit, Context parentContext) {
		fireSpellHealEvent(target, spell, direct, crit, parentContext);

		if (crit) {
			fireSpellHealCritEvent(target, spell, direct, parentContext);
		}
	}

	public void spellDamage(Spell spell, Unit target, boolean direct, boolean crit, Context parentContext) {
		fireSpellDamageEvent(target, spell, direct, crit, parentContext);

		if (crit) {
			fireSpellDamageCritEvent(target, spell, direct, parentContext);
		}
	}

	public void spellManaGain(Spell spell, Unit target, Context parentContext) {
		fireManaGainedEvent(target, spell, parentContext);
	}

	public void spellManaLoss(Spell spell, Unit target, Context parentContext) {
		fireManaLostEvent(target, spell, parentContext);
	}

	public void healthIncreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().increasedResource(HEALTH, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			caster.getEventBus().spellHeal(spell, owner, direct, crit, parentContext);
		}
	}

	public void healthDecreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().decreasedResource(HEALTH, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			caster.getEventBus().spellDamage(spell, owner, direct, crit, parentContext);
		}
	}

	public void manaIncreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().increasedResource(MANA, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			caster.getEventBus().spellManaGain(spell, owner, parentContext);
		}
	}

	public void manaDecreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().decreasedResource(MANA, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			caster.getEventBus().spellManaLoss(spell, owner, parentContext);
		}
	}

	public void targetDied(Spell spell, Unit target, Context parentContext) {
		getGameLog().targetDied(target, owner);

		fireTargetDied(target, spell, parentContext);
	}

	public void petSummoned(Unit master, Pet pet) {
		getGameLog().petSummoned(master, pet);
	}

	public void petDied(Pet pet, Spell spell, Context parentContext) {
		firePetGone(pet, spell, parentContext);
	}

	public void petDismissed(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petDismissed(owner, pet);

		firePetGone(pet, spell, parentContext);
	}

	public void petUnsummoned(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petUnsummoned(owner, pet);

		firePetGone(pet, spell, parentContext);
	}

	public void petSacrificed(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petSacrificed(owner, pet);

		firePetGone(pet, spell, parentContext);
	}

	public void effectEnded(EffectInstance effectInstance, Context parentContext) {
		fireEffectEnded(effectInstance, parentContext);
	}

	public void effectStacksMaxed(EffectInstance effectInstance, Context parentContext) {
		fireStacksMaxed(effectInstance, parentContext);
	}

	public void effectCountersMaxed(EffectInstance effectInstance, Context parentContext) {
		fireCountersMaxed(effectInstance, parentContext);
	}

	private GameLog getGameLog() {
		return owner.getGameLog();
	}

	private Spell getSourceSpell(Context parentContext, Spell spell) {
		if (parentContext != null) {
			return parentContext.getSourceSpell();
		}
		return spell;
	}

	private void fireSpellHitEvent(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(SPELL_HIT, target, spell, parentContext);

		context.fireEvent();
	}

	private void fireSpellResistedEvent(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(SPELL_RESISTED, target, spell, parentContext);

		context.fireEvent();
	}

	private void fireSpellCastEvent(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(SPELL_CAST, target, spell, parentContext);

		context.fireEvent();
	}

	private void fireSpellDamageEvent(Unit target, Spell spell, boolean directDamage, boolean critRoll, Context parentContext) {
		var context = getEventContext(SPELL_DAMAGE, target, spell, parentContext);

		context.setDamage(true);
		context.setDirectDamage(directDamage);
		context.setCritRoll(critRoll);

		context.fireEvent();
	}

	private void fireSpellDamageCritEvent(Unit target, Spell spell, boolean directDamage, Context parentContext) {
		var context = getEventContext(SPELL_CRIT, target, spell, parentContext);

		context.setDamage(true);
		context.setDirectDamage(directDamage);
		context.setCritRoll(true);

		context.fireEvent();
	}

	private void fireSpellHealEvent(Unit target, Spell spell, boolean directHeal, boolean critRoll, Context parentContext) {
		var context = getEventContext(SPELL_HEAL, target, spell, parentContext);

		context.setHeal(true);
		context.setDirectHeal(directHeal);
		context.setCritRoll(critRoll);

		context.fireEvent();
	}

	private void fireSpellHealCritEvent(Unit target, Spell spell, boolean directHeal, Context parentContext) {
		var context = getEventContext(SPELL_CRIT, target, spell, parentContext);

		context.setHeal(true);
		context.setDirectHeal(directHeal);
		context.setCritRoll(true);

		context.fireEvent();
	}

	private void fireManaGainedEvent(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(MANA_GAINED, target, spell, parentContext);

		context.fireEvent();
	}

	private void fireManaLostEvent(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(MANA_DRAINED, target, spell, parentContext);

		context.fireEvent();
	}

	private void firePetGone(Pet pet, Spell spell, Context parentContext) {
		var context = getEventContext(PET_GONE, pet, spell, parentContext);

		context.fireEvent();
	}

	private void fireTargetDied(Unit target, Spell spell, Context parentContext) {
		var context = getEventContext(TARGET_DIED, target, spell, parentContext);

		context.fireEvent();
	}

	private void fireStacksMaxed(EffectInstance effect, Context parentContext) {
		var context = getEffectEventContext(STACKS_MAXED, effect, parentContext);

		context.fireEvent();
	}

	private void fireCountersMaxed(EffectInstance effect, Context parentContext) {
		var context = getEffectEventContext(COUNTERS_MAXED, effect, parentContext);

		context.fireEvent();
	}

	private void fireEffectEnded(EffectInstance effect, Context parentContext) {
		var context = getEventContext(EFFECT_ENDED, effect.getTarget(), effect.getSourceSpell(), parentContext);

		context.fireEvent();
	}

	private EventContext getEventContext(EventType eventType, Unit target, Spell spell, Context parentContext) {
		var eventEntries = getEvents(eventType, target);

		return new EventContext(owner, target, spell, parentContext, eventEntries);
	}

	private EventContext getEffectEventContext(EventType eventType, EffectInstance effect, Context parentContext) {
		var eventEntries = getEvents(eventType, effect);

		return new EventContext(owner, effect.getTarget(), effect.getSourceSpell(), parentContext, eventEntries);
	}

	public List<EventAndEffect> getEvents(EventType eventType, EffectInstance effect) {
		var collector = new EventCollector(eventType);

		collector.addEffect(effect);
		return collector.list;
	}

	public List<EventAndEffect> getEvents(EventType eventType, Unit target) {
		var ownerEvents = this.collectEvents(eventType);

		if (target != null && target != owner) {
			var targetEvents = target.getEventBus().collectEvents(eventType);

			return CollectionUtil.join(ownerEvents, targetEvents);
		}

		return ownerEvents;
	}

	private List<EventAndEffect> collectEvents(EventType eventType) {
		var collector = new EventCollector(eventType);

		collector.solveAll();

		return collector.list;
	}

	private class EventCollector extends AbstractEffectCollector.OnlyEffects {
		final EventType eventType;
		final List<EventAndEffect> list = new ArrayList<>();

		EventCollector(EventType eventType) {
			super(owner);
			this.eventType = eventType;
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (effect.hasAugmentedAbilities()) {
				return;
			}

			var events = effect.getEvents();

			for (var event : events) {
				if (event.types().contains(eventType)) {
					list.add(new EventAndEffect(event, effect, owner));
				}
			}
		}
	}
}
