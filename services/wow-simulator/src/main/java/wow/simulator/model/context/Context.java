package wow.simulator.model.context;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.snapshot.SpellCostSnapshot;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.TriggeredSpell;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public abstract class Context {
	protected final Unit caster;
	protected final Spell spell;

	protected final Context parentContext;

	private int lastManaPaid;
	private int lastHealthPaid;
	private int lastDamageDone;
	private int lastHealingDone;
	private int lastManaRestored;
	private int lastManaDrained;

	@Setter
	private Spell sourceSpellOverride;

	protected Context(Unit caster, Spell spell, Context parentContext) {
		this.caster = caster;
		this.spell = spell;
		this.parentContext = parentContext;
	}

	protected abstract Conversions getConversions();

	protected void decreaseHealth(Unit target, int amount, boolean directDamage, boolean critRoll) {
		this.lastDamageDone = target.decreaseHealth(amount, critRoll, getSourceSpell());

		getConversions().performDamageDoneConversion(lastDamageDone);

		EventContext.fireSpellDamageEvent(caster, target, spell, directDamage, critRoll, this);
	}

	protected void increaseHealth(Unit target, int amount, boolean directHeal, boolean critRoll) {
		this.lastHealingDone = target.increaseHealth(amount, critRoll, getSourceSpell());

		EventContext.fireSpellHealEvent(caster, target, spell, directHeal, critRoll, this);
	}

	protected void increaseMana(Unit target, int amount) {
		this.lastManaRestored = target.increaseMana(amount, false, getSourceSpell());
	}

	protected void decreaseMana(Unit target, int amount) {
		this.lastManaDrained = target.decreaseMana(amount, false, getSourceSpell());
	}

	protected void setPaidCost(SpellCostSnapshot costSnapshot) {
		var cost = costSnapshot.getCostToPayUnreduced();

		switch (cost.resourceType()) {
			case MANA -> this.lastManaPaid = cost.amount();
			case HEALTH -> this.lastHealthPaid = cost.amount();
		}
	}

	protected Context getSourceContext() {
		if (parentContext == null || this == parentContext) {
			return this;
		}
		return spell instanceof TriggeredSpell ? parentContext : this;
	}

	protected Spell getSourceSpell() {
		if (sourceSpellOverride != null) {
			return sourceSpellOverride;
		}
		return getSourceContext().getSpell();
	}
}
