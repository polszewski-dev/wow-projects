package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Profession;
import wow.character.repository.CharacterRepository;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.sources.Source;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Component
@AllArgsConstructor
public class SourceConverter {
	private final CharacterRepository characterRepository;

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
			return String.format("%s - %s", source.getZoneShortNames(), source.getNpc().getName());
		}
		if (source.isTraded()) {
			return getDetailedSources(source.getSourceItem());
		}
		return getSourceString(source, item);
	}

	private String getSourceString(Source source, AbstractItem item) {
		if (!source.getZones().isEmpty()) {
			return source.getZoneShortNames();
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
			return source.getFaction().getName();
		}
		if (source.isTraded()) {
			return getSources(source.getSourceItem());
		}
		return source.toString();
	}

	private Profession getProfession(Source source, AbstractItem item) {
		GameVersionId gameVersionId = item.getTimeRestriction().getUniqueVersion();
		GameVersion gameVersion = characterRepository.getGameVersion(gameVersionId).orElseThrow();
		return gameVersion.getProfession(source.getProfessionId());
	}
}
