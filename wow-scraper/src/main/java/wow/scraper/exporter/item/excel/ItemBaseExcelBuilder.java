package wow.scraper.exporter.item.excel;

import lombok.Getter;
import lombok.SneakyThrows;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.WowheadSpellCategory;
import wow.scraper.parser.tooltip.EnchantTooltipParser;
import wow.scraper.parser.tooltip.GemTooltipParser;
import wow.scraper.parser.tooltip.ItemTooltipParser;
import wow.scraper.parser.tooltip.TradedItemParser;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemBaseExcelBuilder extends WowExcelBuilder {
	private final SavedSets savedSets = new SavedSets();

	private final TradedItemSheetWriter tradedItemSheetWriter;
	private final ItemSheetWriter itemSheetWriter;
	private final EnchantSheetWriter enchantSheetWriter;
	private final GemSheetWriter gemSheetWriter;
	private final ItemSetSheetWriter itemSetSheetWriter;

	public ItemBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.tradedItemSheetWriter = new TradedItemSheetWriter(this);
		this.itemSheetWriter = new ItemSheetWriter(this);
		this.enchantSheetWriter = new EnchantSheetWriter(this);
		this.gemSheetWriter = new GemSheetWriter(this);
		this.itemSetSheetWriter = new ItemSetSheetWriter(this);
	}

	public void addTradedItemHeader() {
		writeHeader(TRADE, tradedItemSheetWriter, 2, 1);
	}

	public void addItemHeader() {
		writeHeader(ITEM, itemSheetWriter, 2, 1);
	}

	public void addEnchantHeader() {
		flushItems();
		writeHeader(ENCHANT, enchantSheetWriter, 2, 1);
	}

	public void addGemHeader() {
		writeHeader(GEM, gemSheetWriter, 2, 1);
	}

	@SneakyThrows
	@Override
	public void finish(String fileName) {
		writeHeader(SET, itemSetSheetWriter, 1, 1);
		savedSets.forEach(setInfo -> writeRow(setInfo, itemSetSheetWriter));
		super.finish(fileName);
	}

	public void add(TradedItemParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, tradedItemSheetWriter);
	}

	public void add(ItemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId()) || parser.isRandomEnchantment()) {
			return;
		}
		itemParserQueue.add(parser);
		savedSets.save(parser);
	}

	public void add(EnchantTooltipParser parser) {
		if (isSpellToBeIgnored(parser.getSpellId(), parser.getGameVersion())) {
			return;
		}
		writeRow(parser, enchantSheetWriter);
	}

	public void add(GemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, gemSheetWriter);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}

	private boolean isSpellToBeIgnored(int spellId, GameVersionId gameVersion) {
		return config.isSpellIgnored(spellId, WowheadSpellCategory.ENCHANTS, gameVersion);
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (ItemTooltipParser parser : itemParserQueue) {
			writeRow(parser, itemSheetWriter);
		}
		itemParserQueue.clear();
	}
}
