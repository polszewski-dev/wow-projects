package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.repository.CombatRatingInfoRepository;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.model.NonPlayerCharacter;
import wow.minmax.model.impl.NonPlayerCharacterImpl;
import wow.minmax.model.persistent.NonPlayerCharacterPO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class NonPlayerCharacterPOConverter implements Converter<NonPlayerCharacter, NonPlayerCharacterPO>, BackConverter<NonPlayerCharacter, NonPlayerCharacterPO> {
	private final PhaseRepository phaseRepository;
	private final CharacterClassRepository characterClassRepository;
	private final CombatRatingInfoRepository combatRatingInfoRepository;
	private final BuffPOConverter buffPOConverter;

	@Override
	public NonPlayerCharacterPO doConvert(NonPlayerCharacter source) {
		return new NonPlayerCharacterPO(
				source.getPhaseId(),
				source.getCharacterClassId(),
				source.getCreatureType(),
				source.getLevel(),
				buffPOConverter.convertList(source.getBuffs().getList())
		);
	}

	@Override
	public NonPlayerCharacter doConvertBack(NonPlayerCharacterPO source) {
		var phase = phaseRepository.getPhase(source.getPhaseId()).orElseThrow();
		var characterClass = characterClassRepository.getCharacterClass(source.getCharacterClassId(), phase.getGameVersionId()).orElseThrow();
		var combatRatingInfo = combatRatingInfoRepository.getCombatRatingInfo(phase.getGameVersionId(), source.getLevel()).orElseThrow();

		return new NonPlayerCharacterImpl(
				phase,
				characterClass,
				source.getCreatureType(),
				source.getLevel(),
				combatRatingInfo
		);
	}
}
