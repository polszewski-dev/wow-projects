package wow.simulator.model.context;

import lombok.Setter;
import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.EffectAugmentations;
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
	@Setter
	private Double valueParam;

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver, Context parentContext, CastSpellAction action) {
		super(caster, spell, parentContext);
		this.targetResolver = targetResolver;
		this.action = action;
	}

	public boolean hitRollOnlyOnce(Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		return hitRollByUnit.computeIfAbsent(target, this::hitRoll);
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
		if (!hitRollOnlyOnce(target)) {
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
		var heal = getCopiedValue(getSourceContext().getLastDamageDone(), directComponent);

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseHealth(componentTarget, heal, true, false)
		);
	}

	private void copyDamageAsManaGain(DirectComponent directComponent) {
		var manaGain = getCopiedValue(getSourceContext().getLastDamageDone(), directComponent);

		targetResolver.forEachTarget(
				directComponent,
				componentTarget -> increaseMana(componentTarget, manaGain)
		);
	}

	private void copyHealthPaidAsManaGain(DirectComponent directComponent) {
		var mana = getCopiedValue(parentContext.getLastHealthPaid(), directComponent);

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

	public void applyEffect(EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();

		targetResolver.forEachTarget(
				effectApplication,
				effectTarget -> applyEffect(effectTarget, effectSource)
		);
	}

	public EffectInstance applyEffect(Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return null;
		}

		return applyEffect(target, new AbilitySource(action.getAbility()));
	}

	public EffectInstance applyEffect(Unit target, EffectSource effectSource) {
		var effectApplication = spell.getEffectApplication();
		var replacementMode = effectApplication.replacementMode();
		var appliedEffect = createEffect(target, effectSource);
		var augmentations = getEffectAugmentations(appliedEffect);

		appliedEffect.augment(augmentations);
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

	public PeriodicEffectInstance putPeriodicEffectOnTheGround() {
		var effect = createGroundPeriodicEffect();

		getScheduler().add(effect);
		return effect;
	}

	private PeriodicEffectInstance createGroundPeriodicEffect() {
		var effectApplication = spell.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, null);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		return new PeriodicEffectInstance(
				caster,
				null,
				effectApplication.effect(),
				duration,
				tickInterval,
				effectApplication.numStacks(),
				effectApplication.numCharges(),
				new AbilitySource(action.getAbility()),
				getSourceSpell(),
				this
		);
	}

	private EffectAugmentations getEffectAugmentations(EffectInstance effect) {
		var target = effect.getTarget();

		return getCharacterCalculationService().getEffectAugmentations(caster, spell, target);
	}

	private boolean shouldAddBonus(DirectComponent directComponent, Unit target) {
		var bonus = directComponent.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}

	private int getCopiedValue(int value, DirectComponent directComponent) {
		var ratioPct = valueParam != null ? valueParam : directComponent.min();
		var copyIncreasePct = getCharacterCalculationService().getCopiedValueIncreasePct(caster, getSourceSpell());

		ratioPct += copyIncreasePct;

		return (int) (value * ratioPct / 100.0);
	}
}
