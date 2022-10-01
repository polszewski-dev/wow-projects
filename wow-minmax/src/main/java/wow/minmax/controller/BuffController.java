package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.minmax.converter.dto.BuffConverter;
import wow.minmax.model.dto.BuffDTO;
import wow.minmax.service.SpellService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@RestController
@RequestMapping("api/v1/buff")
@AllArgsConstructor
public class BuffController {
	private final SpellService spellService;
	private final BuffConverter buffConverter;

	@GetMapping("list")
	public List<BuffDTO> getAvailableBuffs() {
		return buffConverter.convertList(spellService.getAvailableBuffs());
	}
}
