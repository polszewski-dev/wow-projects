package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.profession.Profession;
import wow.commons.model.source.Source;
import wow.commons.repository.character.ProfessionRepository;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Component
@AllArgsConstructor
public class SourceConverter {
	private final ProfessionRepository professionRepository;

	public String getSources(AbstractItem item) {
		return item.getSources().stream()
				.map(source -> getSourceString(source, item))
				.distinct()
				.collect(Collectors.joining(", "));
	}

	public String getDetailedSources(AbstractItem item) {
		return item.getSources().stream()
				.map(source -> getDetailedSourceString(source, item))
				.distinct()
				.collect(Collectors.joining(", "));
	}

	private String getDetailedSourceString(Source source, AbstractItem item) {
		if (source.isNpcDrop()) {
			return String.format("%s - %s", source.zoneShortNames(), source.npc().getName());
		}
		if (source.isTraded()) {
			return getDetailedSources(source.sourceItem());
		}
		return getSourceString(source, item);
	}

	private String getSourceString(Source source, AbstractItem item) {
		if (!source.zones().isEmpty()) {
			return source.zoneShortNames();
		}
		if (source.isBadgeVendor()) {
			return "BoJ";
		}
		if (source.isPvP()) {
			return "PvP";
		}
		if (source.isCrafted()) {
			Profession profession = getProfession(source, item);
			return profession.getName();
		}
		if (source.isReputationReward()) {
			return source.faction().getName();
		}
		if (source.isTraded()) {
			return getSources(source.sourceItem());
		}
		return source.toString();
	}

	private Profession getProfession(Source source, AbstractItem item) {
		var gameVersionId = item.getGameVersionId();

		return professionRepository.getProfession(source.professionId(), gameVersionId).orElseThrow();
	}
}
