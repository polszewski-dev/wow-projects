package wow.simulator.model.unit.impl;

import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.snapshot.*;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.*;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.cooldown.Cooldowns;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.Effects;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.*;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.GcdAction;
import wow.simulator.model.unit.action.IdleAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.model.update.UpdateStage;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextAware;
import wow.simulator.util.IdGenerator;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public abstract class UnitImpl implements Unit, SimulationContextAware {
	private static final IdGenerator<UnitId> ID_GENERATOR = new IdGenerator<>(UnitId::new);

	protected final UnitId id = ID_GENERATOR.newId();
	protected final String name;
	protected final Character character;

	protected final UnitResources resources = new UnitResources(this);
	private final Effects effects = new Effects(this);
	private final Cooldowns cooldowns = new Cooldowns(this);

	private final PendingActionQueue<UnitAction> pendingActionQueue = new PendingActionQueue<>();
	private final UpdateQueue<UnitAction> updateQueue = new UpdateQueue<>();

	private Consumer<Unit> onPendingActionQueueEmpty;

	private Rng rng;

	private SimulationContext simulationContext;

	protected UnitImpl(String name, Character character) {
		this.name = name;
		this.character = character;
		this.resources.setHealth(10_000, 10_000);
		this.resources.setMana(10_000, 10_000);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	@Override
	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		shareClock(effects);
		shareClock(cooldowns);
		shareClock(updateQueue);
	}

	@Override
	public void update(UpdateStage updateStage) {
		switch (updateStage) {
			case COOLDOWN -> cooldowns.updateAllPresentCooldowns();
			case UNIT -> {
				ensureAction();
				updateQueue.updateAllPresentActions();
			}
			case EFFECT -> effects.updateAllPresentActions();
			case ACTION -> {
				// ignore
			}
		}
	}

	private void ensureAction() {
		if (!updateQueue.isEmpty()) {
			return;
		}

		if (pendingActionQueue.isEmpty()) {
			onPendingActionQueueEmpty.accept(this);
		}

		if (pendingActionQueue.isEmpty()) {
			throw new IllegalStateException();
		}

		UnitAction newAction = pendingActionQueue.removeEarliestAction();

		updateQueue.add(newAction);
	}

	@Override
	public Optional<Time> getNextUpdateTime() {
		Optional<Time> nextActionUpdateTime = updateQueue.getNextUpdateTime();

		if (nextActionUpdateTime.isEmpty()) {
			nextActionUpdateTime = Optional.of(getClock().now());
		}

		return Stream.of(nextActionUpdateTime, effects.getNextUpdateTime(), cooldowns.getNextUpdateTime())
				.flatMap(Optional::stream)
				.min(Comparator.naturalOrder());
	}

	@Override
	public UnitId getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public StatSummary getStats() {
		return getCharacterCalculationService().getStatSummary(this);
	}

	@Override
	public Unit getTarget() {
		return (Unit) character.getTarget();
	}

	@Override
	public void setTarget(Unit target) {
		setTarget((Character) target);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void setOnPendingActionQueueEmpty(Consumer<Unit> onPendingActionQueueEmpty) {
		this.onPendingActionQueueEmpty = onPendingActionQueueEmpty;
	}

	@Override
	public void cast(AbilityId abilityId) {
		cast(abilityId, getTarget());
	}

	@Override
	public void cast(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		var targetResolver = getTargetResolver(target);
		var action = new CastSpellAction(this, ability, targetResolver);
		pendingActionQueue.add(action);
	}

	@Override
	public void idleUntil(Time time) {
		pendingActionQueue.add(new IdleAction(this, time));
	}

	@Override
	public void idleFor(Duration duration) {
		idleUntil(getClock().now().add(duration));
	}

	@Override
	public void triggerGcd(Duration gcd, UnitAction sourceAction) {
		GcdAction gcdAction = new GcdAction(gcd, sourceAction);
		updateQueue.add(gcdAction);
	}

	@Override
	public void interruptCurrentAction() {
		if (updateQueue.isEmpty() || !isCurrentActionInterruptible()) {
			return;
		}

		Predicate<UnitAction> isGcd = GcdAction.class::isInstance;

		updateQueue.removeIf(isGcd.negate());
		updateQueue.removeIf(isGcd);
	}

	// possible cases: (gdc, something), (something), (gcd)
	// (gcd) can't be interrupted

	private boolean isCurrentActionInterruptible() {
		var elements = updateQueue.getElements();

		if (elements.size() != 1) {
			return true;
		}

		UnitAction action = elements.iterator().next().get();

		return !(action instanceof GcdAction);
	}

	@Override
	public boolean canCast(AbilityId abilityId) {
		return canCast(abilityId, getTarget());
	}

	@Override
	public boolean canCast(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		return canCast(ability, target);
	}

	@Override
	public boolean canCast(Ability ability, Unit target) {
		var targetResolver = getTargetResolver(target);
		return canCast(ability, targetResolver);
	}

	@Override
	public boolean canCast(Ability ability, TargetResolver targetResolver) {
		return hasAllValidTargets(ability, targetResolver) && !isOnCooldown(ability) && canPaySpellCost(ability);
	}

	private boolean hasAllValidTargets(Ability ability, TargetResolver targetResolver) {
		var targets = ability.getTargets();
		return targetResolver.hasAllValidTargets(targets);
	}

	@Override
	public TargetResolver getTargetResolver(Unit target) {
		return new TargetResolver(this, target);
	}

	private boolean canPaySpellCost(Ability ability) {
		var costSnapshot = getSpellCostSnapshot(ability);
		return resources.canPay(costSnapshot.getCostToPay());
	}

	@Override
	public SpellCostSnapshot paySpellCost(Ability ability) {
		var costSnapshot = getSpellCostSnapshot(ability);
		var cost = costSnapshot.getCostToPay();

		resources.pay(cost, ability);

		return costSnapshot;
	}

	@Override
	public SpellCastContext getSpellCastContext(Ability ability) {
		var castSnapshot = getSpellCastSnapshot(ability);
		return new SpellCastContext(this, ability, castSnapshot);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		return getSpellCastSnapshot(ability);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Ability ability) {
		return getCharacterCalculationService().getSpellCastSnapshot(this, ability, (Character) null);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		return getSpellCostSnapshot(ability);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Ability ability) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getSpellCostSnapshot(this, ability, null, baseStats);
	}

	@Override
	public double getSpellHitPct(Spell spell, Unit target) {
		return getCharacterCalculationService().getSpellHitPct(this, spell, target);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		return getEffectDurationSnapshot(ability, target);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target) {
		return getCharacterCalculationService().getEffectDurationSnapshot(this, spell, target);
	}

	@Override
	public DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Spell spell, Unit target, DirectComponent directComponent) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getDirectSpellDamageSnapshot(this, spell, target, directComponent, baseStats);
	}

	@Override
	public PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Spell spell, Unit target) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getPeriodicSpellDamageSnapshot(this, spell, target, baseStats);
	}

	@Override
	public BaseStatsSnapshot getBaseStatsSnapshot() {
		return getCharacterCalculationService().getBaseStatsSnapshot(this);
	}

	@Override
	public Rng getRng() {
		if (rng == null) {
			rng = getRngFactory().newRng();
		}
		return rng;
	}

	@Override
	public int getCurrentHealth() {
		return resources.getCurrentHealth();
	}

	@Override
	public int getCurrentMana() {
		return resources.getCurrentMana();
	}

	@Override
	public int increaseHealth(int amount, boolean crit, Spell spell) {
		return resources.increaseHealth(amount, crit, spell);
	}

	@Override
	public int decreaseHealth(int amount, boolean crit, Spell spell) {
		return resources.decreaseHealth(amount, crit, spell);
	}

	@Override
	public int increaseMana(int amount, boolean crit, Spell spell) {
		return resources.increaseMana(amount, crit, spell);
	}

	@Override
	public int decreaseMana(int amount, boolean crit, Ability ability) {
		return resources.decreaseMana(amount, crit, ability);
	}

	@Override
	public void addEffect(EffectInstance effect) {
		effects.addEffect(effect);
	}

	@Override
	public void removeEffect(EffectInstance effect) {
		effects.removeEffect(effect);
	}

	@Override
	public boolean isUnderEffect(AbilityId abilityId, Unit owner) {
		return effects.isUnderEffect(abilityId, owner);
	}

	@Override
	public Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner) {
		return effects.getEffect(abilityId, owner);
	}

	@Override
	public boolean hasEffect(AbilityId requiredEffect, Unit effectOwner) {
		return effects.isUnderEffect(requiredEffect, effectOwner);
	}

	@Override
	public boolean isOnCooldown(AbilityId abilityId) {
		return cooldowns.isOnCooldown(abilityId);
	}

	@Override
	public boolean isOnCooldown(Ability ability) {
		return isOnCooldown(ability.getAbilityId());
	}

	@Override
	public void triggerCooldown(Ability ability, Duration actualDuration) {
		cooldowns.triggerCooldown(ability, actualDuration);
	}

	// character interface

	@Override
	public Phase getPhase() {
		return character.getPhase();
	}

	@Override
	public CharacterClass getCharacterClass() {
		return character.getCharacterClass();
	}

	@Override
	public CreatureType getCreatureType() {
		return character.getCreatureType();
	}

	@Override
	public PetType getActivePetType() {
		return character.getActivePetType();
	}

	@Override
	public void setTarget(Character target) {
		character.setTarget(target);
	}

	@Override
	public BaseStatInfo getBaseStatInfo() {
		return character.getBaseStatInfo();
	}

	@Override
	public CombatRatingInfo getCombatRatingInfo() {
		return character.getCombatRatingInfo();
	}

	@Override
	public Spellbook getSpellbook() {
		return character.getSpellbook();
	}

	@Override
	public Buffs getBuffs() {
		return character.getBuffs();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		character.collectEffects(collector);
		effects.collectEffects(collector);
	}

	@Override
	public int getLevel() {
		return character.getLevel();
	}

	@Override
	public RaceId getRaceId() {
		return character.getRaceId();
	}

	@Override
	public Side getSide() {
		return character.getSide();
	}

	@Override
	public PveRole getRole() {
		return character.getRole();
	}

	@Override
	public boolean hasProfession(ProfessionId professionId) {
		return character.hasProfession(professionId);
	}

	@Override
	public boolean hasProfession(ProfessionId professionId, int level) {
		return character.hasProfession(professionId, level);
	}

	@Override
	public boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return character.hasProfessionSpecialization(specializationId);
	}

	@Override
	public boolean hasActivePet(PetType petType) {
		return character.hasActivePet(petType);
	}

	@Override
	public boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction) {
		return character.hasExclusiveFaction(exclusiveFaction);
	}

	@Override
	public boolean hasTalent(TalentId talentId) {
		return character.hasTalent(talentId);
	}

	@Override
	public Percent getHealthPct() {
		return Percent.of(100.0 * resources.getCurrentHealth() / resources.geMaxHealth());
	}

	@Override
	public void setHealthPct(Percent healthPct) {
		throw new UnsupportedOperationException();
	}
}
