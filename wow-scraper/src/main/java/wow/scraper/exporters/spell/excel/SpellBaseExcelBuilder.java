package wow.scraper.exporters.spell.excel;

import wow.commons.model.talents.TalentTree;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporters.excel.WowExcelBuilder;
import wow.scraper.parsers.tooltip.AbilityTooltipParser;
import wow.scraper.parsers.tooltip.TalentTooltipParser;

import static wow.commons.repository.impl.parsers.spells.SpellBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class SpellBaseExcelBuilder extends WowExcelBuilder {
	private final AbilitySheetWriter abilitySheetWriter;
	private final AbilityRankSheetWriter abilityRankSheetWriter;
	private final TalentSheetWriter talentSheetWriter;
	private final TalentRankSheetWriter talentRankSheetWriter;

	private TalentTree currentTree;
	private String currentName;

	public SpellBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.abilitySheetWriter = new AbilitySheetWriter(this);
		this.abilityRankSheetWriter = new AbilityRankSheetWriter(this);
		this.talentSheetWriter = new TalentSheetWriter(this);
		this.talentRankSheetWriter = new TalentRankSheetWriter(this);
	}

	public void addAbilityHeader() {
		writeHeader(SPELLS, abilitySheetWriter, 0, 1);
		resetSeparators();
	}

	public void addAbility(AbilityTooltipParser parser) {
		separateByTree(parser.getTalentTree());
		writeRow(parser, abilitySheetWriter);
	}

	public void addAbilityRankHeader() {
		writeHeader(SPELL_RANKS, abilityRankSheetWriter, 0, 1);
		resetSeparators();
	}

	public void addAbilityRank(AbilityTooltipParser parser) {
		separateByName(parser.getName());
		writeRow(parser, abilityRankSheetWriter);
	}

	public void addTalentHeader() {
		writeHeader(TALENTS, talentSheetWriter, 0, 1);
		resetSeparators();
	}

	public void addTalent(TalentTooltipParser parser) {
		separateByTree(parser.getTalentTree());
		writeRow(parser, talentSheetWriter);
	}

	public void addTalentRankHeader() {
		writeHeader(TALENT_RANKS, talentRankSheetWriter, 0, 1);
		resetSeparators();
	}

	public void addTalentRank(TalentTooltipParser parser) {
		separateByName(parser.getName());
		writeRow(parser, talentRankSheetWriter);
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
