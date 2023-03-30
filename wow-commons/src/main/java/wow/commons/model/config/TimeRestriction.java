package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static wow.commons.util.CollectionUtil.*;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Getter
@Builder
public class TimeRestriction {
	@NonNull
	@Builder.Default
	private List<GameVersionId> versions = List.of();
	private PhaseId phaseId;

	public static final TimeRestriction EMPTY = builder().build();

	public boolean isMetBy(PhaseId phaseId) {
		if (!this.versions.isEmpty() && !this.versions.contains(phaseId.getGameVersionId())) {
			return false;
		}
		return this.phaseId == null || this.phaseId.isEarlierOrTheSame(phaseId) && this.phaseId.isTheSameVersion(phaseId);
	}

	public TimeRestriction merge(TimeRestriction other) {
		return builder()
				.versions(mergeCriteria(versions, other.versions))
				.phaseId(mergeValues(phaseId, other.phaseId))
				.build();
	}

	public GameVersionId getUniqueVersion() {
		return getUniqueResult(versions).orElseThrow();
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (!versions.isEmpty()) {
			parts.add(String.format("versions: %s", versions));
		}
		if (phaseId != null) {
			parts.add(String.format("phase: %s", phaseId));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
