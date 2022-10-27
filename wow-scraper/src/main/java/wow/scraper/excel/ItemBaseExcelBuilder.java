package wow.scraper.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.professions.Profession;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemQuality;
import wow.scraper.parsers.GemTooltipParser;
import wow.scraper.parsers.ItemTooltipParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public class ItemBaseExcelBuilder extends AbstractExcelBuilder {
	private static final int MAX_STATS = 10;
	private static final int MAX_BONUSES = 6;
	private static final int MAX_PIECES = 9;

	@Override
	public void start() {
		writer.open("item");
		writeItemHeader();
	}

	public void timeForGems() {
		writer.nextSheet("gem");
		writeGemHeader();
	}

	@Override
	public void finish(String fileName) throws IOException {
		writer.nextSheet("set");
		writeItemSetHeader();
		savedSets.values().forEach(this::writeItemSetRow);
		super.finish(fileName);
	}

	public void add(ItemTooltipParser parser, JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		// TODO skipping this for now
		if (parser.isRandomEnchantment()) {
			return;
		}

		writeItemRow(parser, itemDetailsAndTooltip);
		saveSetInfo(parser);
	}

	public void add(GemTooltipParser parser, JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		writeGemRow(parser, itemDetailsAndTooltip);
	}

	private void writeItemHeader() {
		setHeader("id");
		setHeader("name", 50);
		setHeader("rarity");
		setHeader("item_level");
		setHeader("req_level");
		setHeader("binding");
		setHeader("unique");
		setHeader("item_type");
		setHeader("item_subtype");
		setHeader("phase");
		setHeader("socket_types");
		setHeader("socket_bonus");
		setHeader("class_restriction");
		setHeader("race_restriction");
		setHeader("side_restriction");
		setHeader("item_set");
		setHeader("req_profession");
		setHeader("req_profession_level");
		setHeader("req_profession_spec");
		setHeader("dropped_by");
		setHeader("drop_chance");
		setHeader("sell_price");

		for (int i = 1; i <= MAX_STATS; ++i) {
			setHeader("stat" + i);;
		}

		setHeader("icon");
		setHeader("tooltip");

		writer.nextRow().freeze(0, 1);
	}

	private void writeItemRow(ItemTooltipParser parser, JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		ItemRarity rarity = getItemRarity(itemDetailsAndTooltip);

		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(rarity);
		setValue(parser.getItemLevel());
		setValue(parser.getRequiredLevel());
		setValue(parser.getBinding());
		setValue(parser.isUnique() ? 1 : 0);
		setValue((parser.getItemType()));
		setValue(parser.getItemSubType() != null ? parser.getItemSubType().toString() : null);
		setValue(parser.getPhase());
		setValue(parser.getSocketTypes());
		setValue(parser.getSocketBonus());
		setValue(parser.getClassRestriction());
		setValue(parser.getRaceRestriction());
		setValue(parser.getSideRestriction());
		setValue(parser.getItemSetName());
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(parser.getRequiredProfessionSpec());
		setValue(parser.getDroppedBy());
		setValue(parser.getDropChance());
		setValue(parser.getSellPrice());
		setList(parser.getStatLines(), MAX_STATS);
		setValue(itemDetailsAndTooltip.getIcon());
		setValue(itemDetailsAndTooltip.getHtmlTooltip().trim());

		writer.nextRow();
	}

	@Data
	@AllArgsConstructor
	private static class SetInfo {
		private String itemSetName;
		private List<String> itemSetPieces;
		private List<ItemSetBonus> itemSetBonuses;
		private Profession itemSetRequiredProfession;
		private Integer itemSetRequiredProfessionLevel;
	}

	private final Map<String, SetInfo> savedSets = new TreeMap<>();

	private void saveSetInfo(ItemTooltipParser parser) {
		if (parser.getItemSetName() == null || savedSets.containsKey(parser.getItemSetName())) {
			return;
		}

		savedSets.put(parser.getItemSetName(), new SetInfo(
				parser.getItemSetName(),
				parser.getItemSetPieces(),
				parser.getItemSetBonuses(),
				parser.getItemSetRequiredProfession(),
				parser.getItemSetRequiredProfessionLevel()
		));
	}

	private void writeItemSetHeader() {
		setHeader("name", 50);
		setHeader("#pieces");

		for (int i = 1; i <= MAX_PIECES; ++i) {
			setHeader("item" + i);
		}

		for (int i = 1; i <= MAX_BONUSES; ++i) {
			setHeader("bonus" + i + "_p");
			setHeader("bonus" + i + "_d");
		}

		setHeader("req_prof");
		setHeader("req_prof_lvl");

		writer.nextRow().freeze(0, 1);
	}

	private void writeItemSetRow(SetInfo setInfo) {
		setValue(setInfo.getItemSetName());
		setValue(setInfo.getItemSetPieces().size());
		setList(setInfo.getItemSetPieces(), MAX_PIECES);
		setList(setInfo.getItemSetBonuses(), MAX_BONUSES, x -> {
			setValue(x.getNumPieces());
			setValue(x.getDescription());
		}, 2);
		setValue(setInfo.getItemSetRequiredProfession());
		setValue(setInfo.getItemSetRequiredProfessionLevel());
		writer.nextRow();
	}

	private void writeGemHeader() {
		setHeader("id");
		setHeader("name", 50);
		setHeader("rarity");
		setHeader("item_level");
		setHeader("phase");
		setHeader("color");
		setHeader("binding");
		setHeader("unique");
		setHeader("stats");
		setHeader("meta_enablers");
		setHeader("sell_price");
		setHeader("icon");
		setHeader("tooltip");
		writer.nextRow();
	}

	private void writeGemRow(GemTooltipParser parser, JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		setValue(parser.getItemId());
		setValue(parser.getName());
		setValue(getItemRarity(itemDetailsAndTooltip));
		setValue(parser.getItemLevel());
		setValue(parser.getPhase());
		setValue(parser.getColor());
		setValue(parser.getBinding());
		setValue(parser.isUnique() ? 1 : 0);
		setValue(parser.getStatLines().get(0));
		setValue(parser.getMetaEnablers());
		setValue(parser.getSellPrice());
		setValue(itemDetailsAndTooltip.getIcon());
		setValue(itemDetailsAndTooltip.getHtmlTooltip().trim());
		if (parser.getStatLines().size() != 1) {
			throw new IllegalArgumentException("Only 1 stat line allowed");
		}
		writer.nextRow();
	}

	private static ItemRarity getItemRarity(JsonItemDetailsAndTooltip itemDetailsAndTooltip) {
		Integer quality = itemDetailsAndTooltip.getDetails().getQuality();
		return WowheadItemQuality.fromCode(quality).getItemRarity();
	}
}
