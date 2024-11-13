package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.service.CharacterService;
import wow.commons.client.converter.Converter;
import wow.commons.client.dto.BuffDTO;
import wow.commons.model.buff.BuffIdAndRank;
import wow.simulator.client.dto.SimulationRequestDTO;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.impl.NonPlayerImpl;
import wow.simulator.model.unit.impl.PlayerImpl;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
@AllArgsConstructor
public class PlayerConverter implements Converter<SimulationRequestDTO, Player> {
	private final CharacterService characterService;

	private final PlayerCharacterConverter playerCharacterConverter;
	private final NonPlayerCharacterConverter nonPlayerCharacterConverter;

	@Override
	public Player doConvert(SimulationRequestDTO source) {
		var character = playerCharacterConverter.convert(source.character());
		var targetEnemy = nonPlayerCharacterConverter.convert(source.targetEnemy(), source.character().phaseId());

		character.setTarget(targetEnemy);

		characterService.updateAfterRestrictionChange(character);

		character.setBuffs(getBuffIds(source.character().buffs()));
		targetEnemy.setBuffs(getBuffIds(source.targetEnemy().targetDebuffs()));

		var player = new PlayerImpl(source.character().name(), character);
		var target = new NonPlayerImpl(source.targetEnemy().name(), (NonPlayerCharacter) character.getTarget());

		player.setTarget(target);

		return player;
	}

	private static List<BuffIdAndRank> getBuffIds(List<BuffDTO> buffs) {
		return buffs.stream()
				.map(BuffDTO::getId)
				.toList();
	}
}
