package wow.simulator.model.context;

import wow.character.model.snapshot.PeriodicSpellDamageSnapshot;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.RoundingReminder;

import java.util.HashMap;
import java.util.Map;

import static wow.commons.model.effect.component.ComponentType.DAMAGE;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
public class EffectUpdateContext extends Context {
	private final EffectInstance effect;
	private final TargetResolver targetResolver;
	private final Map<Unit, PeriodicSpellDamageSnapshot> spellDamageSnapshots = new HashMap<>();
	private final RoundingReminder roundingReminder = new RoundingReminder();

	public EffectUpdateContext(EffectInstance effect, Context parentContext) {
		super(effect.getOwner(), effect.getSourceSpell(), parentContext);
		this.effect = effect;
		this.targetResolver = getTargetResolver();
		if (hasSingleTargetDamagingPeriodicComponent()) {
			computeSpellDamageSnapshot();
		}
	}

	public void periodicComponentAction(PeriodicComponent periodicComponent, int tickNo, int numStacks) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					dealPeriodicDamage(tickNo, numStacks);
			case MANA_GAIN ->
					periodicManaGain(numStacks);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void dealPeriodicDamage(int tickNo, int numStacks) {
		targetResolver.forEachTarget(
				effect.getPeriodicComponent(),
				componentTarget -> dealPeriodicDamage(tickNo, numStacks, componentTarget)
		);

		// multi-target periodic component requires snapshot to be computed each time damage is dealt

		if (isAoE()) {
			spellDamageSnapshots.clear();
		}
	}

	private void dealPeriodicDamage(int tickNo, int numStacks, Unit target) {
		// multi-target periodic component requires hit roll each time damage is dealt

		if (isAoE() && !hitRoll(target)) {
			return;
		}

		var snapshot = getSpellDamageSnapshot(target);
		var tickDamage = numStacks * snapshot.getTickDamage(tickNo);
		var roundedTickDamage = roundingReminder.roundValue(tickDamage);

		decreaseHealth(target, roundedTickDamage, false, false);
	}

	private void periodicManaGain(int numStacks) {
		targetResolver.forEachTarget(
				effect.getPeriodicComponent(),
				componentTarget -> periodicManaGain(numStacks, componentTarget)
		);
	}

	private void periodicManaGain(int numStacks, Unit target) {
		var amount = numStacks * effect.getPeriodicComponent().amount();
		increaseMana(target, amount);
	}

	private PeriodicSpellDamageSnapshot getSpellDamageSnapshot(Unit target) {
		return spellDamageSnapshots.computeIfAbsent(
				target,
				targetKey -> caster.getPeriodicSpellDamageSnapshot(spell, targetKey)
		);
	}

	private TargetResolver getTargetResolver() {
		return effect.getTarget() != null
				? TargetResolver.ofTarget(effect.getOwner(), effect.getTarget())
				: TargetResolver.ofSelf(effect.getOwner());
	}

	private boolean hasSingleTargetDamagingPeriodicComponent() {
		return effect.hasPeriodicComponent(DAMAGE) && effect.getPeriodicComponent().target().isSingle();
	}

	private boolean isAoE() {
		return effect.getPeriodicComponent().target().isAoE();
	}

	private void computeSpellDamageSnapshot() {
		targetResolver.forEachTarget(
				effect.getPeriodicComponent(),
				this::getSpellDamageSnapshot
		);
	}

	public void increaseEffect(double effectIncreasePct) {
		var snapshot = getSpellDamageSnapshot(effect.getTarget());

		snapshot.setDamagePct(snapshot.getDamagePct() + effectIncreasePct);
	}
}
