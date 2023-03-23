package wow.scraper.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersion;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemCategory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
@AllArgsConstructor
public class ItemDetailRepositoryImpl implements wow.scraper.repository.ItemDetailRepository {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private ScraperConfig config;

	@Override
	public boolean hasItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId) {
		Path path = getPath(gameVersion, category, itemId);
		return Files.exists(path);
	}

	@Override
	public Optional<JsonItemDetailsAndTooltip> getDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId) throws IOException {
		Path path = getPath(gameVersion, category, itemId);
		if (!Files.exists(path)) {
			return Optional.empty();
		}
		var detailsAndTooltip = MAPPER.readValue(new FileInputStream(path.toFile()), JsonItemDetailsAndTooltip.class);
		return Optional.of(detailsAndTooltip);
	}

	@Override
	public void saveItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId, JsonItemDetailsAndTooltip detailsAndTooltip) throws IOException {
		Path path = getPath(gameVersion, category, itemId);
		path.toFile().getParentFile().mkdirs();
		MAPPER.writeValue(new FileOutputStream(path.toFile()), detailsAndTooltip);
	}

	@Override
	public List<Integer> getItemIds(GameVersion gameVersion, WowheadItemCategory category) {
		File[] files = getPath(gameVersion, category, 0).getParent().toFile().listFiles();
		if (files == null) {
			return List.of();
		}
		return Stream.of(files)
				.map(file -> Integer.parseInt(file.getName()))
				.toList();
	}

	private Path getPath(GameVersion gameVersion, WowheadItemCategory category, int itemId) {
		return Paths.get(config.getDirectoryPath(), "items", gameVersion.toString().toLowerCase(), category.name().toLowerCase(), Integer.toString(itemId));
	}
}
