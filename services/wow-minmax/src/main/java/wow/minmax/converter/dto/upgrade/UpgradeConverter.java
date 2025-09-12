package wow.minmax.converter.dto.upgrade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.client.converter.equipment.EquippableItemConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.upgrade.UpgradeDTO;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
@Component
@AllArgsConstructor
public class UpgradeConverter implements ParametrizedConverter<wow.estimator.client.dto.upgrade.UpgradeDTO, UpgradeDTO, PhaseId> {
	private final EquippableItemConverter leanEquippableItemConverter;
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public UpgradeDTO doConvert(wow.estimator.client.dto.upgrade.UpgradeDTO source, PhaseId phaseId) {
		var equippableItems = leanEquippableItemConverter.convertBackList(source.itemDifference(), phaseId);

		return new UpgradeDTO(
				source.changePct(),
				equippableItemConverter.convertList(equippableItems),
				source.statDifference(),
				source.addedAbilities(),
				source.removedAbilities()
		);
	}
}
