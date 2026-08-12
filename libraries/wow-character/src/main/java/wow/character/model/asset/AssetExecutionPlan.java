package wow.character.model.asset;

import wow.character.model.character.PlayerCharacter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * User: POlszewski
 * Date: 12.08.2026
 */
public record AssetExecutionPlan<P extends PlayerCharacter>(
		List<AssetExecution<P>> personalExecutions,
		List<AssetExecution<P>> partyExecutions,
		List<AssetExecution<P>> raidExecutions
) {
	public AssetExecutionPlan {
		Objects.requireNonNull(personalExecutions);
		Objects.requireNonNull(partyExecutions);
		Objects.requireNonNull(partyExecutions);
	}

	public List<AssetExecution<P>> buffs() {
		return Stream.concat(
				personalExecutions.stream(),
				Stream.concat(
						partyExecutions.stream(),
						raidExecutions.stream()
				)
		).toList();
	}

	public Map<P, List<AssetExecution<P>>> buffsByPlayer() {
		return buffs().stream().collect(groupingBy(
				AssetExecution::player
		));
	}
}
