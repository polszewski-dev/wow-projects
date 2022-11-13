package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatEquivalentProvider;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.ItemDataRepository;
import wow.commons.util.AttributesBuilder;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;
import wow.minmax.service.CalculationService;
import wow.minmax.service.ItemService;
import wow.minmax.service.SpellService;
import wow.minmax.service.UpgradeService;
import wow.minmax.service.impl.enumerators.BestItemVariantEnumerator;
import wow.minmax.service.impl.enumerators.FindUpgradesEnumerator;

import java.util.List;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_DAMAGE;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class UpgradeServiceImpl implements UpgradeService {
	private final ItemDataRepository itemDataRepository;
	private final SpellService spellService;
	private final ItemService itemService;
	private final CalculationService calculationService;

	@Override
	public List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, SpellId spellId) {
		Spell spell = spellService.getSpell(spellId);

		FindUpgradesEnumerator enumerator = new FindUpgradesEnumerator(
				playerProfile, slotGroup, spell, itemService, calculationService
		);

		return enumerator.run(slotGroup).getResult();
	}

	@Override
	public EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot slot, SpellId spellId) {
		Spell spell = spellService.getSpell(spellId);

		BestItemVariantEnumerator enumerator = new BestItemVariantEnumerator(
				playerProfile, slot, item, spell, itemService, calculationService
		);

		return enumerator.run(ItemSlotGroup.getGroup(slot))
				.getResult()
				.get(0)
				.getPossibleEquipment().get(slot);
	}

	@Override
	public double getItemScore(Item item, SpellSchool spellSchool) {
		Attributes statTotal = getStatTotal(item);

		return statTotal.getTotalSpellDamage(spellSchool) +
				statTotal.getSpellCritRating() +
				statTotal.getSpellHitRating() +
				statTotal.getSpellHasteRating();
	}

	private Attributes getStatTotal(Item item) {
		AttributesBuilder builder = new AttributesBuilder();

		builder.addAttributes(item.getAttributes());

		if (item.hasSockets()) {
			for (int i = 1; i <= item.getSocketCount(); ++i) {
				if (item.getSocketType(i) == SocketType.META) {
					builder.addAttribute(SPELL_DAMAGE, 50);
				} else {
					builder.addAttribute(SPELL_DAMAGE, 9);
				}
			}

			if (item.getSocketSpecification().getSocketTypes().stream().noneMatch(x -> x == SocketType.BLUE)) {
				builder.addAttributes(item.getSocketSpecification().getSocketBonus());
			}
		}

		for (ComplexAttribute attribute : item.getAttributes().getList(ComplexAttributeId.values())) {
			if (attribute instanceof StatEquivalentProvider) {
				StatEquivalentProvider statEquivalentProvider = (StatEquivalentProvider) attribute;
				Attributes statEquivalent = statEquivalentProvider.getStatEquivalent(getStatProviderForItemScore());
				builder.addAttributes(statEquivalent);
			}
		}

		return builder.toAttributes();
	}

	private static StatProvider getStatProviderForItemScore() {
		return StatProvider.fixedValues(0.99, 0.30, Duration.seconds(2.5));
	}
}
