package wow.simulator.model.context;

import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final Unit target;

	private Boolean hitRoll;
	private boolean spellHitFired;

	public SpellResolutionContext(Unit caster, Spell spell, Unit target) {
		super(caster, spell);
		this.target = target;
	}

	public boolean hitRoll(CastSpellAction action) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		if (hitRoll == null) {
			double hitChancePct = caster.getSpellHitPct(spell, target);
			this.hitRoll = caster.getRng().hitRoll(hitChancePct, spell);
			if (!hitRoll) {
				caster.getGameLog().spellResisted(action, target);
				EventContext.fireSpellResistedEvent(caster, target, spell);
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

	public void directComponentAction(DirectComponent directComponent, CastSpellAction action) {
		switch (directComponent.type()) {
			case DAMAGE ->
					dealDirectDamage(directComponent, action);
			case MANA_GAIN ->
					increaseMana(directComponent);
			case COPY_DAMAGE_AS_HEAL_PCT -> {
					// void
			}
			default ->
					throw new UnsupportedOperationException();
		}
		fireSpellHitEvent();
	}

	private void increaseMana(DirectComponent directComponent) {
		var targetResolver = new TargetResolver(caster, target);
		var componentTarget = targetResolver.getTarget(directComponent.target());

		increaseMana(componentTarget, (directComponent.min() + directComponent.max()) / 2);
	}

	private void dealDirectDamage(DirectComponent directComponent, CastSpellAction action) {
		if (!hitRoll(action)) {
			return;
		}

		dealDirectDamage(directComponent);
	}

	private void dealDirectDamage(DirectComponent directComponent) {
		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	public EffectInstance applyEffect(CastSpellAction action) {
		if (!hitRoll(action)) {
			return null;
		}

		return applyEffect(new AbilitySource(action.getAbility()));
	}

	public EffectInstance applyEffect(EffectSource effectSource) {
		var appliedEffect = createEffect(effectSource);
		target.addEffect(appliedEffect);
		fireSpellHitEvent();
		return appliedEffect;
	}

	private EffectInstance createEffect(EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval != 0) {
			return new PeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					Duration.seconds(duration),
					Duration.seconds(tickInterval),
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					Duration.seconds(duration),
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource
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

	private void fireSpellHitEvent() {
		if (spellHitFired || Unit.areFriendly(caster, target)) {
			return;
		}

		EventContext.fireSpellHitEvent(caster, target, spell);
		this.spellHitFired = true;
	}
}
