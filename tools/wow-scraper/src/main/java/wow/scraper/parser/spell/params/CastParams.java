package wow.scraper.parser.spell.params;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public record CastParams(
	String castTime,
	boolean channeled,
	boolean ignoresGcd
) {
	public CastParams {
		Objects.requireNonNull(castTime);
	}
}
