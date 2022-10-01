package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.spells.SpellId;
import wow.commons.repository.impl.parsers.SimpleAttributeParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-09-18
 */
public class StatSetterParams {
	private String specialType;
	private SimpleAttributeParser attributeParser;
	private String specialAmount;
	private String specialDuration;
	private String specialCd;
	private String specialProcChance;
	private String specialProcCd;
	private List<SpellId> specialSpell;

	public String getSpecialType() {
		return specialType;
	}

	public void setSpecialType(String specialType) {
		this.specialType = specialType;
	}

	public SimpleAttributeParser getAttributeParser() {
		return attributeParser;
	}

	public void setAttributeParser(SimpleAttributeParser attributeParser) {
		this.attributeParser = attributeParser;
	}

	public String getSpecialAmount() {
		return specialAmount;
	}

	public void setSpecialAmount(String specialAmount) {
		this.specialAmount = specialAmount;
	}

	public String getSpecialDuration() {
		return specialDuration;
	}

	public void setSpecialDuration(String specialDuration) {
		this.specialDuration = specialDuration;
	}

	public String getSpecialCd() {
		return specialCd;
	}

	public void setSpecialCd(String specialCd) {
		this.specialCd = specialCd;
	}

	public String getSpecialProcChance() {
		return specialProcChance;
	}

	public void setSpecialProcChance(String specialProcChance) {
		this.specialProcChance = specialProcChance;
	}

	public String getSpecialProcCd() {
		return specialProcCd;
	}

	public void setSpecialProcCd(String specialProcCd) {
		this.specialProcCd = specialProcCd;
	}

	public List<SpellId> getSpecialSpell() {
		return specialSpell;
	}

	public void setSpecialSpell(List<SpellId> specialSpell) {
		this.specialSpell = specialSpell;
	}
}
