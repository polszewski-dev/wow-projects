package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Buffs;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.minmax.converter.dto.BuffConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.BuffDTO;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@RestController
@RequestMapping("api/v1/buff")
@AllArgsConstructor
@Slf4j
public class BuffController {
	private final PlayerProfileService playerProfileService;
	private final SpellService spellService;
	private final BuffConverter buffConverter;

	@GetMapping("{profileId}")
	public List<BuffDTO> getBuffs(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		return getPlayerBuffs(playerProfile);
	}

	@GetMapping("{profileId}/enable/buff/{buffId}/{enabled}")
	public List<BuffDTO> changeBuff(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("buffId") int buffId,
			@PathVariable("enabled") boolean enabled
	) {
		PlayerProfile playerProfile = playerProfileService.enableBuff(profileId, buffId, enabled);
		log.info("Changed buff profile id: {}, buffId: {}, enabled: {}", profileId, buffId, enabled);
		return getPlayerBuffs(playerProfile);
	}

	private List<BuffDTO> getPlayerBuffs(PlayerProfile playerProfile) {
		Buffs buffs = playerProfile.getBuffs();
		List<Buff> availableBuffs = spellService.getBuffs(playerProfile.getCharacter());

		return availableBuffs.stream()
				.map(availableBuff -> getBuffDTO(availableBuff, buffs))
				.collect(Collectors.toList());
	}

	private BuffDTO getBuffDTO(Buff buff, Buffs buffs) {
		BuffDTO dto = buffConverter.convert(buff);
		dto.setEnabled(buffs.hasBuff(buff));
		return dto;
	}
}
