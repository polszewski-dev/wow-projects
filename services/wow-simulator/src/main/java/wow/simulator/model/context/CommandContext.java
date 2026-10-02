package wow.simulator.model.context;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.TriggeredSpell;
import wow.commons.model.spell.component.ComponentCommand;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.RoundingReminder;

import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2026-10-01
 */
public abstract class CommandContext extends Context {
	@Getter
	protected final Context parentContext;

	private record SpellAndTarget(Spell spell, Unit target) {}

	private Map<SpellAndTarget, RoundingReminder> roundingRemindersBySpellTarget;

	@Getter
	@Setter
	private int lastDamageDone;
	@Setter
	private int lastHealingDone;
	@Setter
	private int lastManaRestored;
	@Setter
	private int lastManaLost;

	@Setter
	private Spell sourceSpellOverride;

	protected CommandContext(Unit caster, Spell spell, Context parentContext) {
		super(caster, spell);
		this.parentContext = parentContext;
	}

	public boolean hitRoll(Unit target) {
		var hitChancePct = caster.getSpellHitPct(spell, target);
		var hitRoll = caster.getRng().hitRoll(hitChancePct, spell);

		if (hitRoll) {
			caster.getEventBus().spellHit(spell, target, this);
		} else {
			caster.getEventBus().spellResisted(spell, target, this);
		}

		return hitRoll;
	}

	protected void decreaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		target.decreaseHealth(amount, direct, crit, caster, spell, this);
	}

	protected void increaseHealth(Unit target, int amount, boolean direct, boolean crit) {
		target.increaseHealth(amount, direct, crit, caster, spell, this);
	}

	protected void increaseMana(Unit target, int amount, boolean direct, boolean crit) {
		target.increaseMana(amount, direct, crit, caster, spell, this);
	}

	protected void decreaseMana(Unit target, int amount, boolean direct, boolean crit) {
		target.decreaseMana(amount, direct, crit, caster, spell, this);
	}

	protected void copy(ComponentCommand.Copy copy, Unit target, boolean direct) {
		var from = getFrom(copy);
		var ratioPct = getRatioPct(copy);

		switch (copy.to()) {
			case DAMAGE ->
					copyAsDamage(target, from, ratioPct, direct);

			case HEAL ->
					copyAsHeal(target, from, ratioPct, direct);

			case MANA_LOSS ->
					copyAsManaLoss(target, from, ratioPct, direct);

			case MANA_GAIN ->
					copyAsManaGain(target, from, ratioPct, direct);
		}
	}

	private int getFrom(ComponentCommand.Copy copy) {
		return switch (copy.from()) {
			case DAMAGE -> lastDamageDone;
			case MANA_LOSS -> lastManaLost;
			case HEALTH_PAID -> ((SpellCastContext) parentContext).getLastHealthPaid();
			case PARENT_DAMAGE -> ((CommandContext) parentContext).lastDamageDone;
			case PARENT_MANA_GAIN -> ((CommandContext) parentContext).lastManaRestored;
			default -> throw new IllegalArgumentException(copy.from().name());
		};
	}

	protected double getRatioPct(ComponentCommand.Copy command) {
		return command.ratio().value();
	}

	private void copyAsDamage(Unit target, int value, double ratioPct, boolean direct) {
		var damage = getCharacterCalculationService().getCopiedAmountAsDamage(caster, getSourceSpell(), target, value, ratioPct);
		var roundedDamage = roundValue(damage, target);

		decreaseHealth(target, roundedDamage, direct, false);
	}

	private void copyAsHeal(Unit target, int value, double ratioPct, boolean direct) {
		var heal = getCharacterCalculationService().getCopiedAmountAsHeal(caster, getSourceSpell(), target, value, ratioPct);
		var roundedHeal = roundValue(heal, target);

		increaseHealth(target, roundedHeal, direct, false);
	}

	private void copyAsManaGain(Unit target, int value, double ratioPct, boolean direct) {
		var manaGain = getCharacterCalculationService().getCopiedAmountAsManaGain(caster, getSourceSpell(), target, value, ratioPct);
		var roundedManaGain = roundValue(manaGain, target);

		increaseMana(target, roundedManaGain, direct, false);
	}

	private void copyAsManaLoss(Unit target, int value, double ratioPct, boolean direct) {
		var manaLoss = value * ratioPct / 100;
		var roundedManaGain = roundValue(manaLoss, target);

		decreaseMana(target, roundedManaGain, direct, false);
	}

	protected int roundValue(double value, Unit target) {
		return getRootContext().getRoundingReminder(spell, target).roundValue(value);
	}

	private CommandContext getRootContext() {
		if (parentContext instanceof CommandContext parentCommandContext) {
			return parentCommandContext.getRootContext();
		}
		return this;
	}

	private RoundingReminder getRoundingReminder(Spell spell, Unit target) {
		if (roundingRemindersBySpellTarget == null) {
			roundingRemindersBySpellTarget = new HashMap<>();
		}

		return roundingRemindersBySpellTarget.computeIfAbsent(
				new SpellAndTarget(spell, target),
				x -> new RoundingReminder()
		);
	}

	public Spell getSourceSpell() {
		if (sourceSpellOverride != null) {
			return sourceSpellOverride;
		}
		if (spell instanceof TriggeredSpell) {
			return parentContext.spell;
		}
		return spell;
	}
}
