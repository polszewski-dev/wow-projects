package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Build;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.buff.BuffIdAndRank;
import wow.minmax.model.CharacterId;
import wow.minmax.model.persistent.BuffPO;
import wow.minmax.model.persistent.BuildPO;
import wow.minmax.model.persistent.PlayerCharacterPO;
import wow.minmax.model.persistent.TalentPO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PlayerCharacterPOConverter implements ParametrizedConverter<PlayerCharacter, PlayerCharacterPO, CharacterId>, BackConverter<PlayerCharacter, PlayerCharacterPO> {
	private final BuildPOConverter buildPOConverter;
	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
	private final BuffPOConverter buffPOConverter;
	private final NonPlayerCharacterPOConverter nonPlayerCharacterPOConverter;

	private final CharacterService characterService;

	@Override
	public PlayerCharacterPO doConvert(PlayerCharacter source, CharacterId characterId) {
		return new PlayerCharacterPO(
				characterId.toString(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				buildPOConverter.convert(source.getBuild()),
				equipmentPOConverter.convert(source.getEquipment()),
				characterProfessionPOConverter.convertList(source.getProfessions().getList()),
				source.getExclusiveFactions().getList(),
				buffPOConverter.convertList(source.getBuffs().getList()),
				nonPlayerCharacterPOConverter.convert((NonPlayerCharacter) source.getTarget())
		);
	}

	@Override
	public PlayerCharacter doConvertBack(PlayerCharacterPO source) {
		PlayerCharacter character = characterService.createPlayerCharacter(
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getPhaseId()
		);

		changeBuild(character, source);

		character.setProfessions(characterProfessionPOConverter.convertBackList(source.getProfessions(), character.getPhaseId()));
		character.getExclusiveFactions().set(source.getExclusiveFactions());
		character.setEquipment(equipmentPOConverter.convertBack(source.getEquipment(), character.getPhaseId()));

		var target = nonPlayerCharacterPOConverter.convertBack(source.getTarget());
		character.setTarget(target);

		characterService.updateAfterRestrictionChange(character);

		character.setBuffs(getBuffIds(source.getBuffs()));
		character.getTarget().setBuffs(getBuffIds(source.getTarget().getDebuffs()));

		return character;
	}

	private void changeBuild(PlayerCharacter character, PlayerCharacterPO source) {
		Build build = character.getBuild();
		BuildPO sourceBuild = source.getBuild();

		for (TalentPO sourceTalent : sourceBuild.getTalents()) {
			build.getTalents().enableTalent(sourceTalent.getTalentId(), sourceTalent.getRank());
		}

		build.setRole(sourceBuild.getRole());
		build.setActivePet(sourceBuild.getActivePet());
		build.setRotation(RotationTemplate.parse(sourceBuild.getRotation()).createRotation());
	}

	private List<BuffIdAndRank> getBuffIds(List<BuffPO> buffs) {
		return buffs.stream()
				.map(BuffPO::getId)
				.toList();
	}
}