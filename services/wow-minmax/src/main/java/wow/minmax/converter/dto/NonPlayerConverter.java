package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.impl.NonPlayerImpl;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
public class NonPlayerConverter extends AbstractNonPlayerConverter<NonPlayer> {
	public NonPlayerConverter(CharacterService characterService) {
		super(characterService);
	}

	@Override
	protected NonPlayerCharacterFactory<NonPlayer> getFactory(String name) {
		return NonPlayerImpl::new;
	}
}
