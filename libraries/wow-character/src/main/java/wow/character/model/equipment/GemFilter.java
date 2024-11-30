package wow.character.model.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2024-11-28
 */
@Getter
@Setter
@AllArgsConstructor
public class GemFilter {
	private boolean unique;

	public static GemFilter empty() {
		return new GemFilter(
				false
		);
	}
}
