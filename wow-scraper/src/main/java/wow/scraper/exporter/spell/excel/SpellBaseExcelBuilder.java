package wow.scraper.exporter.spell.excel;

import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentTree;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.TalentTooltipParser;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelColumnNames.*;
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
	private final TalentSheetWriter talentSheetWriter;

	private TalentTree currentTree;
	private String currentName;

	public SpellBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.abilitySheetWriter = new AbilitySheetWriter(this);
		this.abilitySpellSheetWriter = new SpellSheetWriter(this);
		this.abilityEffectSheetWriter = new EffectSheetWriter(this, MAX_ABILITY_EFFECT_MODIFIER_ATTRIBUTES, MAX_ABILITY_EFFECT_EVENTS);
		this.itemSpellSheetWriter = new SpellSheetWriter(this);
		this.itemEffectSheetWriter = new EffectSheetWriter(this, MAX_ITEM_EFFECT_MODIFIER_ATTRIBUTES, MAX_ITEM_EFFECT_EVENTS);
		this.talentSheetWriter = new TalentSheetWriter(this);
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

	public void addTalentHeader() {
		writeHeader(TALENTS, talentSheetWriter, 2, 1);
		resetSeparators();
	}

	public void addTalent(TalentTooltipParser parser) {
		separateByName(parser.getName());
		writeRow(parser, talentSheetWriter);
	}

	private void resetSeparators() {
		currentTree = null;
		currentName = null;
	}

	private void separateByTree(TalentTree tree) {
		if (currentTree != null && tree != currentTree) {
			writer.nextRow();
		}
		currentTree = tree;
	}

	private void separateByName(String name) {
		if (currentName != null && !name.equals(currentName)) {
			writer.nextRow();
		}
		currentName = name;
	}
}
