package wow.simulator.model.unit;

import lombok.RequiredArgsConstructor;
import wow.commons.model.spell.SpellTarget;

import java.util.Collection;
import java.util.Objects;

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
			case FRIEND ->
					getFriendlyTargetOrSelf();
			case ENEMY ->
					getHostileTarget();
			case TARGET ->
					Objects.requireNonNull(target);
			default ->
					throw new UnsupportedOperationException("No AoE targets atm");
		};
	}

	public boolean hasValidTarget(SpellTarget targetType) {
		return switch (targetType) {
			case FRIEND_AOE, FRIENDS_PARTY, PARTY, PARTY_AOE, ENEMY_AOE ->
					true;
			default ->
					getTarget(targetType) != null;
		};
	}

	public boolean hasAllValidTargets(Collection<SpellTarget> targetTypes) {
		return targetTypes.stream().allMatch(this::hasValidTarget);
	}

	private Unit getFriendlyTargetOrSelf() {
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
