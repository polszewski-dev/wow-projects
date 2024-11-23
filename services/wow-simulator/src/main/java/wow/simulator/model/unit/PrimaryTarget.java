package wow.simulator.model.unit;

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

	default Unit getSingleTarget() {
		return this instanceof PrimaryTarget.Specified s ? s.unit() : null;
	}

	default TargetResolver getTargetResolver(Unit caster) {
		return switch (this) {
			case PrimaryTarget.Invalid() ->
					null;
			case PrimaryTarget.Empty() ->
					null;
			case PrimaryTarget.Self(var ignored) ->
					TargetResolver.ofSelf(caster);
			case PrimaryTarget.Friend(var unit) ->
					TargetResolver.ofFriend(caster, unit);
			case PrimaryTarget.Enemy(var unit) ->
					TargetResolver.ofEnemy(caster, unit);
		};
	}

}
