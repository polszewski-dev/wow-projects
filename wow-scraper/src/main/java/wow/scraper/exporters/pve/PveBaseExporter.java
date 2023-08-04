package wow.scraper.exporters.pve;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporters.ExcelExporter;
import wow.scraper.exporters.pve.excel.PveBaseExcelBuilder;
import wow.scraper.model.HasRequiredVersion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public abstract class PveBaseExporter<T extends HasRequiredVersion> extends ExcelExporter<PveBaseExcelBuilder> {
	@Override
	public void exportAll() {
		addHeader();

		getSortedData().forEach(this::addRow);
	}

	protected abstract void addHeader();

	protected abstract void addRow(T row);

	protected abstract List<T> getData(GameVersionId gameVersion);

	protected abstract void fixData(List<T> data);

	protected abstract Comparator<T> getComparator();

	private List<T> getSortedData() {
		List<T> data = getAllData();
		fixData(data);
		data.sort(getComparator());
		return data;
	}

	private List<T> getAllData() {
		List<T> data = new ArrayList<>();

		for (GameVersionId gameVersion : getScraperConfig().getGameVersions()) {
			List<T> versionData = getData(gameVersion);
			versionData.forEach(x -> x.setReqVersion(gameVersion));
			data.addAll(versionData);
		}

		return data;
	}
}
