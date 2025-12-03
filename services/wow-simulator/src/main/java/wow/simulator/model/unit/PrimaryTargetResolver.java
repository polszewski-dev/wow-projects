package wow.simulator.model.unit;

import lombok.AllArgsConstructor;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.SpellTarget;

import static wow.commons.model.spell.SpellTargetType.SELF;
import static wow.simulator.model.unit.Unit.areFriendly;
import static wow.simulator.model.unit.Unit.areHostile;

/**
 * User: POlszewski
 * Date: 2024-11-24
 */
@AllArgsConstructor
public class PrimaryTargetResolver {
	private final Ability ability;
	private final Unit self;
	private final Unit defaultTarget;
	private final Unit explicitTarget;

	public PrimaryTarget getPrimaryTarget() {
		var targets = ability.getTargets();
		var singleTargets = targets.stream()
				.filter(SpellTarget::isSingle)
				.toList();

		if (singleTargets.isEmpty()) {
			if (explicitTarget != null) {
				return PrimaryTarget.INVALID;
			} else {
				return PrimaryTarget.EMPTY;
			}
		}

		var singleTargetsExceptSelf = singleTargets.stream()
				.filter(x -> !x.hasType(SELF))
				.toList();

		if (singleTargetsExceptSelf.size() == 1) {
			return resolveTarget(singleTargetsExceptSelf.getFirst());
		}

		if (singleTargets.size() == 1) {
			return resolveTarget(singleTargets.getFirst());
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget resolveTarget(SpellTarget spellTarget) {
		return switch (spellTarget.type()) {
			case SELF ->
					getSelf();
			case PET ->
					throw new UnsupportedOperationException("No pets atm");
			case FRIEND, FRIENDS_PARTY ->
					getFriendlyTarget();
			case ENEMY ->
					getHostileTarget();
			case ANY ->
					getAnyTarget();
			default ->
					throw new UnsupportedOperationException("No AoE targets atm");
		};
	}

	private PrimaryTarget getSelf() {
		if (explicitTarget == self || explicitTarget == null) {
			return PrimaryTarget.ofSelf(self);
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getFriendlyTarget() {
		if (explicitTarget != null) {
			if (areFriendly(self, explicitTarget)) {
				return PrimaryTarget.ofFriend(explicitTarget);
			} else {
				return PrimaryTarget.INVALID;
			}
		}

		if (defaultTarget != null && areFriendly(self, defaultTarget)) {
			return PrimaryTarget.ofFriend(defaultTarget);
		}

		return PrimaryTarget.ofFriend(self);
	}

	private PrimaryTarget getHostileTarget() {
		if (explicitTarget != null) {
			if (areHostile(self, explicitTarget)) {
				return PrimaryTarget.ofEnemy(explicitTarget);
			} else {
				return PrimaryTarget.INVALID;
			}
		}

		if (defaultTarget != null && areHostile(self, defaultTarget)) {
			return PrimaryTarget.ofEnemy(defaultTarget);
		}

		return PrimaryTarget.INVALID;
	}

	private PrimaryTarget getAnyTarget() {
		if (explicitTarget != null) {
			return PrimaryTarget.ofAny(explicitTarget);
		}
		if (defaultTarget != null) {
			return PrimaryTarget.ofAny(defaultTarget);
		}
		return PrimaryTarget.ofAny(self);
	}
}
