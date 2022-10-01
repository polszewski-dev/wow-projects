package wow.commons.model;

/**
 * User: POlszewski
 * Date: 2021-12-21
 */
public interface Copyable<T> {
	default T copy() {
		return copy(false);
	}

	default T readOnlyCopy() {
		return copy(true);
	}

	T copy(boolean readOnly);

	boolean isReadOnly();

	default void assertCanBeModified() {
		if (isReadOnly()) {
			throw new IllegalStateException("Can't modify");
		}
	}

	static <S extends Copyable<S>> S copyNullable(S object, boolean readOnly) {
		if (object == null) {
			return null;
		}
		return object.copy(readOnly);
	}
}
