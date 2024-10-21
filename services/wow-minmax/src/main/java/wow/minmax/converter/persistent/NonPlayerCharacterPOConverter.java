package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.repository.CombatRatingInfoRepository;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.NonPlayerCharacterPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class NonPlayerCharacterPOConverter implements Converter<NonPlayerCharacter, NonPlayerCharacterPO>, ParametrizedBackConverter<NonPlayerCharacter, NonPlayerCharacterPO> {
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
	public NonPlayerCharacter doConvertBack(NonPlayerCharacterPO source, Map<String, Object> params) {
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
