package wow.simulator.model.context;

import wow.character.model.snapshot.PeriodicSpellComponentSnapshot;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final EffectInstance effect;
	private final PeriodicComponent periodicComponent;
	private final TargetResolver targetResolver;
	private final Map<Unit, PeriodicSpellComponentSnapshot> periodicSnapshots = new HashMap<>();

	public EffectUpdateContext(EffectInstance effect, Context parentContext) {
		super(effect.getOwner(), effect.getSourceSpell(), parentContext);
		this.effect = effect;
		this.periodicComponent = effect.getPeriodicComponent();
		this.targetResolver = getTargetResolver();
		computeSingleTargetSnapshot();
	}

	public void periodicComponentAction(int tickNo, int numStacks) {
		forEachTarget(
				componentTarget -> periodicComponentAction(tickNo, numStacks, componentTarget)
		);

		// multi-target periodic component requires snapshot to be computed each time damage is dealt

		if (isAoE()) {
			periodicSnapshots.clear();
		}
	}

	private void periodicComponentAction(int tickNo, int numStacks, Unit target) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					dealPeriodicDamage(tickNo, numStacks, target);
			case HEAL ->
					periodicHeal(tickNo, numStacks, target);
			case MANA_GAIN ->
					periodicManaGain(tickNo, numStacks, target);
			case PCT_OF_TOTAL_MANA_GAIN ->
					periodicPctOfTotalManaGain(tickNo, numStacks, target);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealPeriodicDamage(int tickNo, int numStacks, Unit target) {
		// multi-target periodic component requires hit roll each time damage is dealt

		if (isAoE() && !hitRoll(target)) {
			return;
		}

		var snapshot = getSpellDamageSnapshot(target);
		var tickDamage = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		decreaseHealth(target, tickDamage, false, false);
	}

	private void periodicHeal(int tickNo, int numStacks, Unit target) {
		var snapshot = getHealingSnapshot(target);
		var tickHealing = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseHealth(target, tickHealing, false, false);
	}

	private void periodicManaGain(int tickNo, int numStacks, Unit target) {
		var snapshot = getManaGainSnapshot(target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
	}

	private void periodicPctOfTotalManaGain(int tickNo, int numStacks, Unit target) {
		var snapshot = getPctOfTotalManaGainSnapshot(target);
		var roundedTickManaGain = getRoundedTickAmount(snapshot, tickNo, numStacks, target);

		increaseMana(target, roundedTickManaGain);
	}

	private int getRoundedTickAmount(PeriodicSpellComponentSnapshot snapshot, int tickNo, int numStacks, Unit target) {
		var tickAmount = numStacks * snapshot.getTickAmount(tickNo);

		return roundValue(tickAmount, target);
	}

	private TargetResolver getTargetResolver() {
		return effect.getTarget() != null
				? TargetResolver.ofTarget(effect.getOwner(), effect.getTarget())
				: TargetResolver.ofSelf(effect.getOwner());
	}

	private void forEachTarget(Consumer<Unit> consumer) {
		targetResolver.forEachTarget(
				periodicComponent,
				consumer
		);
	}

	private boolean hasSingleTargetComponent() {
		return periodicComponent != null && periodicComponent.target().isSingle();
	}

	private boolean isAoE() {
		return periodicComponent.target().isAoE();
	}

	private void computeSingleTargetSnapshot() {
		if (!hasSingleTargetComponent()) {
			return;
		}

		forEachTarget(this::computeSingleTargetSnapshot);
	}

	private void computeSingleTargetSnapshot(Unit target) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					getSpellDamageSnapshot(target);
			case HEAL ->
					getHealingSnapshot(target);
			case PCT_OF_TOTAL_MANA_GAIN ->
					getPctOfTotalManaGainSnapshot(target);
			default -> {
				// void
			}
		}
	}

	private PeriodicSpellComponentSnapshot getSpellDamageSnapshot(Unit target) {
		return periodicSnapshots.computeIfAbsent(
				target,
				x -> caster.getPeriodicSpellDamageSnapshot(spell, target)
		);
	}

	private PeriodicSpellComponentSnapshot getHealingSnapshot(Unit target) {
		return periodicSnapshots.computeIfAbsent(
				target,
				x -> caster.getPeriodicHealingSnapshot(spell, target)
		);
	}

	private PeriodicSpellComponentSnapshot getManaGainSnapshot(Unit target) {
		return periodicSnapshots.computeIfAbsent(
				target,
				x -> caster.getPeriodicManaGainSnapshot(spell, target)
		);
	}

	private PeriodicSpellComponentSnapshot getPctOfTotalManaGainSnapshot(Unit target) {
		return periodicSnapshots.computeIfAbsent(
				target,
				x -> caster.getPeriodicPctOfTotalManaGainSnapshot(spell, target)
		);
	}

	public void increaseEffect(double effectIncreasePct) {
		var snapshot = periodicSnapshots.get(effect.getTarget());

		snapshot.increaseEffect(effectIncreasePct);
	}
}
