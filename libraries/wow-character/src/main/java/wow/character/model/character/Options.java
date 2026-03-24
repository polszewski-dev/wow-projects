package wow.character.model.character;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2026-03-05
 */
public abstract class Options<T, I> {
	private final Map<I, T> availableById = new LinkedHashMap<>();
	private final Map<String, T> availableByName = new LinkedHashMap<>();
	private final Map<String, T> enabledByKey = new LinkedHashMap<>();

	public List<T> getList() {
		return List.copyOf(enabledByKey.values());
	}

	public Stream<T> getStream() {
		return enabledByKey.values().stream();
	}

	public List<T> getAvailable() {
		return List.copyOf(availableById.values());
	}

	public Stream<T> getAvailableStream() {
		return availableById.values().stream();
	}

	public void forEach(Consumer<T> action) {
		enabledByKey.values().forEach(action);
	}

	public boolean has(I id) {
		return getStream().anyMatch(option -> getId(option).equals(id));
	}

	public boolean has(String name) {
		return getStream().anyMatch(option -> getName(option).equals(name));
	}

	public void setAvailable(List<T> options) {
		availableById.clear();
		availableByName.clear();

		for (var option : options) {
			availableById.put(getId(option), option);
			availableByName.put(getName(option), option);
		}

		var namesToEnable = getEnabledNames();

		reset();

		setNamesIgnoreMissing(namesToEnable);
	}

	private List<String> getEnabledNames() {
		return getStream()
				.map(this::getName)
				.toList();
	}

	public <R> List<R> getIds(Function<I, R> mapper) {
		return getStream()
				.map(this::getId)
				.map(mapper)
				.toList();
	}

	public List<I> getIds() {
		return getIds(Function.identity());
	}

	public void setIds(Collection<I> ids) {
		setIds(ids, Function.identity());
	}

	public <M> void setIds(Collection<M> ids, Function<M, I> mapper) {
		reset();

		for (var id : ids) {
			enable(mapper.apply(id));
		}
	}

	public void setNames(String... names) {
		setNames(List.of(names));
	}

	public void setNames(Collection<String> names) {
		reset();

		for (var name : names) {
			enable(name);
		}
	}

	private void setNamesIgnoreMissing(Collection<String> names) {
		reset();

		for (var name : names) {
			if (isAvailable(name)) {
				enable(name);
			}
		}
	}

	public void setIf(Predicate<T> predicate) {
		getAvailable().stream()
				.filter(predicate)
				.forEach(option -> enableImpl(option, true));
	}

	public List<OptionStatus<T>> getStatuses() {
		return getAvailableStream()
				.map(option -> new OptionStatus<>(option, has(getId(option))))
				.toList();
	}

	private boolean isAvailable(String name) {
		return availableByName.containsKey(name);
	}

	public void reset() {
		enabledByKey.clear();
	}

	public void enable(I id, boolean enabled) {
		var option = get(id).orElseThrow();

		enableImpl(option, enabled);
	}

	public void enable(I id) {
		enable(id, true);
	}

	public void disable(I id) {
		enable(id, false);
	}

	public void enable(String name, boolean enabled) {
		var option = get(name).orElseThrow();

		enableImpl(option, enabled);
	}

	public void enable(String name) {
		enable(name, true);
	}

	public void disable(String name) {
		enable(name, false);
	}

	private void enableImpl(T option, boolean enabled) {
		var key = getKey(option);

		if (enabled) {
			enabledByKey.put(key, option);
		} else {
			enabledByKey.remove(key);
		}
	}

	private Optional<T> get(I id) {
		var option = availableById.get(id);

		return Optional.ofNullable(option);
	}

	private Optional<T> get(String name) {
		var option = availableByName.get(name);

		return Optional.ofNullable(option);
	}

	protected abstract I getId(T option);

	protected abstract String getName(T option);

	protected abstract String getKey(T option);

	protected void copyInto(Options<T, I> copy) {
		copy.availableById.putAll(this.availableById);
		copy.availableByName.putAll(this.availableByName);
		copy.enabledByKey.putAll(this.enabledByKey);
	}
}
