package wow.estimator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.impl.NonPlayerImpl;

/**
 * User: POlszewski
 * Date: 2024-11-10
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
