package wow.scraper.exporters.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonBossDetails;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class BossExporter extends PveBaseExporter<JsonBossDetails> {
	@Override
	protected void addHeader() {
		builder.addBossHeader();
	}

	@Override
	protected void addRow(JsonBossDetails boss) {
		builder.add(boss);
	}

	@Override
	protected List<JsonBossDetails> getData(GameVersionId gameVersion) throws IOException {
		return getBossDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData(List<JsonBossDetails> bosses) {
		bosses.forEach(this::fixLocation);
	}

	@Override
	protected Comparator<JsonBossDetails> getComparator() {
		return Comparator.comparing(JsonBossDetails::getName);
	}

	private void fixLocation(JsonBossDetails boss) {
		boss.getLocation().remove(Integer.valueOf(1417));
		if (List.of(14887, 14888, 14889, 14890).contains(boss.getId())) {
			boss.setLocation(List.of(10, 47, 331, 357));
		}
	}
}
