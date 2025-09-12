package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
public class NonPlayerConverter extends AbstractNonPlayerConverter<NonPlayerCharacter> {
	public NonPlayerConverter(CharacterService characterService) {
		super(characterService);
	}

	@Override
	protected NonPlayerCharacterFactory<NonPlayerCharacter> getFactory(String name) {
		return NonPlayerCharacterImpl::new;
	}
}
