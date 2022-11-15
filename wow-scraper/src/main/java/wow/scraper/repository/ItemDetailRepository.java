package wow.scraper.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import wow.commons.model.pve.GameVersion;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
public class ItemDetailRepository {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static boolean hasItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId) {
		Path path = getPath(gameVersion, category, itemId);
		return Files.exists(path);
	}

	public static Optional<JsonItemDetailsAndTooltip> getDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId) throws IOException {
		Path path = getPath(gameVersion, category, itemId);
		if (!Files.exists(path)) {
			return Optional.empty();
		}
		var detailsAndTooltip = MAPPER.readValue(new FileInputStream(path.toFile()), JsonItemDetailsAndTooltip.class);
		return Optional.of(detailsAndTooltip);
	}

	public static void saveItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId, JsonItemDetailsAndTooltip detailsAndTooltip) throws IOException {
		Path path = getPath(gameVersion, category, itemId);
		path.toFile().getParentFile().mkdirs();
		MAPPER.writeValue(new FileOutputStream(path.toFile()), detailsAndTooltip);
	}

	public static List<Integer> getItemIds(GameVersion gameVersion, WowheadItemCategory category) {
		File[] files = getPath(gameVersion, category, 0).getParent().toFile().listFiles();
		if (files == null) {
			return List.of();
		}
		return Stream.of(files)
				.map(file -> Integer.parseInt(file.getName()))
				.collect(Collectors.toList());
	}

	private static Path getPath(GameVersion gameVersion, WowheadItemCategory category, int itemId) {
		return Paths.get("scraper", "items", gameVersion.toString().toLowerCase(), category.name().toLowerCase(), Integer.toString(itemId));
	}

	private ItemDetailRepository() {}
}
