package wow.scraper.exporters.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonNpcDetails;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class NpcExporter extends PveBaseExporter<JsonNpcDetails> {
	@Override
	protected void addHeader() {
		builder.addNpcHeader();
	}

	@Override
	protected void addRow(JsonNpcDetails npc) {
		builder.add(npc);
	}

	@Override
	protected List<JsonNpcDetails> getData(GameVersionId gameVersion) throws IOException {
		return getNpcDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData(List<JsonNpcDetails> npcs) {
		npcs.forEach(this::fixLocation);
	}

	@Override
	protected Comparator<JsonNpcDetails> getComparator() {
		return Comparator.comparing(JsonNpcDetails::getName);
	}

	private void fixLocation(JsonNpcDetails npc) {
		npc.getLocation().remove(Integer.valueOf(1417));
		if (List.of(14887, 14888, 14889, 14890).contains(npc.getId())) {
			npc.setLocation(List.of(10, 47, 331, 357));
		}
	}
}
