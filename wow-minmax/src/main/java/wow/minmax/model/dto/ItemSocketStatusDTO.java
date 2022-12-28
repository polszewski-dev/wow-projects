package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Data
@AllArgsConstructor
public class ItemSocketStatusDTO {
	private List<SocketStatusDTO> socketStatuses;
	private SocketBonusStatusDTO socketBonusStatus;
}
