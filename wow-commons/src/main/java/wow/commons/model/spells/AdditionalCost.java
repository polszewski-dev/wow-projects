package wow.commons.model.spells;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public class AdditionalCost {
	private final CostType type;
	private final int amount;
	private final boolean scaled;

	public AdditionalCost(CostType type, int amount, boolean scaled) {
		this.type = type;
		this.amount = amount;
		this.scaled = scaled;
	}

	public CostType getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isScaled() {
		return scaled;
	}
}
