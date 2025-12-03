package wow.simulator.model.unit;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2024-11-24
 */
public sealed interface PrimaryTarget {
	record Invalid() implements PrimaryTarget {}

	record Empty() implements PrimaryTarget {}

	sealed interface Specified extends PrimaryTarget {
		Unit unit();
	}

	record Self(Unit unit) implements Specified {}

	record Friend(Unit unit) implements Specified {}

	record Enemy(Unit unit) implements Specified {}

	record Any(Unit unit) implements Specified {}

	PrimaryTarget INVALID = new Invalid();

	PrimaryTarget EMPTY = new Empty();

	static Self ofSelf(Unit unit) {
		return new Self(unit);
	}

	static Friend ofFriend(Unit unit) {
		return new Friend(unit);
	}

	static Enemy ofEnemy(Unit unit) {
		return new Enemy(unit);
	}

	static Any ofAny(Unit unit) {
		return new Any(unit);
	}

	default Unit getSingleTarget() {
		return this instanceof Specified s ? s.unit() : null;
	}

	default Unit requireSingleTarget() {
		return Objects.requireNonNull(getSingleTarget());
	}

	default TargetResolver getTargetResolver(Unit caster) {
		return switch (this) {
			case Invalid() ->
					null;
			case Empty() ->
					TargetResolver.ofSelf(caster);
			case Self(var ignored) ->
					TargetResolver.ofSelf(caster);
			case Friend(var unit) ->
					TargetResolver.ofFriend(caster, unit);
			case Enemy(var unit) ->
					TargetResolver.ofEnemy(caster, unit);
			case Any(var unit) ->
					TargetResolver.ofAny(caster, unit);
		};
	}

}
