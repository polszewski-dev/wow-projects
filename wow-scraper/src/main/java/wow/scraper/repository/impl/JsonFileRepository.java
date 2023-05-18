package wow.scraper.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import wow.scraper.config.ScraperConfig;

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
 * Date: 2023-05-19
 */
@RequiredArgsConstructor
public class JsonFileRepository<T> {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final ScraperConfig config;
	private final Class<T> valueClass;

	public boolean has(String[] idParts) {
		Path path = getPath(idParts);
		return Files.exists(path);
	}

	public Optional<T> get(String[] idParts) throws IOException {
		Path path = getPath(idParts);
		if (!Files.exists(path)) {
			return Optional.empty();
		}
		var value = MAPPER.readValue(new FileInputStream(path.toFile()), valueClass);
		return Optional.of(value);
	}

	public void save(String[] idParts, T value) throws IOException {
		Path path = getPath(idParts);
		path.toFile().getParentFile().mkdirs();
		MAPPER.writeValue(new FileOutputStream(path.toFile()), value);
	}

	public List<String> getIds(String[] idParts) {
		File[] files = getPath(idParts)
				.getParent()
				.toFile()
				.listFiles();

		if (files == null) {
			return List.of();
		}

		return Stream.of(files)
				.map(File::getName)
				.toList();
	}

	private Path getPath(String[] idParts) {
		return Paths.get(config.getDirectoryPath(), idParts);
	}
}
