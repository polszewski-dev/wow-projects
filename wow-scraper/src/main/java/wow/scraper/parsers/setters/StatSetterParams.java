package wow.scraper.parsers.setters;

import lombok.Data;
import wow.commons.util.PrimitiveAttributeSupplier;

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
}
