package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
@Data
@AllArgsConstructor
public class UpgradesDTO {
	private List<UpgradeDTO> head;
	private List<UpgradeDTO> neck;
	private List<UpgradeDTO> shoulder;
	private List<UpgradeDTO> back;
	private List<UpgradeDTO> chest;
	private List<UpgradeDTO> wrist;
	private List<UpgradeDTO> hands;
	private List<UpgradeDTO> waist;
	private List<UpgradeDTO> legs;
	private List<UpgradeDTO> feet;
	private List<UpgradeDTO> finger1;
	private List<UpgradeDTO> trinket1;
	private List<UpgradeDTO> mainhand;
	private List<UpgradeDTO> ranged;
}
