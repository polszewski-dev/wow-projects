package wow.minmax.repository.impl.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 02.07.2026
 */
public abstract class AbstractFileRepository<T> {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String directory;
	private final Class<T> valueClass;

	protected AbstractFileRepository(String directory, Class<T> valueClass) {
		objectMapper.findAndRegisterModules();

		this.directory = directory;
		this.valueClass = valueClass;

		getDirectoryPath().toFile().mkdirs();
	}

	@SneakyThrows
	protected List<T> readAllFromJsonFiles() {
		var path = getDirectoryPath();

		try (var stream = Files.list(path)) {
			return stream
					.map(this::readFromJsonFile)
					.flatMap(Optional::stream)
					.toList();
		}
	}

	protected Optional<T> readFromJsonFile(String id) {
		var path = getFilePath(id);

		return readFromJsonFile(path);
	}

	@SneakyThrows
	private Optional<T> readFromJsonFile(Path path) {
		var file = path.toFile();

		if (!file.exists()) {
			return Optional.empty();
		}

		var value = objectMapper.readValue(file, valueClass);

		return Optional.of(value);
	}

	@SneakyThrows
	protected T saveToJsonFile(String id, T value) {
		var path = getFilePath(id);

		objectMapper.writeValue(path.toFile(), value);
		return value;
	}

	private Path getDirectoryPath() {
		return Path.of(directory);
	}

	private Path getFilePath(String id) {
		return Path.of(getDirectoryPath().toString(), id);
	}
}
