package wow.simulator.model.context;

import lombok.Setter;
import wow.character.model.snapshot.RngStrategy;
import wow.character.util.SpellTargetConditionArgs;
import wow.character.util.SpellTargetConditionChecker;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.AbilitySource;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.spell.Spell;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.component.ComponentCommand.*;

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

	private PetType sacrificedPetType;

	public SpellResolutionContext(Unit caster, Spell spell, TargetResolver targetResolver, Context parentContext, CastSpellAction action) {
		super(caster, spell, parentContext);
		this.targetResolver = targetResolver;
		this.action = action;
	}

	public List<EffectInstance> resolveCastSpell() {
		var effectSource = new AbilitySource(action.getAbility());

		return resolveSpell(effectSource);
	}

	public void resolveTriggeredSpell(Effect sourceEffect) {
		var effectSource = sourceEffect.getSource();

		resolveSpell(effectSource);
	}

	private List<EffectInstance> resolveSpell(EffectSource effectSource) {
		executeDirectCommands();

		return applyEffects(effectSource);
	}

	private void executeDirectCommands() {
		for (var command : spell.getDirectCommands()) {
			directComponentAction(command);
		}
	}

	private boolean hitRollOnlyOnce(Unit target) {
		if (action == null || Unit.areFriendly(caster, target)) {
			return true;
		}

		return hitRollByUnit.computeIfAbsent(target, this::hitRoll);
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, spell);
	}

	private void directComponentAction(DirectCommand command) {
		var lastValueSnapshot = getLastValueSnapshot();

		targetResolver.forEachTarget(
				command,
				componentTarget -> directComponentAction(command, componentTarget, lastValueSnapshot)
		);
	}

	private void directComponentAction(DirectCommand directCommand, Unit target, LastValueSnapshot last) {
		if (!checkSecondaryCondition(directCommand, target)) {
			return;
		}

		switch (directCommand) {
			case DealDamageDirectly command ->
					dealDirectDamage(command, target);

			case HealDirectly command ->
					directHeal(command, target);

			case LoseManaDirectly command ->
					directManaLoss(command, target);

			case GainManaDirectly command ->
					directManaGain(command, target);

			case Copy command ->
					copy(command, target, last);

			case SummonPet command ->
					summonPet(command, target);

			case SacrificePet command ->
					sacrificePet(command, target);

			default ->
					throw new UnsupportedOperationException();
		}
	}

	private boolean checkSecondaryCondition(HasSecondaryTargetCondition command, Unit target) {
		var args = new SpellTargetConditionArgs(caster, target);

		args.setSacrificedPetType(sacrificedPetType);

		return SpellTargetConditionChecker.check(command.condition(), args);
	}

	private void dealDirectDamage(DealDamageDirectly command, Unit target) {
		if (!hitRollOnlyOnce(target)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directDamage = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, true, critRoll);
	}

	private void directHeal(HealDirectly command, Unit target) {
		var snapshot = caster.getDirectHealingSnapshot(spell, target, command);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(command, target);
		var directHealing = snapshot.getDirectAmount(RngStrategy.AVERAGED, addBonus, critRoll);

		increaseHealth(target, directHealing, true, critRoll);
	}

	private void directManaLoss(LoseManaDirectly command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		decreaseMana(target, mana);
	}

	private void directManaGain(GainManaDirectly command, Unit target) {
		var mana = (command.min() + command.max()) / 2;

		increaseMana(target, mana);
	}

	protected void summonPet(SummonPet command, Unit target) {
		target.setActivePet(command.petType());
	}

	protected void sacrificePet(SacrificePet command, Unit target) {
		this.sacrificedPetType = target.getActivePetType();

		target.setActivePet(null);
	}

	@Override
	protected double getRatioPct(Copy command) {
		return valueParam != null ? valueParam : command.ratio().value();
	}

	private List<EffectInstance> applyEffects(EffectSource effectSource) {
		var appliedEffects = new ArrayList<EffectInstance>();

		for (var command : spell.getApplyEffectCommands()) {
			var appliedEffect = applyEffect(command, effectSource);

			appliedEffects.addAll(appliedEffect);
		}

		return appliedEffects;
	}

	private List<EffectInstance> applyEffect(ApplyEffect command, EffectSource effectSource) {
		if (command.target().hasType(GROUND)) {
			var groundEffect = putPeriodicEffectOnTheGround(command);

			return List.of(groundEffect);
		}

		var appliedEffects = new ArrayList<EffectInstance>();

		targetResolver.forEachTarget(
				command,
				effectTarget -> {
					var appliedEffect = applyEffect(command, effectTarget, effectSource);

					if (appliedEffect != null) {
						appliedEffects.add(appliedEffect);
					}
				}
		);

		return appliedEffects;
	}

	private EffectInstance applyEffect(ApplyEffect command, Unit target, EffectSource effectSource) {
		if (!checkSecondaryCondition(command, target) || !hitRollOnlyOnce(target)) {
			return null;
		}

		var replacementMode = command.replacementMode();
		var appliedEffect = createEffect(command, target, effectSource);
		var augmentations = getEffectAugmentations(appliedEffect);

		appliedEffect.augment(augmentations);
		target.addEffect(appliedEffect, replacementMode);
		return appliedEffect;
	}

	private EffectInstance createEffect(ApplyEffect command, Unit target, EffectSource effectSource) {
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, target, command);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		if (tickInterval.isPositive()) {
			return new PeriodicEffectInstance(
					caster,
					target,
					command.effect(),
					duration,
					tickInterval,
					command.numStacks(),
					command.numCharges(),
					effectSource,
					getSourceSpell(),
					this
			);
		} else {
			return new NonPeriodicEffectInstance(
					caster,
					target,
					command.effect(),
					duration,
					command.numStacks(),
					command.numCharges(),
					effectSource,
					getSourceSpell(),
					this
			);
		}
	}

	private PeriodicEffectInstance putPeriodicEffectOnTheGround(ApplyEffect command) {
		var effect = createGroundPeriodicEffect(command);
		var replacementMode = command.replacementMode();

		getSimulation().addGroundEffect(effect, replacementMode);
		return effect;
	}

	private PeriodicEffectInstance createGroundPeriodicEffect(ApplyEffect command) {
		var durationSnapshot = caster.getEffectDurationSnapshot(spell, null, command);
		var duration = durationSnapshot.getDuration();
		var tickInterval = durationSnapshot.getTickInterval();

		return new PeriodicEffectInstance(
				caster,
				null,
				command.effect(),
				duration,
				tickInterval,
				command.numStacks(),
				command.numCharges(),
				new AbilitySource(action.getAbility()),
				getSourceSpell(),
				this
		);
	}

	private EffectAugmentations getEffectAugmentations(EffectInstance effect) {
		var target = effect.getTarget();

		return getCharacterCalculationService().getEffectAugmentations(caster, spell, target);
	}

	private boolean shouldAddBonus(ChangeHealthDirectly command, Unit target) {
		var bonus = command.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}
}
