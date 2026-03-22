package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2026-03-19
 */
public record OptionStatusDTO<T>(
		T option,
		boolean enabled
) {
}
