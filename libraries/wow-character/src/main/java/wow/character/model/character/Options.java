package wow.character.model.character;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2026-03-05
 */
public abstract class Options<T, I> {
	public abstract List<T> getList();

	public abstract Stream<T> getStream();

	public abstract List<T> getAvailable();

	public abstract void setAvailable(List<T> options);

	public abstract void setIds(Collection<I> ids);

	public abstract void setNames(Collection<String> names);

	public abstract void reset();

	public abstract void enable(I id, boolean enabled);

	public void enable(I id) {
		enable(id, true);
	}

	public void disable(I id) {
		enable(id, false);
	}

	public abstract void enable(String name, boolean enabled);

	public void enable(String name) {
		enable(name, true);
	}

	public void disable(String name) {
		enable(name, false);
	}
}
