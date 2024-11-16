package wow.simulator.model.context;

import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final Unit target;

	private Boolean hitRoll;

	public SpellResolutionContext(Unit caster, Spell spell, Unit target) {
		super(caster, spell);
		this.target = target;
	}

	public boolean hitRoll(CastSpellAction action) {
		if (Unit.areFriendly(caster, target)) {
			return true;
		}

		if (hitRoll == null) {
			double hitChancePct = caster.getSpellHitPct(spell, target);
			this.hitRoll = caster.getRng().hitRoll(hitChancePct, spell);
			if (!hitRoll) {
				caster.getGameLog().spellResisted(action, target);
			}
		}
		return hitRoll;
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	@Override
	protected SpellResolutionConversions getConversions() {
		return new SpellResolutionConversions(caster, spell);
	}

	public void dealDirectDamage(DirectComponent directComponent, CastSpellAction action) {
		if (!hitRoll(action)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, critRoll);
	}

	public EffectInstance applyEffect(CastSpellAction action) {
		if (!hitRoll(action)) {
			return null;
		}

		var appliedEffect = createEffect();

		target.addEffect(appliedEffect);

		return appliedEffect;
	}

	private EffectInstance createEffect() {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval != 0) {
			return new PeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					spell,
					Duration.seconds(duration),
					Duration.seconds(tickInterval),
					effectApplication.numStacks(),
					effectApplication.numCharges()
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					spell,
					Duration.seconds(duration),
					effectApplication.numStacks(),
					effectApplication.numCharges()
			);
		}
	}

	private boolean shouldAddBonus(DirectComponent directComponent, Unit target) {
		var bonus = directComponent.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}
}
