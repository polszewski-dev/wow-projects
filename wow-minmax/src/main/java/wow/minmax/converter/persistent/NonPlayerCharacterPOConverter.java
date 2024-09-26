package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.repository.CharacterRepository;
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
	private final CharacterRepository characterRepository;
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
		var phase = characterRepository.getPhase(source.getPhaseId()).orElseThrow();
		var characterClass = phase.getGameVersion().getCharacterClass(source.getCharacterClassId());
		var combatRatingInfo = characterRepository.getCombatRatingInfo(phase.getGameVersionId(), source.getLevel()).orElseThrow();

		return new NonPlayerCharacterImpl(
				phase,
				characterClass,
				source.getCreatureType(),
				source.getLevel(),
				combatRatingInfo
		);
	}
}
