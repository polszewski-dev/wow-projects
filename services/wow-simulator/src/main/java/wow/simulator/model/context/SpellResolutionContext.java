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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final TargetResolver targetResolver;

	private final Map<Unit, Boolean> hitRollByUnit = new HashMap<>();
	private final Set<Unit> spellHitFiredByUnit = new HashSet<>();

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver, Context parentContext) {
		super(caster, spell, parentContext);
		this.targetResolver = targetResolver;
	}

	public boolean hitRoll(CastSpellAction action, Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		var fireEvents = !hitRollByUnit.containsKey(target);

		var hitRoll = hitRollByUnit.computeIfAbsent(target, key -> {
			double hitChancePct = caster.getSpellHitPct(spell, target);
			return caster.getRng().hitRoll(hitChancePct, spell);
		});

		if (fireEvents) {
			if (hitRoll) {
				caster.getGameLog().spellHit(action, target);
			} else {
				caster.getGameLog().spellResisted(action, target);
				EventContext.fireSpellResistedEvent(caster, target, spell, this);
			}
		}

		return hitRoll;
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	public void directComponentAction(DirectComponent directComponent, CastSpellAction action) {
		switch (directComponent.type()) {
			case DAMAGE ->
					dealDirectDamage(directComponent, action);
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

	private void dealDirectDamage(DirectComponent directComponent, CastSpellAction action) {
		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> dealDirectDamage(directComponent, action, componentTarget)
		);
	}

	private void dealDirectDamage(DirectComponent directComponent, CastSpellAction action, Unit target) {
		if (!hitRoll(action, target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
		fireSpellHitEvent(target);
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

	public EffectInstance applyEffect(CastSpellAction action, Unit target) {
		if (!hitRoll(action, target)) {
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
		fireSpellHitEvent(target);
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

	private void fireSpellHitEvent(Unit target) {
		if (spellHitFiredByUnit.contains(target) || Unit.areFriendly(caster, target)) {
			return;
		}

		EventContext.fireSpellHitEvent(caster, target, spell, this);
		spellHitFiredByUnit.add(target);
	}

	private int getScaledValue(int value, DirectComponent directComponent) {
		var ratioPct = directComponent.min();
		return (int) (value * ratioPct / 100.0);
	}
}
