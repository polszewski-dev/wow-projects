import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { catchError, filter, from, map, mergeMap, of, switchMap, withLatestFrom } from "rxjs";
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { UpgradeService } from '../../services/upgrade.service';
import { dpsChanged, selectCharacter } from "../character/character.actions";
import { CharacterModuleState } from './../character-module.state';
import { loadAllUpgrades, loadAllUpgradesFailure, loadAllUpgradesSuccess, updateItemFilter } from './upgrades.actions';
import { selectItemFilter } from "./upgrades.selectors";

@Injectable()
export class UpgradesEffects {
	constructor(
		private actions$: Actions,
		private store: Store<CharacterModuleState>,
		private upgradeService: UpgradeService
	) {}

	upgradeListNeedsUpdate$ = createEffect(() => this.actions$.pipe(
		ofType(selectCharacter, dpsChanged, updateItemFilter),
		filter(({ characterId }) => !!characterId),
		map(({ characterId }) => loadAllUpgrades({ characterId: characterId! }))
	));

	loadAllUpgrades$ = createEffect(() => this.actions$.pipe(
		ofType(loadAllUpgrades),
		withLatestFrom(this.store.select(selectItemFilter)),
		switchMap(([{ characterId }, itemFilter]) => from(Object.values(ItemSlotGroup)).pipe(
			mergeMap(slotGroup => this.upgradeService.getUpgrades(characterId!, slotGroup, itemFilter).pipe(
				map(upgrades => loadAllUpgradesSuccess({ slotGroup, upgrades })),
				catchError(error => of(loadAllUpgradesFailure({ slotGroup, error })))
			))
		))
	));
}
