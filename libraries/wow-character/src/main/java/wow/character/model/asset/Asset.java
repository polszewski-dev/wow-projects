package wow.character.model.asset;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wow.character.model.asset.AssetOption.SingleOption;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
@Getter
@Setter
public class Asset {
	private final AssetTemplate template;
	private RaceId raceId;
	private final Map<String, SingleOption> selectedOptions = new HashMap<>();

	public Asset(AssetTemplate template, RaceId raceId) {
		this.template = template;
		this.raceId = raceId;
		selectDefaultOptions();
	}

	public String name() {
		return template.name();
	}

	public GameVersionId gameVersionId() {
		return template.gameVersionId();
	}

	public CharacterClassId characterClassId() {
		return template.characterClassId();
	}

	public List<SingleOption> getSelectedOptions() {
		return List.copyOf(selectedOptions.values());
	}

	public void selectOption(String name) {
		var option = template.getOption(name).orElseThrow();

		selectOption(option);
	}

	private void selectOption(SingleOption option) {
		var key = getOptionKey(option);

		selectedOptions.put(key, option);
	}

	private void selectDefaultOptions() {
		for (var option : template.getDefaultOptions()) {
			selectOption(option);
		}
	}

	private String getOptionKey(SingleOption option) {
		if (option.exclusionGroup() != null) {
			return option.exclusionGroup().toString();
		}
		return option.name();
	}
}
