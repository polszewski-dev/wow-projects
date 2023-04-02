package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemSocketStatusDTO {
	private List<SocketStatusDTO> socketStatuses;
	private SocketBonusStatusDTO socketBonusStatus;
}
