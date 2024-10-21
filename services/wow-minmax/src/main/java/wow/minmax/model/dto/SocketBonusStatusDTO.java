package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SocketBonusStatusDTO {
	private String bonus;
	private boolean enabled;
}
