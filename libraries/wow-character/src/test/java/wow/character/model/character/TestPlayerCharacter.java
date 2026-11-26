package wow.character.model.character;

import wow.character.model.asset.Asset;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.commons.model.buff.Buff;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.Talent;

import java.util.ArrayList;
import java.util.List;

import static wow.character.model.character.TestEvent.*;

/**
 * User: POlszewski
 * Date: 2026-11-25
 */
public class TestPlayerCharacter extends PlayerCharacterImpl {
	public TestPlayerCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, availableTalents, professions, exclusiveFactions);
	}

	public List<TestEvent> events = new ArrayList<>();
	
	private void addEvent(TestEvent event) {
		events.add(event);
	}

	@Override
	public void itemStatsAdded(Item item) {
		super.itemStatsAdded(item);
		addEvent(new ItemStatsAdded(item));
	}

	@Override
	public void itemStatsRemoved(Item item) {
		super.itemStatsRemoved(item);
		addEvent(new ItemStatsRemoved(item));
	}

	@Override
	public void itemSetBonusAdded(ItemSetBonus setBonus) {
		super.itemSetBonusAdded(setBonus);
		addEvent(new ItemSetBonusAdded(setBonus));
	}

	@Override
	public void itemSetBonusRemoved(ItemSetBonus setBonus) {
		super.itemSetBonusRemoved(setBonus);
		addEvent(new ItemSetBonusRemoved(setBonus));
	}

	@Override
	public void enchantStatsAdded(Enchant enchant) {
		super.enchantStatsAdded(enchant);
		addEvent(new EnchantStatsAdded(enchant));
	}

	@Override
	public void enchantStatsRemoved(Enchant enchant) {
		super.enchantStatsRemoved(enchant);
		addEvent(new EnchantStatsRemoved(enchant));
	}

	@Override
	public void gemStatsAdded(Gem gem) {
		super.gemStatsAdded(gem);
		addEvent(new GemStatsAdded(gem));
	}

	@Override
	public void gemStatsRemoved(Gem gem) {
		super.gemStatsRemoved(gem);
		addEvent(new GemStatsRemoved(gem));
	}

	@Override
	public void socketBonusAdded(Effect socketBonus) {
		super.socketBonusAdded(socketBonus);
		addEvent(new SocketBonusAdded(socketBonus));
	}

	@Override
	public void socketBonusRemoved(Effect socketBonus) {
		super.socketBonusRemoved(socketBonus);
		addEvent(new SocketBonusRemoved(socketBonus));
	}

	@Override
	public void talentEnabled(Talent talent) {
		super.talentEnabled(talent);
		addEvent(new TalentEnabled(talent));
	}

	@Override
	public void talentDisabled(Talent talent) {
		super.talentDisabled(talent);
		addEvent(new TalentDisabled(talent));
	}

	@Override
	public void buffEnabled(Buff buff) {
		super.buffEnabled(buff);
		addEvent(new BuffEnabled(buff));
	}

	@Override
	public void buffDisabled(Buff buff) {
		super.buffDisabled(buff);
		addEvent(new BuffDisabled(buff));
	}

	@Override
	public void consumableEnabled(Consumable consumable) {
		super.consumableEnabled(consumable);
		addEvent(new ConsumableEnabled(consumable));
	}

	@Override
	public void consumableDisabled(Consumable consumable) {
		super.consumableDisabled(consumable);
		addEvent(new ConsumableDisabled(consumable));
	}

	@Override
	public void assetEnabled(Asset asset) {
		super.assetEnabled(asset);
		addEvent(new AssetEnabled(asset));
	}

	@Override
	public void assetDisabled(Asset asset) {
		super.assetDisabled(asset);
		addEvent(new AssetDisabled(asset));
	}
}
