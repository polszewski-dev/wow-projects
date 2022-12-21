package wow.character.model;

/**
 * User: POlszewski
 * Date: 2021-12-21
 */
public interface Copyable<T> {
	T copy();

	static <S extends Copyable<S>> S copyNullable(S object) {
		if (object == null) {
			return null;
		}
		return object.copy();
	}
}
