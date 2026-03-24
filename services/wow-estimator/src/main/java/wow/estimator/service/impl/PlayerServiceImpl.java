package wow.estimator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.asset.AssetExecution;
import wow.character.model.character.Raid;
import wow.character.service.AssetService;
import wow.character.service.CharacterCalculationService;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectId;
import wow.commons.repository.spell.SpellRepository;
import wow.estimator.model.EffectInstance;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;
import wow.estimator.model.Unit;
import wow.estimator.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-12
 */
@Component
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final AssetService assetService;
	private final CharacterCalculationService characterCalculationService;
	private final SpellRepository spellRepository;

	@Override
	public Player getPlayer(Raid<Player> raid, NonPlayer target) {
		var player = raid.getFirstMember();
		var executionPlan = assetService.getAssetExecutionPlan(raid);
		var forPlayerOnly = getExecutionsAffectingPlayer(executionPlan, raid);

		for (var execution : forPlayerOnly) {
			execute(execution, player, target);
		}

		player.setTarget(target);

		return player;
	}

	private List<AssetExecution<Player>> getExecutionsAffectingPlayer(List<AssetExecution<Player>> executionPlan, Raid<Player> raid) {
		var result = new ArrayList<AssetExecution<Player>>();

		for (var execution : executionPlan) {
			if (affectsPlayer(execution, raid)) {
				result.add(execution);
			}
		}

		return result;
	}

	private boolean affectsPlayer(AssetExecution<Player> execution, Raid<Player> raid) {
		return switch (execution.scope()) {
			case PERSONAL -> execution.player() == raid.getFirstMember();
			case PARTY -> raid.getFirstParty().has(execution.player());
			case RAID -> true;
		};
	}

	private void execute(AssetExecution<Player> execution, Player player, NonPlayer target) {
		switch (execution.name()) {
			case
					"Curse of Shadow",
					"Curse of the Elements"
			->
					applyAbilityEffect(target, execution);

			case
					"Improved Scorch"
			->
					applyEffect(target, "Fire Vulnerability", 5);

			case
					"Shadow Weaving"
			->
					applyEffect(target, 15258, 5);

			case
					"Misery"
			->
					applyEffect(target, 33200, 1);

			case
					"Burning Wish",
					"Touch of Shadow",
					"Moonkin Aura"
			->
					applyEffect(player, execution);

			case "Imp" -> {
					// ignored
			}

			default ->
					applyAbilityEffect(player, execution);

		}
	}

	private void applyAbilityEffect(Unit target, AssetExecution<Player> execution) {
		applyAbilityEffect(target, execution, 1);
	}

	private void applyAbilityEffect(Unit target, AssetExecution<Player> execution, int numStacks) {
		var caster = execution.player();
		var abilityName = execution.name();
		var ability = caster.getAbility(abilityName).orElseThrow();
		var effect = ability.getEffectApplication().commands().getFirst().effect();

		var effectAugmentations = characterCalculationService.getEffectAugmentations(caster, ability, target);
		var augmentedEffect = effect.augment(effectAugmentations);

		apply(target, augmentedEffect, numStacks);
	}

	private void applyEffect(Unit target, AssetExecution<Player> execution) {
		applyEffect(target, execution.name(), 1);
	}

	private void applyEffect(Unit target, String effectName, int numStacks) {
		var effect = spellRepository.getEffect(effectName, target.getPhaseId()).orElseThrow();

		apply(target, effect, numStacks);
	}

	private void applyEffect(Unit target, int effectId, int numStacks) {
		var effect = spellRepository.getEffect(EffectId.of(effectId), target.getPhaseId()).orElseThrow();

		apply(target, effect, numStacks);
	}

	private void apply(Unit target, Effect effect, int numStacks) {
		var effectInstance = new EffectInstance(effect, numStacks);

		target.getEffectInstances().add(effectInstance);
	}
}
