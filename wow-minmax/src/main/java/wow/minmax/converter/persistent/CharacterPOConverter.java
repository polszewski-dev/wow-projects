package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Build;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.character.service.CharacterService;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.BuildPO;
import wow.minmax.model.persistent.CharacterPO;
import wow.minmax.model.persistent.TalentPO;

import java.util.Map;

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
	private final RotationPOConverter rotationPOConverter;

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
				source.getExclusiveFactions().getList(),
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

		var params = PoConverterParams.createParams(character);

		Enemy targetEnemy = enemyPOConverter.convertBack(source.getTargetEnemy(), params);
		character.setTargetEnemy(targetEnemy);

		changeBuild(character, source, params);

		character.setProfessions(characterProfessionPOConverter.convertBackList(source.getProfessions(), params));
		character.getExclusiveFactions().set(source.getExclusiveFactions());
		character.setBuffs(buffPOConverter.convertBackList(source.getBuffs(), params));
		character.getTargetEnemy().setDebuffs(buffPOConverter.convertBackList(source.getTargetEnemy().getDebuffs(), params));
		character.setEquipment(equipmentPOConverter.convertBack(source.getEquipment(), params));

		return character;
	}

	private void changeBuild(Character character, CharacterPO source, Map<String, Object> params) {
		Build build = character.getBuild();
		BuildPO sourceBuild = source.getBuild();

		for (TalentPO sourceTalent : sourceBuild.getTalents()) {
			build.getTalents().enableTalent(sourceTalent.getTalentId(), sourceTalent.getRank());
		}

		build.setRole(sourceBuild.getRole());
		build.setActivePet(sourceBuild.getActivePet());

		characterService.onTalentsChange(character);

		build.setRotation(rotationPOConverter.convertBack(sourceBuild.getRotation(), params));
	}
}
