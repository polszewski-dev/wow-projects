package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.sources.Source;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Component
@AllArgsConstructor
public class SourceConverter {
	public String getSources(AbstractItem item) {
		return item.getSources().stream()
				.map(this::getSourceString)
				.distinct()
				.collect(Collectors.joining(", "));
	}

	private String getSourceString(Source source) {
		if (source.getZone() != null) {
			return source.getZone().getShortName();
		}
		if (source.isBadgeVendor()) {
			return "BoJ";
		}
		if (source.isPvP()) {
			return "PvP";
		}
		if (source.isCrafted()) {
			return source.toString();
		}
		if (source.isReputationReward()) {
			return source.getFaction().getName();
		}
		if (source.isTraded()) {
			return getSources(source.getSourceItem());
		}
		return source.toString();
	}
}