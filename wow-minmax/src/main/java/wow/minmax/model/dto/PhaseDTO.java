package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseDTO {
	private PhaseId id;
	private String name;
	private int maxLevel;
}
