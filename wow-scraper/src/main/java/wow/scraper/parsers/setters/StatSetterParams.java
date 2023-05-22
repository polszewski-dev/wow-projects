package wow.scraper.parsers.setters;

import lombok.Data;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.professions.ProfessionId;
import wow.commons.util.PrimitiveAttributeSupplier;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-09-18
 */
@Data
public class StatSetterParams {
	private String type;
	private PrimitiveAttributeSupplier statsSupplier;
	private String amount;
	private String duration;
	private String expression;
	private List<ItemType> itemTypes;
	private List<ItemSubType> itemSubTypes;
	private ProfessionId requiredProfession;
	private Integer requiredProfessionLevel;
	private List<PveRole> pveRoles;
}
