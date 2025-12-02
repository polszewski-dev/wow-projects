package wow.scraper.exporter.spell.excel;

import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.CastInfo;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponentBonus;

import java.util.List;

import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.ID;
import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.spell.AbstractSpellSheetParser.Config;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
import static wow.scraper.util.CommonAssertions.assertSizeNoLargerThan;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public abstract class AbstractSpellSheetWriter<T extends Spell> extends SpellBaseSheetWriter<T, SpellBaseExcelBuilder> {
	private final Config config;
	
	protected AbstractSpellSheetWriter(SpellBaseExcelBuilder builder, Config config) {
		super(builder);
		this.config = config;
	}

	protected void writeIdNameHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
	}

	protected void writeIdName(Spell spell) {
		setValue(spell.getId().value());
		setValue(spell.getName());
	}

	protected void writeSpellInfoHeader() {
		writeCastInfoHeader();
		setHeader(COOLDOWN);
		setHeader(RANGE);
		setHeader(BOLT);
		setHeader(EFFECT_REMOVED_ON_HIT);
		writeDirectComponentHeader();
		writeEffectApplicationHeader();
		writeIconAndTooltipHeader();
	}

	protected void writeSpellInfo(Spell spell) {
		if (spell instanceof Ability ability) {
			writeCastInfo(ability.getCastInfo());
			setValue(ability.getCooldown(), Duration.ZERO);
			setValue(ability.getRange());
			setValue(ability.isBolt());
			setValue(ability.getEffectRemovedOnHit());
		} else {
			writeCastInfo(null);
			setValue(spell.getCooldown(), Duration.ZERO);
			setValue((String) null);
			setValue(spell.isBolt());
			setValue((String) null);
		}
		writeDirectComponent(spell);
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

		if (!castInfo.channeled()) {
			setValue(castInfo.castTime());
		} else {
			setValue((Duration) null);
		}

		setValue(castInfo.channeled());
		setValue(castInfo.ignoresGcd());
	}

	private void writeDirectComponentHeader() {
		for (int i = 1; i <= config.maxDirectCommands(); ++i) {
			writeDirectCommandHeader(i, i == 1);
		}
	}

	private void writeDirectCommandHeader(int idx, boolean writeBonus) {
		var prefix = getDirectCommandPrefix(idx);
		setHeader(TARGET, prefix);
		setHeader(DIRECT_TYPE, prefix);
		setHeader(COEFF_VALUE, prefix);
		setHeader(COEFF_SCHOOL, prefix);
		setHeader(DIRECT_MIN, prefix);
		setHeader(DIRECT_MAX, prefix);
		if (writeBonus) {
			writeBonusHeader(prefix);
		}
	}

	private void writeDirectComponent(Spell spell) {
		writeDirectCommands(spell.getDirectCommands());
	}

	private void writeDirectCommands(List<DirectCommand> commands) {
		assertSizeNoLargerThan("direct commands", commands, config.maxDirectCommands());

		for (int i = 0; i < config.maxDirectCommands(); ++i) {
			var writeBonus = i == 0;

			if (i < commands.size()) {
				writeDirectCommand(commands.get(i), writeBonus);
			} else {
				writeDirectCommand(null, writeBonus);
			}
		}
	}

	private void writeDirectCommand(DirectCommand command, boolean writeBonus) {
		if (command == null) {
			fillRemainingEmptyCols(writeBonus ? 10 : 7);
			return;
		}

		setValue(command.target());
		setValue(command.type());
		setValue(command.coefficient().value());
		setValue(command.school());
		setValue(command.min());
		setValue(command.max());
		if (writeBonus) {
			writeBonus(command.bonus());
		} else if (command.bonus() != null) {
			throw new IllegalArgumentException();
		}
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
		setValue(effectApplication.effect().getId().value());
		setValue(effectApplication.duration());
		setValue(effectApplication.numStacks());
		setValue(effectApplication.numCharges());
	}
}
