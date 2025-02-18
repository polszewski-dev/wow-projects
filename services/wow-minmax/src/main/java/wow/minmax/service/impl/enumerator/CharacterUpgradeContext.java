package wow.minmax.service.impl.enumerator;

import wow.character.model.build.Rotation;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.AccumulatedRotationStats;
import wow.minmax.model.Player;
import wow.minmax.model.Upgrade;
import wow.minmax.service.CalculationService;
import wow.minmax.util.EffectList;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public class CharacterUpgradeContext {
	private final Player referenceCharacter;
	private final Rotation rotation;
	private final ItemSlotGroup slotGroup;
	private final double referenceDps;
	private final Player workingCharacter;
	private final EffectList workingEffectList;
	private final AccumulatedRotationStats rotationStats;
	private final EffectList targetEffectList;

	private final CalculationService calculationService;

	public CharacterUpgradeContext(Player referenceCharacter, ItemSlotGroup slotGroup, CalculationService calculationService) {
		this.referenceCharacter = referenceCharacter;
		this.rotation = referenceCharacter.getRotation();
		this.slotGroup = slotGroup;
		this.calculationService = calculationService;

		this.targetEffectList = new EffectList(referenceCharacter.getTarget());
		targetEffectList.collectEffects();

		this.referenceDps = getReferenceDps();
		this.workingCharacter = referenceCharacter.copy();

		workingCharacter.getEquipment().unequip(slotGroup);

		this.workingEffectList = new EffectList(workingCharacter);

		workingEffectList.collectEffects();

		this.rotationStats = calculationService.getAccumulatedRotationStats(workingCharacter, rotation, workingEffectList, targetEffectList);

		workingEffectList.removeAll();
		targetEffectList.removeAll();
	}

	private double getReferenceDps() {
		var referenceEffectList = EffectList.createSolved(referenceCharacter);
		var referenceRotationStats = calculationService.getAccumulatedRotationStats(referenceCharacter, rotation, referenceEffectList, targetEffectList);

		return calculationService.getRotationDps(referenceCharacter, rotation, referenceRotationStats);
	}

	public double getChangePct(EquippableItem[] itemOption) {
		var totalStats = getTotalStats(itemOption);
		var dps = calculationService.getRotationDps(workingCharacter, rotation, totalStats);
		return 100 * (dps / referenceDps - 1);
	}

	public Upgrade getUpgrade(EquippableItem[] itemOption, double changePct) {
		return new Upgrade(
				slotGroup,
				List.of(itemOption),
				changePct,
				referenceCharacter
		);
	}

	private AccumulatedRotationStats getTotalStats(EquippableItem[] itemOption) {
		var itemOptionEffects = getItemOptionEffects(itemOption);
		var result = rotationStats.copy();

		accumulateAttributes(itemOptionEffects, result);
		return result;
	}

	private void accumulateAttributes(EffectList effectList, AccumulatedRotationStats result) {
		effectList.accumulateAttributes(result.getBaseStats(), result);

		for (var stats : result.getStatsByAbilityId().values()) {
			effectList.accumulateAttributes(stats, null);
		}
	}

	private EffectList getItemOptionEffects(EquippableItem[] itemOption) {
		var result = workingEffectList.copy();

		for (var item : itemOption) {
			item.collectEffects(result);
		}

		result.collectRest();
		return result;
	}
}
