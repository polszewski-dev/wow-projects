package wow.commons.model.spells;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public class Cost {
	private final CostType type;
	private final int amount;

	public Cost(CostType type, int amount) {
		this.type = type;
		this.amount = amount;
	}

	public CostType getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}
}
