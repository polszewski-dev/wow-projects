package wow.simulator.model.context;

import wow.character.model.snapshot.RngStrategy;
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

import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final TargetResolver targetResolver;
	private final CastSpellAction action;
	private final Map<Unit, Boolean> hitRollByUnit = new HashMap<>();

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver, Context parentContext, CastSpellAction action) {
		super(caster, spell, parentContext);
		this.targetResolver = targetResolver;
		this.action = action;
	}

	public boolean hitRoll(Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		return hitRollByUnit.computeIfAbsent(target, this::hitRollOnce);
	}

	private boolean hitRollOnce(Unit target) {
		var hitChancePct = caster.getSpellHitPct(spell, target);
		var hitRoll = caster.getRng().hitRoll(hitChancePct, spell);

		if (hitRoll) {
			caster.getGameLog().spellHit(action, target);
			EventContext.fireSpellHitEvent(caster, target, spell, this);
		} else {
			caster.getGameLog().spellResisted(action, target);
			EventContext.fireSpellResistedEvent(caster, target, spell, this);
		}

		return hitRoll;
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	public void directComponentAction(DirectComponent directComponent) {
		switch (directComponent.type()) {
			case DAMAGE ->
					dealDirectDamage(directComponent);
			case HEAL ->
					directHeal(directComponent);
			case MANA_GAIN ->
					increaseMana(directComponent);
			case COPY_DAMAGE_AS_HEAL_PCT ->
					copyDamageAsHeal(directComponent);
			case COPY_DAMAGE_AS_MANA_GAIN_PCT ->
					copyDamageAsManaGain(directComponent);
			case COPY_HEALTH_PAID_AS_MANA_GAIN_PCT ->
					copyHealthPaidAsManaGain(directComponent);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealDirectDamage(DirectComponent directComponent) {
		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> dealDirectDamage(directComponent, componentTarget)
		);
	}

	private void dealDirectDamage(DirectComponent directComponent, Unit target) {
		if (!hitRoll(target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	private void directHeal(DirectComponent directComponent) {
		var heal = (directComponent.min() + directComponent.max()) / 2;

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseHealth(componentTarget, heal, true, false)
		);
	}

	private void copyDamageAsHeal(DirectComponent directComponent) {
		var heal = getScaledValue(getSourceContext().getLastDamageDone(), directComponent);

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseHealth(componentTarget, heal, true, false)
		);
	}

	private void copyDamageAsManaGain(DirectComponent directComponent) {
		var manaGain = getScaledValue(getSourceContext().getLastDamageDone(), directComponent);

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseMana(componentTarget, manaGain)
		);
	}

	private void copyHealthPaidAsManaGain(DirectComponent directComponent) {
		var mana = getScaledValue(parentContext.getLastHealthPaid(), directComponent);

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseMana(componentTarget, mana)
		);
	}

	private void increaseMana(DirectComponent directComponent) {
		var mana = (directComponent.min() + directComponent.max()) / 2;

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseMana(componentTarget, mana)
		);
	}

	public EffectInstance applyEffect(Unit target) {
		if (!hitRoll(target)) {
			return null;
		}

		return applyEffect(new AbilitySource(action.getAbility()));
	}

	public EffectInstance applyEffect(EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var target = targetResolver.getTarget(effectApplication);
		var replacementMode = effectApplication.replacementMode();
		var appliedEffect = createEffect(target, effectSource);

		target.addEffect(appliedEffect, replacementMode);
		return appliedEffect;
	}

	private EffectInstance createEffect(Unit target, EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval.isPositive()) {
			return new PeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					duration,
					tickInterval,
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource,
					getSourceSpell(),
					this
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					effectApplication.effect(),
					duration,
					effectApplication.numStacks(),
					effectApplication.numCharges(),
					effectSource,
					getSourceSpell(),
					this
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

	private int getScaledValue(int value, DirectComponent directComponent) {
		var ratioPct = directComponent.min();
		return (int) (value * ratioPct / 100.0);
	}
}
