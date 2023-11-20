package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemSocketStatusDTO {
	private List<SocketStatusDTO> socketStatuses;
	private SocketBonusStatusDTO socketBonusStatus;
}
