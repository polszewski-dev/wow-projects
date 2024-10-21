package wow.simulator.model.unit;

import lombok.RequiredArgsConstructor;
import wow.commons.model.spell.SpellTarget;

import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2023-11-02
 */
@RequiredArgsConstructor
public class TargetResolver {
	private final Unit caster;
	private final Unit target;

	public Unit getTarget(SpellTarget targetType) {
		return switch (targetType) {
			case SELF ->
					caster;
			case PET ->
					throw new UnsupportedOperationException("No pets atm");
			case FRIEND, FRIEND_AOE, FRIENDS_PARTY, PARTY, PARTY_AOE ->
					getFriendlyTarget();
			case ENEMY, ENEMY_AOE ->
					getHostileTarget();
			case TARGET, ATTACKER ->
					throw new IllegalArgumentException();
		};
	}

	public boolean hasValidTarget(SpellTarget targetType) {
		return getTarget(targetType) != null;
	}

	public boolean hasAllValidTargets(Collection<SpellTarget> targetTypes) {
		return targetTypes.stream().allMatch(this::hasValidTarget);
	}

	private Unit getFriendlyTarget() {
		if (target == null) {
			return caster;
		}
		return Unit.areFriendly(caster, target) ? target : null;
	}

	private Unit getHostileTarget() {
		if (target == null) {
			return null;
		}
		return Unit.areHostile(caster, target) ? target : null;
	}
}
