package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.character.service.CharacterService;
import wow.commons.model.buffs.Buff;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.CharacterPO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class CharacterPOConverter implements Converter<Character, CharacterPO>, BackConverter<Character, CharacterPO> {
	private final BuildPOConverter buildPOConverter;
	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
	private final BuffPOConverter buffPOConverter;
	private final EnemyPOConverter enemyPOConverter;

	private final CharacterService characterService;

	@Override
	public CharacterPO doConvert(Character source) {
		return new CharacterPO(
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				buildPOConverter.convert(source.getBuild()),
				equipmentPOConverter.convert(source.getEquipment()),
				characterProfessionPOConverter.convertList(source.getProfessions().getList()),
				buffPOConverter.convertList(source.getBuffs().getList()),
				enemyPOConverter.convert(source.getTargetEnemy())
		);
	}

	@Override
	public Character doConvertBack(CharacterPO source) {
		Character character = characterService.createCharacter(
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getPhaseId()
		);

		var params = PoConverterParams.createParams(source.getPhaseId());

		Enemy targetEnemy = enemyPOConverter.convertBack(source.getTargetEnemy(), params);
		Collection<Buff> debuffs = new ArrayList<>(targetEnemy.getDebuffs().getList());

		character.setTargetEnemy(targetEnemy);

		characterService.changeBuild(character, source.getBuild().getCharacterTemplateId());

		targetEnemy.setDebuffs(debuffs); // changeBuild resets target's debuffs

		character.setEquipment(equipmentPOConverter.convertBack(source.getEquipment(), params));
		character.setProfessions(characterProfessionPOConverter.convertBackList(source.getProfessions(), params));
		character.setBuffs(buffPOConverter.convertBackList(source.getBuffs(), params));

		return character;
	}
}
