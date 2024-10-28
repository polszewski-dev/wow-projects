import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { catchError, filter, from, map, mergeMap, of, switchMap } from "rxjs";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { EquipmentOptionsService } from '../../services/equipment-options.service';
import { CharacterModuleState } from './../character-module.state';
import { loadEnchantOptions, loadEnchantOptionsFailure, loadEnchantOptionsSuccess, loadEquipmentOptions, loadEquipmentOptionsFailure, loadEquipmentOptionsSuccess, loadGemOptions, loadGemOptionsFailure, loadGemOptionsSuccess, loadItemOptions, loadItemOptionsFailure, loadItemOptionsSuccess } from "./equipment-options.actions";

@Injectable()
export class EquipmentOptionsEffects {
	constructor(
		private actions$: Actions,
		private store: Store<CharacterModuleState>,
		private equipmentOptionsService: EquipmentOptionsService,
	) {}

	loadEquipmentOptions$ = createEffect(() => this.actions$.pipe(
		ofType(loadEquipmentOptions),
		switchMap(({ characterId }) => this.equipmentOptionsService.getEquipmentOptions(characterId).pipe(
			map(equipmentOptions => loadEquipmentOptionsSuccess({ equipmentOptions })),
			catchError(error => of(loadEquipmentOptionsFailure({ error })))
		))
	));

	loadItemOptions$ = createEffect(() => this.actions$.pipe(
		ofType(loadItemOptions),
		switchMap(({ characterId }) => from(Object.values(ItemSlot)).pipe(
			filter(itemSlot => itemSlot !== ItemSlot.FINGER_2 && itemSlot !== ItemSlot.TRINKET_2),
			mergeMap(itemSlot => this.equipmentOptionsService.getItemOptions(characterId, itemSlot).pipe(
				map(itemOptions => loadItemOptionsSuccess({ itemSlot, itemOptions })),
				catchError(error => of(loadItemOptionsFailure({ itemSlot, error })))
			))
		))
	));

	loadEnchantOptions$ = createEffect(() => this.actions$.pipe(
		ofType(loadEnchantOptions),
		switchMap(({ characterId }) => this.equipmentOptionsService.getEnchantOptions(characterId).pipe(
			map(enchantOptions => loadEnchantOptionsSuccess({ enchantOptions })),
			catchError(error => of(loadEnchantOptionsFailure({ error })))
		))
	));

	loadGemOptions$ = createEffect(() => this.actions$.pipe(
		ofType(loadGemOptions),
		switchMap(({ characterId }) => this.equipmentOptionsService.getGemOptions(characterId).pipe(
			map(gemOptions => loadGemOptionsSuccess({ gemOptions })),
			catchError(error => of(loadGemOptionsFailure({ error })))
		))
	));
}
