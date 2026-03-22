package wow.character.model.character;

/**
 * User: POlszewski
 * Date: 2026-03-19
 */
public record OptionStatus<T>(
		T option,
		boolean enabled
) {
}
