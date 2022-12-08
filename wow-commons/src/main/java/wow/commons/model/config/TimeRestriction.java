package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static wow.commons.util.CollectionUtil.mergeCriteria;
import static wow.commons.util.CollectionUtil.mergeValues;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Getter
@Builder
public class TimeRestriction {
	@NonNull
	@Builder.Default
	private List<GameVersion> versions = List.of();
	private Phase phase;

	public static final TimeRestriction EMPTY = builder().build();

	public boolean isMetBy(Phase phase) {
		if (!this.versions.isEmpty() && !this.versions.contains(phase.getGameVersion())) {
			return false;
		}
		return this.phase == null || this.phase.isEarlierOrTheSame(phase) && this.phase.isTheSameVersion(phase);
	}

	public TimeRestriction merge(TimeRestriction other) {
		return builder()
				.versions(mergeCriteria(versions, other.versions))
				.phase(mergeValues(phase, other.phase))
				.build();
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (!versions.isEmpty()) {
			parts.add(String.format("versions: %s", versions));
		}
		if (phase != null) {
			parts.add(String.format("phase: %s", phase));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
