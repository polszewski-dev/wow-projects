package wow.simulator.model.event;

import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.Spell;
import wow.simulator.log.GameLog;
import wow.simulator.model.context.Context;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.Pet;
import wow.simulator.model.unit.Unit;

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
		EventContext.fireSpellCastEvent(owner, target, ability, parentContext);
	}

	public void spellHit(Spell spell, Unit target, Context parentContext) {
		getGameLog().spellHit(owner, target, spell);

		EventContext.fireSpellHitEvent(owner, target, spell, parentContext);
	}

	public void spellResisted(Spell spell, Unit target, Context parentContext) {
		getGameLog().spellResisted(owner, target, spell);

		EventContext.fireSpellResistedEvent(owner, target, spell, parentContext);
	}

	public void costPaid(Ability ability, Cost cost, Context parentContext) {
		var actualAmount = cost.amount();

		if (actualAmount > 0) {
			var type = cost.resourceType();

			getGameLog().decreasedResource(type, ability, owner, actualAmount, true, false, owner);
		}
	}

	public void healthIncreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().increasedResource(HEALTH, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			EventContext.fireSpellHealEvent(caster, owner, spell, direct, crit, parentContext);
		}
	}

	public void healthDecreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().decreasedResource(HEALTH, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		EventContext.fireSpellDamageEvent(caster, owner, spell, direct, crit, parentContext);
	}

	public void manaIncreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().increasedResource(MANA, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		if (caster != null) {
			EventContext.fireManaGainedEvent(caster, owner, spell, parentContext);
		}
	}

	public void manaDecreased(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		if (amount == 0) {
			return;
		}

		getGameLog().decreasedResource(MANA, getSourceSpell(parentContext, spell), owner, amount, direct, crit, caster);

		EventContext.fireManaLostEvent(caster, owner, spell, parentContext);
	}

	public void targetDied(Spell spell, Unit target, Context parentContext) {
		getGameLog().targetDied(target, owner);

		EventContext.fireTargetDied(owner, target, spell, parentContext);
	}

	public void petSummoned(Unit master, Pet pet) {
		getGameLog().petSummoned(master, pet);
	}

	public void petDied(Pet pet, Spell spell, Context parentContext) {
		EventContext.firePetDied(owner, pet, spell, parentContext);
	}

	public void petDismissed(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petDismissed(owner, pet);

		EventContext.firePetDismissed(owner, pet, spell, parentContext);
	}

	public void petUnsummoned(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petUnsummoned(owner, pet);

		EventContext.firePetUnsummoned(owner, pet, spell, parentContext);
	}

	public void petSacrificed(Pet pet, Spell spell, Context parentContext) {
		if (pet == null) {
			return;
		}

		getGameLog().petSacrificed(owner, pet);

		EventContext.firePetSacrificed(owner, pet, spell, parentContext);
	}

	public void effectEnded(EffectInstance effectInstance, Context parentContext) {
		EventContext.fireEffectEnded(effectInstance, parentContext);
	}

	public void effectStacksMaxed(EffectInstance effectInstance, Context parentContext) {
		EventContext.fireStacksMaxed(effectInstance, parentContext);
	}

	public void effectCountersMaxed(EffectInstance effectInstance, Context parentContext) {
		EventContext.fireCountersMaxed(effectInstance, parentContext);
	}

	private GameLog getGameLog() {
		return owner.getGameLog();
	}

	private static Spell getSourceSpell(Context parentContext, Spell spell) {
		if (parentContext != null) {
			return parentContext.getSourceSpell();
		}
		return spell;
	}
}
