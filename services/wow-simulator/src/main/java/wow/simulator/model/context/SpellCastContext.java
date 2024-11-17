package wow.simulator.model.context;

import wow.character.model.snapshot.SpellCastSnapshot;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.SpellTarget;
import wow.simulator.model.unit.TargetResolver;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.UnitId;

import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-08-13
 */
public class SpellCastContext extends Context {
	private final SpellCastSnapshot snapshot;
	private final Ability ability;
	private final Map<UnitId, SpellResolutionContext> spellResolutionContextMap = new HashMap<>();

	public SpellCastContext(Unit caster, Ability ability, SpellCastSnapshot snapshot) {
		super(caster, ability);
		this.snapshot = snapshot;
		this.ability = ability;
	}

	public Duration getGcd() {
		return Duration.seconds(snapshot.getGcd());
	}

	public Duration getCastTime() {
		return Duration.seconds(snapshot.getCastTime());
	}

	public boolean isInstantCast() {
		return snapshot.isInstantCast();
	}

	@Override
	public SpellCastConversions getConversions() {
		return new SpellCastConversions(caster, ability);
	}

	public void paySpellCost() {
		var costSnapshot = caster.paySpellCost(ability);
		var cooldown = Duration.seconds(costSnapshot.getCooldown());
		var cost = costSnapshot.getCostToPayUnreduced();

		caster.triggerCooldown(ability, cooldown);
		getConversions().performPaidCostConversion(cost);
	}

	public SpellResolutionContext getSpellResolutionContext(SpellTarget spellTarget, TargetResolver targetResolver) {
		var target = targetResolver.getTarget(spellTarget);

		return getSpellResolutionContext(target);
	}

	public SpellResolutionContext getSpellResolutionContext(Unit target) {
		return spellResolutionContextMap.computeIfAbsent(
				target.getId(),
				x -> new SpellResolutionContext(caster, ability, target)
		);
	}
}
