package wow.minmax.converter.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Talents;
import wow.character.repository.CombatRatingInfoRepository;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.db.NonPlayerConfig;
import wow.minmax.model.impl.NonPlayerImpl;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class NonPlayerConfigConverter implements Converter<NonPlayer, NonPlayerConfig>, BackConverter<NonPlayer, NonPlayerConfig> {
	private final PhaseRepository phaseRepository;
	private final CharacterClassRepository characterClassRepository;
	private final CombatRatingInfoRepository combatRatingInfoRepository;

	@Override
	public NonPlayerConfig doConvert(NonPlayer source) {
		return new NonPlayerConfig(
				source.getName(),
				source.getPhaseId(),
				source.getCharacterClassId(),
				source.getCreatureType(),
				source.getSide(),
				source.getLevel()
		);
	}

	@Override
	public NonPlayer doConvertBack(NonPlayerConfig source) {
		var phase = phaseRepository.getPhase(source.getPhaseId()).orElseThrow();
		var characterClass = characterClassRepository.getCharacterClass(source.getCharacterClassId(), phase.getGameVersionId()).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(phase.getGameVersionId(), source.getLevel()).orElseThrow();
		var talents = new Talents(characterClass.getCharacterClassId(), phase.getPhaseId(), List.of());

		return new NonPlayerImpl(
				source.getName(),
				phase,
				characterClass,
				source.getLevel(),
				source.getCreatureType(),
				source.getSide(),
				combatRatingInfo,
				talents
		);
	}
}
