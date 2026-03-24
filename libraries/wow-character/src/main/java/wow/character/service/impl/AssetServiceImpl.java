package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.asset.Asset;
import wow.character.model.asset.AssetExecution;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static wow.character.model.asset.Asset.Scope.*;

/**
 * User: POlszewski
 * Date: 2026-03-11
 */
@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {
	@Override
	public <P extends PlayerCharacter> List<AssetExecution<P>> getAssetExecutionPlan(Raid<P> raid) {
		var personalExecutions = getPersonalExecutions(raid);
		var partyExecutions = getPartyExecutions(raid);
		var raidExecutions = getRaidExecutions(raid);

		var result = new ArrayList<AssetExecution<P>>();

		result.addAll(personalExecutions);
		result.addAll(partyExecutions);
		result.addAll(raidExecutions);

		return result;
	}

	private <P extends PlayerCharacter> List<AssetExecution<P>> getPersonalExecutions(Raid<P> raid) {
		var execFactory = new ExecutionFactory<P>();

		return raid.getMembers().stream()
				.map(member -> execFactory.getExecutions(List.of(member), PERSONAL))
				.flatMap(List::stream)
				.toList();
	}

	private <P extends PlayerCharacter> List<AssetExecution<P>> getPartyExecutions(Raid<P> raid) {
		var execFactory = new ExecutionFactory<P>();

		return raid.getParties().stream()
				.map(party -> execFactory.getExecutions(party.getMembers(), PARTY))
				.flatMap(List::stream)
				.toList();
	}

	private <P extends PlayerCharacter> List<AssetExecution<P>> getRaidExecutions(Raid<P> raid) {
		var execFactory = new ExecutionFactory<P>();

		return execFactory.getExecutions(raid.getMembers(), RAID);
	}

	private static class ExecutionFactory<P extends PlayerCharacter> {
		private int indexGenerator;
		private final Map<AssetExecution<P>, Integer> executionToIndex = new HashMap<>();

		List<AssetExecution<P>> getExecutions(List<P> players, Asset.Scope scope) {
			var executions = players.stream()
					.map(player -> getExecutions(player, scope))
					.flatMap(List::stream)
					.toList();

			if (scope == PARTY || scope == RAID) {
				executions = removeInferiorExecutions(executions);
			}

			return executions;
		}

		private List<AssetExecution<P>> getExecutions(P player, Asset.Scope scope) {
			return player.getAssets().getList().stream()
					.filter(asset -> asset.scope() == scope)
					.map(asset -> newExecution(player, asset))
					.toList();
		}

		private AssetExecution<P> newExecution(P player, Asset asset) {
			var execution = new AssetExecution<>(player, asset);
			int index = indexGenerator++;

			executionToIndex.put(execution, index);

			return execution;
		}

		private List<AssetExecution<P>> removeInferiorExecutions(List<AssetExecution<P>> executions) {
			var byName = executions.stream().collect(groupingBy(
					AssetExecution::name,
					LinkedHashMap::new,
					toCollection(ArrayList::new)
			));

			var comparator = Comparator.<AssetExecution<P>>
					comparingInt(AssetExecution::effectiveRank)
					.reversed()
					.thenComparing(this::getIndex);

			byName.values().forEach(
					list -> list.sort(comparator)
			);

			return byName.values().stream()
					.map(List::getFirst)
					.toList();
		}

		private int getIndex(AssetExecution<P> execution) {
			return executionToIndex.get(execution);
		}
	}
}
