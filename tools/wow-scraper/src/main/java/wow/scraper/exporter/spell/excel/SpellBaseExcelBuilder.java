package wow.scraper.exporter.spell.excel;

import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentTree;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetConfigs.*;
import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class SpellBaseExcelBuilder extends WowExcelBuilder {
	private final AbilitySheetWriter abilitySheetWriter;
	private final SpellSheetWriter abilitySpellSheetWriter;
	private final EffectSheetWriter abilityEffectSheetWriter;
	private final SpellSheetWriter itemSpellSheetWriter;
	private final EffectSheetWriter itemEffectSheetWriter;

	private TalentTree currentTree;

	public SpellBaseExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.abilitySheetWriter = new AbilitySheetWriter(this, ABILITIES_CONFIG);
		this.abilitySpellSheetWriter = new SpellSheetWriter(this, ABILITY_SPELLS_CONFIG);
		this.abilityEffectSheetWriter = new EffectSheetWriter(this, ABILITY_EFFECTS_CONFIG);
		this.itemSpellSheetWriter = new SpellSheetWriter(this, ITEM_SPELLS_CONFIG);
		this.itemEffectSheetWriter = new EffectSheetWriter(this, ITEM_EFFECTS_CONFIG);
	}

	public void addAbilityHeader() {
		writeHeader(ABILITIES, abilitySheetWriter, 3, 1);
		resetSeparators();
	}

	public void addAbility(ClassAbility ability) {
		separateByTree(ability.getTalentTree());
		writeRow(ability, abilitySheetWriter);
	}

	public void addAbilitySpellHeader() {
		writeHeader(ABILITY_SPELLS, abilitySpellSheetWriter, 2, 1);
		resetSeparators();
	}

	public void addAbilitySpell(Spell spell) {
		writeRow(spell, abilitySpellSheetWriter);
	}

	public void addAbilityEffectHeader() {
		writeHeader(ABILITY_EFFECTS, abilityEffectSheetWriter, 2, 1);
		resetSeparators();
	}

	public void addAbilityEffect(Effect effect) {
		writeRow(effect, abilityEffectSheetWriter);
	}

	public void addItemSpellHeader() {
		writeHeader(ITEM_SPELLS, itemSpellSheetWriter, 2, 1);
	}

	public void addItemSpell(Spell spell) {
		writeRow(spell, itemSpellSheetWriter);
	}

	public void addItemEffectHeader() {
		writeHeader(ITEM_EFFECTS, itemEffectSheetWriter, 2, 1);
	}

	public void addItemEffect(Effect effect) {
		writeRow(effect, itemEffectSheetWriter);
	}

	private void resetSeparators() {
		currentTree = null;
	}

	private void separateByTree(TalentTree tree) {
		if (currentTree != null && tree != currentTree) {
			writer.nextRow();
		}
		currentTree = tree;
	}
}
