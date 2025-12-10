package wow.character.model.asset;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.character.model.asset.AssetOption.OneOfManyOption;
import static wow.character.model.asset.AssetOption.SingleOption;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
public record AssetTemplate(
		String name,
		CharacterClassId characterClassId,
		GameVersionId gameVersionId,
		List<AssetOption> options
) {
	public AssetTemplate {
		Objects.requireNonNull(name);
		Objects.requireNonNull(characterClassId);
		Objects.requireNonNull(gameVersionId);
	}

	public Optional<SingleOption> getOption(String name) {
		return getSingleOptionStream()
				.filter(x -> x.name().equals(name))
				.findAny();
	}

	public List<SingleOption> getDefaultOptions() {
		return getSingleOptionStream()
				.filter(SingleOption::isDefault)
				.toList();
	}

	private Stream<SingleOption> getSingleOptionStream() {
		return options.stream()
				.map(AssetTemplate::getSingleOptions)
				.flatMap(List::stream);
	}

	private static List<SingleOption> getSingleOptions(AssetOption option) {
		return switch (option) {
			case OneOfManyOption oneOfManyOption -> oneOfManyOption.list();
			case SingleOption singleOption -> List.of(singleOption);
		};
	}
}
