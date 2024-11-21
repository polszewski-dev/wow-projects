package wow.commons.client.dto;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
public record ConsumableDTO(
		int id,
		String name,
		String attributes,
		String icon,
		String tooltip,
		boolean enabled
) {
	public ConsumableDTO withEnabled(boolean enabled) {
		return new ConsumableDTO(id, name, attributes, icon, tooltip, enabled);
	}
}
