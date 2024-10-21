package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Side;
import wow.commons.model.racial.Racial;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@AllArgsConstructor
@Getter
public class Race implements Described {
	@NonNull
	private final RaceId raceId;

	@NonNull
	private final Description description;

	@NonNull
	private final Side side;

	private final List<Racial> racials = new ArrayList<>();

	@NonNull
	private final GameVersion gameVersion;

	public List<Racial> getRacials(CharacterInfo characterInfo) {
		return racials.stream()
				.filter(x -> x.isAvailableTo(characterInfo))
				.toList();
	}
}
