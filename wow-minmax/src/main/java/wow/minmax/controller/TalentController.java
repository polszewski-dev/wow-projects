package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Character;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.talents.Talent;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.TalentDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.UUID;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_POWER;
import static wow.minmax.service.CalculationService.EquivalentMode.REPLACEMENT;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/talent")
@AllArgsConstructor
@Slf4j
public class TalentController {
	private final PlayerProfileService playerProfileService;
	private final CalculationService calculationService;

	@GetMapping("{profileId}/list")
	public List<TalentDTO> getTalents(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		return playerProfile.getTalents().getList().stream()
				.map(x -> getTalentStatDTO(x, playerProfile.getCharacter()))
				.toList();
	}

	private TalentDTO getTalentStatDTO(Talent talent, Character character) {
		Attributes spEquivalent = getSpEquivalent(talent, character);

		return new TalentDTO(
				talent.getName(),
				talent.getRank(),
				talent.getMaxRank(),
				talent.getIcon(),
				talent.getTooltip(),
				talent.getAttributes().statString(),
				spEquivalent.getSpellPower()
		);
	}

	private Attributes getSpEquivalent(Talent talent, Character character) {
		return calculationService.getDpsStatEquivalent(
				talent.getAttributes(),
				SPELL_POWER,
				REPLACEMENT,
				character
		);
	}
}
