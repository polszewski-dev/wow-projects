package wow.scraper.exporter.item.excel;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parser.tooltip.EnchantTooltipParser;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ENCHANT;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class EnchantExcelBuilder extends WowExcelBuilder {
	private final EnchantSheetWriter enchantSheetWriter;

	public EnchantExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.enchantSheetWriter = new EnchantSheetWriter(this);
	}

	public void addEnchantHeader() {
		writeHeader(ENCHANT, enchantSheetWriter, 2, 1);
	}

	public void add(EnchantTooltipParser parser) {
		if (isSpellToBeIgnored(parser.getSpellId(), parser.getGameVersion())) {
			return;
		}
		writeRow(parser, enchantSheetWriter);
	}

	private boolean isSpellToBeIgnored(int spellId, GameVersionId gameVersion) {
		return datafixes.isSpellIgnored(spellId, WowheadSpellCategory.ENCHANTS, gameVersion);
	}
}
