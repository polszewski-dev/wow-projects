package wow.scraper.parsers.setters;

import wow.commons.util.PrimitiveAttributeSupplier;

/**
 * User: POlszewski
 * Date: 2021-09-18
 */
public class StatSetterParams {
	private String type;
	private PrimitiveAttributeSupplier statsSupplier;
	private String amount;
	private String duration;
	private String cooldown;
	private String procChance;
	private String procCooldown;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PrimitiveAttributeSupplier getStatsSupplier() {
		return statsSupplier;
	}

	public void setStatsSupplier(PrimitiveAttributeSupplier statsSupplier) {
		this.statsSupplier = statsSupplier;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getCooldown() {
		return cooldown;
	}

	public void setCooldown(String cooldown) {
		this.cooldown = cooldown;
	}

	public String getProcChance() {
		return procChance;
	}

	public void setProcChance(String procChance) {
		this.procChance = procChance;
	}

	public String getProcCooldown() {
		return procCooldown;
	}

	public void setProcCooldown(String procCooldown) {
		this.procCooldown = procCooldown;
	}
}
