package wow.simulator.model.context;

import wow.character.model.snapshot.RngStrategy;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.impl.TickingEffect;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.CastSpellAction;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
public class SpellResolutionContext extends Context {
	private final Unit target;

	private Boolean hitRoll;

	public SpellResolutionContext(Unit caster, Ability ability, Unit target) {
		super(caster, ability);
		this.target = target;
	}

	public boolean hitRoll(CastSpellAction action) {
		if (hitRoll == null) {
			double hitChancePct = caster.getSpellHitPct(ability, target);
			this.hitRoll = caster.getRng().hitRoll(hitChancePct, ability.getAbilityId());
			if (!hitRoll) {
				caster.getGameLog().spellResisted(action, target);
			}
		}
		return hitRoll;
	}

	private boolean critRoll(double critChancePct) {
		return caster.getRng().critRoll(critChancePct, ability.getAbilityId());
	}

	@Override
	protected SpellResolutionConversions getConversions() {
		return new SpellResolutionConversions(caster, ability);
	}

	public void dealDirectDamage(DirectComponent directComponent, CastSpellAction action) {
		if (!hitRoll(action)) {
			return;
		}

		var snapshot = caster.getDirectSpellDamageSnapshot(ability, target, directComponent);
		var critRoll = critRoll(snapshot.getCritPct());
		var addBonus = shouldAddBonus(directComponent, target);
		var directDamage = snapshot.getDirectDamage(RngStrategy.AVERAGED, addBonus, critRoll);

		decreaseHealth(target, directDamage, critRoll);
	}

	public TickingEffect applyEffect(CastSpellAction action) {
		if (!hitRoll(action)) {
			return null;
		}

		var effectApplication = ability.getEffectApplication();
		var durationSnapshot = caster.getEffectDurationSnapshot(ability, target);
		var duration = Duration.seconds(durationSnapshot.getDuration());
		var tickInterval = Duration.seconds(durationSnapshot.getTickInterval());

		var appliedEffect = new TickingEffect(
				caster,
				target,
				effectApplication.effect(),
				ability,
				duration,
				tickInterval,
				effectApplication.numStacks(),
				effectApplication.numCharges()
		);

		target.addEffect(appliedEffect);

		return appliedEffect;
	}

	private boolean shouldAddBonus(DirectComponent directComponent, Unit target) {
		var bonus = directComponent.bonus();

		if (bonus == null) {
			return false;
		}

		return bonus.requiredEffect() == null || target.hasEffect(bonus.requiredEffect(), caster);
	}
}
