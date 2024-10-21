package wow.scraper.exporter.pve;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.ExcelExporter;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.HasRequiredVersion;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public abstract class PveBaseExporter<T extends HasRequiredVersion, B extends WowExcelBuilder> extends ExcelExporter<B> {
	protected List<T> data;

	@Override
	protected void prepareData() {
		this.data = getAllData();
		fixData();
		data.sort(getComparator());
	}

	@Override
	protected void exportPreparedData(B builder) {
		addHeader(builder);
		data.forEach(row -> addRow(row, builder));
	}

	protected abstract void addHeader(B builder);

	protected abstract void addRow(T row, B builder);

	protected abstract List<T> getData(GameVersionId gameVersion);

	protected abstract void fixData();

	protected abstract Comparator<T> getComparator();

	private List<T> getAllData() {
		return getScraperConfig().getGameVersions().stream()
				.map(this::getVersionData)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private List<T> getVersionData(GameVersionId gameVersion) {
		var versionData = getData(gameVersion);
		versionData.forEach(x -> x.setReqVersion(gameVersion));
		return versionData;
	}
}
