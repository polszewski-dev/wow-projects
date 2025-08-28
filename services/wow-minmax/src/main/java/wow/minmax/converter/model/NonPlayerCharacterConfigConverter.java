package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.repository.CombatRatingInfoRepository;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.model.NonPlayerCharacterConfig;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class NonPlayerCharacterConfigConverter implements Converter<NonPlayerCharacter, NonPlayerCharacterConfig>, BackConverter<NonPlayerCharacter, NonPlayerCharacterConfig> {
	private final PhaseRepository phaseRepository;
	private final CharacterClassRepository characterClassRepository;
	private final CombatRatingInfoRepository combatRatingInfoRepository;
	private final BuffConfigConverter buffConfigConverter;

	@Override
	public NonPlayerCharacterConfig doConvert(NonPlayerCharacter source) {
		return new NonPlayerCharacterConfig(
				source.getName(),
				source.getPhaseId(),
				source.getCharacterClassId(),
				source.getCreatureType(),
				source.getLevel(),
				buffConfigConverter.convertList(source.getBuffs().getList())
		);
	}

	@Override
	public NonPlayerCharacter doConvertBack(NonPlayerCharacterConfig source) {
		var phase = phaseRepository.getPhase(source.getPhaseId()).orElseThrow();
		var characterClass = characterClassRepository.getCharacterClass(source.getCharacterClassId(), phase.getGameVersionId()).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(phase.getGameVersionId(), source.getLevel()).orElseThrow();

		return new NonPlayerCharacterImpl(
				source.getName(),
				phase,
				characterClass,
				source.getCreatureType(),
				source.getLevel(),
				combatRatingInfo
		);
	}
}
