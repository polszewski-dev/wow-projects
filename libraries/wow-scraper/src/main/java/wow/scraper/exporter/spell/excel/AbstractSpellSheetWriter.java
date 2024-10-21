package wow.scraper.exporter.spell.excel;

import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.CastInfo;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.spell.component.DirectComponentBonus;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.ID;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertSizeNoLargerThan;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public abstract class AbstractSpellSheetWriter<T extends Spell> extends SpellBaseSheetWriter<T, SpellBaseExcelBuilder> {
	protected AbstractSpellSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	protected void writeIdNameHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
	}

	protected void writeIdName(Spell spell) {
		setValue(spell.getId());
		setValue(spell.getName());
	}

	protected void writeSpellInfoHeader() {
		writeCastInfoHeader();
		setHeader(COOLDOWN);
		setHeader(RANGE);
		setHeader(REQUIRED_EFFECT);
		setHeader(EFFECT_REMOVED_ON_HIT);
		writeDirectComponentHeader();
		writeConversionHeader();
		writeEffectApplicationHeader();
		writeIconAndTooltipHeader();
	}

	protected void writeSpellInfo(Spell spell) {
		if (spell instanceof Ability ability) {
			writeCastInfo(ability.getCastInfo());
			setValue(ability.getCooldown(), Duration.ZERO);
			setValue(ability.getRange());
			setValue(ability.getRequiredEffect());
			setValue(ability.getEffectRemovedOnHit());
		} else {
			writeCastInfo(null);
			setValue((String) null);
			setValue((String) null);
			setValue((String) null);
			setValue((String) null);
		}
		writeDirectComponents(spell);
		writeConversion(spell.getConversion());
		writeEffectApplication(spell);
		writeIconAndTooltip(spell);
	}

	private void writeCastInfoHeader() {
		setHeader(CAST_TIME);
		setHeader(CAST_CHANNELED);
		setHeader(CAST_IGNORES_GCD);
	}

	private void writeCastInfo(CastInfo castInfo) {
		if (castInfo == null) {
			fillRemainingEmptyCols(3);
			return;
		}

		setValue(castInfo.castTime());
		setValue(castInfo.channeled());
		setValue(castInfo.ignoresGcd());
	}

	private void writeDirectComponentHeader() {
		for (int i = 1; i <= MAX_DIRECT_COMPONENTS; ++i) {
			writeDirectComponentHeader(i, i == 1);
		}
	}

	private void writeDirectComponentHeader(int idx, boolean writeBonus) {
		String prefix = getDirectComponentPrefix(idx);
		setHeader(TARGET, prefix);
		setHeader(DIRECT_TYPE, prefix);
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(DIRECT_MIN, prefix);
		setHeader(DIRECT_MAX, prefix);
		if (writeBonus) {
			writeBonusHeader(prefix);
		}
		setHeader(DIRECT_BOLT, prefix);
	}

	private void writeDirectComponents(Spell spell) {
		assertSizeNoLargerThan("direct components", spell.getDirectComponents(), MAX_DIRECT_COMPONENTS);

		for (int i = 0; i < MAX_DIRECT_COMPONENTS; ++i) {
			boolean writeBonus = i == 0;
			if (i < spell.getDirectComponents().size()) {
				writeDirectComponent(spell.getDirectComponents().get(i), writeBonus);
			} else {
				writeDirectComponent(null, writeBonus);
			}
		}
	}

	private void writeDirectComponent(DirectComponent directComponent, boolean writeBonus) {
		if (directComponent == null) {
			fillRemainingEmptyCols(writeBonus ? 10 : 7);
			return;
		}

		setValue(directComponent.target());
		setValue(directComponent.type());
		setValue(directComponent.coefficient().value());
		setValue(directComponent.school());
		setValue(directComponent.min());
		setValue(directComponent.max());
		if (writeBonus) {
			writeBonus(directComponent.bonus());
		} else if (directComponent.bonus() != null) {
			throw new IllegalArgumentException();
		}
		setValue(directComponent.bolt());
	}

	private void writeBonusHeader(String prefix) {
		setHeader(DIRECT_BONUS_MIN, prefix);
		setHeader(DIRECT_BONUS_MAX, prefix);
		setHeader(DIRECT_BONUS_REQUIRED_EFFECT, prefix);
	}

	private void writeBonus(DirectComponentBonus bonus) {
		if (bonus == null) {
			fillRemainingEmptyCols(3);
			return;
		}

		setValue(bonus.min());
		setValue(bonus.max());
		setValue(bonus.requiredEffect());
	}

	private void writeEffectApplicationHeader() {
		var prefix = APPLY_PREFIX;
		setHeader(TARGET, prefix);
		setHeader(APPLIED_EFFECT_ID, prefix);
		setHeader(APPLIED_EFFECT_DURATION, prefix);
		setHeader(APPLIED_EFFECT_STACKS, prefix);
		setHeader(APPLIED_EFFECT_CHARGES, prefix);
	}

	private void writeEffectApplication(Spell spell) {
		var effectApplication = spell.getEffectApplication();

		if (effectApplication == null) {
			fillRemainingEmptyCols(5);
			return;
		}

		setValue(effectApplication.target());
		setValue(effectApplication.effect().getEffectId());
		setValue(effectApplication.duration());
		setValue(effectApplication.numStacks());
		setValue(effectApplication.numCharges());
	}
}
