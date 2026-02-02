package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.ItemSource;

import java.util.stream.Collectors;

import static wow.commons.model.item.ItemSource.*;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Component
@AllArgsConstructor
public class ItemSourceConverter {
	public String getSources(AbstractItem<?> item) {
		return item.getSources().stream()
				.map(this::getSourceString)
				.distinct()
				.collect(Collectors.joining(", "));
	}

	public String getDetailedSources(AbstractItem<?> item) {
		return item.getSources().stream()
				.map(this::getDetailedSourceString)
				.distinct()
				.collect(Collectors.joining(", "));
	}

	private String getDetailedSourceString(ItemSource source) {
		if (source.isNpcDrop()) {
			return String.format("%s - %s", source.zoneShortNames(), source.npc().getName());
		}
		if (source.isTraded()) {
			return getDetailedSources(source.sourceItem());
		}
		return getSourceString(source);
	}

	private String getSourceString(ItemSource source) {
		if (!source.zones().isEmpty()) {
			return source.zoneShortNames();
		}

		return switch (source) {
			case BadgeVendor() ->
					"BoJ";
			case ContainedInItem(var ignored, var name) ->
					name;
			case ContainedInObject(var ignored, var name, var ignored2) ->
					name;
			case Crafted(var professionId) ->
					professionId.getName();
			case NpcDrop(var npc, var ignored) ->
					"Npc: " + npc;
			case PvP() ->
					"PvP";
			case QuestReward(var ignored, var questName) ->
					questName != null ? "Quest: " + questName : "Quest";
			case ReputationReward(var faction) ->
					faction.getName();
			case Traded(var sourceItem) ->
					getSources(sourceItem);
			case WorldDrop() ->
					"WorldDrop";
			case ZoneDrop ignored ->
					"Trash: " + source.zoneShortNames();
		};
	}
}
