package wow.simulator.util;

import lombok.RequiredArgsConstructor;

import java.util.function.LongFunction;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@RequiredArgsConstructor
public class IdGenerator<T> {
	private long idGen;
	private final LongFunction<T> idFactory;

	public T newId() {
		return idFactory.apply(++idGen);
	}
}
