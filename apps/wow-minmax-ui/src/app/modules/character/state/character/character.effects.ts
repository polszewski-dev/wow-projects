import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { catchError, filter, from, map, mergeMap, of, switchMap, tap } from "rxjs";
import { BuffListType } from "../../model/buff/BuffListType";
import { BuffService } from '../../services/buff.service';
import { CharacterService } from '../../services/character.service';
import { EquipmentService } from '../../services/equipment.service';
import { loadEnchantOptions, loadEquipmentOptions, loadGemOptions, loadItemOptions } from "../equipment-options/equipment-options.actions";
import { CharacterModuleState } from './../character-module.state';
import { dpsChanged, enableBuff, enableBuffFailure, enableBuffSuccess, enableConsumable, enableConsumableFailure, enableConsumableSuccess, equipEnchant, equipEnchantFailure, equipEnchantSuccess, equipGem, equipGemFailure, equipGemSuccess, equipItemBestVariant, equipItemBestVariantFailure, equipItemBestVariantSuccess, equipItemGroup, equipItemGroupFailure, equipItemGroupSuccess, loadBuffListFailure, loadBuffListSuccess, loadBuffs, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumables, loadConsumablesFailure, loadConsumablesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatus, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipment, resetEquipmentFailure, resetEquipmentSuccess, selectCharacter } from './character.actions';
import { ConsumableService } from "../../services/consumable.service";

@Injectable()
export class CharacterEffects {
	constructor(
		private actions$: Actions,
		private store: Store<CharacterModuleState>,
		private characterService: CharacterService,
		private equipmentService: EquipmentService,
		private buffService: BuffService,
		private consumableService: ConsumableService
	) {}

	selectCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(selectCharacter),
		filter(({ characterId }) => !!characterId),
		switchMap(({ characterId }) => of(
			loadCharacter({ characterId: characterId! }),
			loadEquipment({ characterId: characterId! }),
			loadSocketStatus({ characterId: characterId! }),
			loadBuffs({ characterId: characterId! }),
			loadConsumables({ characterId: characterId! }),
			loadEquipmentOptions({ characterId: characterId! }),
			loadItemOptions({ characterId: characterId! }),
			loadEnchantOptions({ characterId: characterId! }),
			loadGemOptions({ characterId: characterId! })
		))
	));

	loadCharacter$ = createEffect(() => this.actions$.pipe(
		ofType(loadCharacter),
		switchMap(({ characterId }) => this.characterService.getCharacter(characterId).pipe(
			map(character => loadCharacterSuccess({ character })),
			catchError(error => of(loadCharacterFailure({ error })))
		))
	));

	loadEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(loadEquipment),
		switchMap(({ characterId }) => this.equipmentService.getEquipment(characterId).pipe(
			map(equipment => loadEquipmentSuccess({ equipment })),
			catchError(error => of(loadEquipmentFailure({ error })))
		))
	));

	loadSocketStatus$ = createEffect(() => this.actions$.pipe(
		ofType(loadSocketStatus),
		switchMap(({ characterId }) => this.equipmentService.getSocketStatus(characterId).pipe(
			map(socketStatus => loadSocketStatusSuccess({ socketStatus })),
			catchError(error => of(loadSocketStatusFailure({ error })))
		))
	));

	loadBuffs$ = createEffect(() => this.actions$.pipe(
		ofType(loadBuffs),
		switchMap(({ characterId }) => from(Object.values(BuffListType)).pipe(
			mergeMap(buffListType => this.buffService.getBuffs(characterId, buffListType).pipe(
				map(buffList => loadBuffListSuccess({ buffListType, buffList })),
				catchError(error => of(loadBuffListFailure({ buffListType, error })))
			))
		))
	));

	loadConsumables$ = createEffect(() => this.actions$.pipe(
		ofType(loadConsumables),
		switchMap(({ characterId }) => this.consumableService.getConsumables(characterId).pipe(
			map(consumables => loadConsumablesSuccess({ consumables })),
			catchError(error => of(loadConsumablesFailure({ error })))
		))
	));

	equipItemBestVariant$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemBestVariant),
		switchMap(({ characterId, itemSlot, item }) => this.equipmentService.equipItemBestVariant(characterId, itemSlot, item).pipe(
			map(equippableItem => equipItemBestVariantSuccess({ characterId, itemSlot, equippableItem })),
			catchError(error => of(equipItemBestVariantFailure({ itemSlot, error })))
		))
	));

	equipItemGroup$ = createEffect(() => this.actions$.pipe(
		ofType(equipItemGroup),
		switchMap(({ characterId, slotGroup, items }) => this.equipmentService.equipItems(characterId, slotGroup, items).pipe(
			map(() => equipItemGroupSuccess({ characterId, slotGroup, items })),
			catchError(error => of(equipItemGroupFailure({ slotGroup, error })))
		))
	));

	equipEnchant$ = createEffect(() => this.actions$.pipe(
		ofType(equipEnchant),
		switchMap(({ characterId, equippedItem, itemSlot, enchant }) => this.equipmentService.equipEnchant(characterId, equippedItem, itemSlot, enchant).pipe(
			map(equippableItem => equipEnchantSuccess({ characterId, itemSlot, equippableItem })),
			catchError(error => of(equipEnchantFailure({ itemSlot, error })))
		))
	));

	equipGem$ = createEffect(() => this.actions$.pipe(
		ofType(equipGem),
		switchMap(({ characterId, equippedItem, itemSlot, socketNo, gem }) => this.equipmentService.equipGem(characterId, equippedItem, itemSlot, socketNo, gem).pipe(
			map(equippableItem => equipGemSuccess({ characterId, itemSlot, socketNo, equippableItem })),
			catchError(error => of(equipGemFailure({ itemSlot, socketNo, error })))
		))
	));

	resetEquipment$ = createEffect(() => this.actions$.pipe(
		ofType(resetEquipment),
		switchMap(({ characterId }) => this.equipmentService.resetEquipment(characterId).pipe(
			map(() => resetEquipmentSuccess({ characterId })),
			catchError(error => of(resetEquipmentFailure({ error })))
		))
	));

	enableBuff$ = createEffect(() => this.actions$.pipe(
		ofType(enableBuff),
		switchMap(({ characterId, buffListType, buff }) => this.buffService.enableBuff(characterId, buffListType, buff).pipe(
			map(buffList => enableBuffSuccess({ characterId, buffListType, buffList })),
			catchError(error => of(enableBuffFailure({ buffListType, error })))
		))
	));

	enableConsumable$ = createEffect(() => this.actions$.pipe(
		ofType(enableConsumable),
		switchMap(({ characterId, consumable }) => this.consumableService.enableConsumable(characterId, consumable).pipe(
			map(consumables => enableConsumableSuccess({ characterId, consumables })),
			catchError(error => of(enableConsumableFailure({ error })))
		))
	));

	dpsChanged$ = createEffect(() => this.actions$.pipe(
		ofType(
			equipItemBestVariantSuccess,
			equipItemGroupSuccess,
			equipEnchantSuccess,
			equipGemSuccess,
			resetEquipmentSuccess,
			enableBuffSuccess
		),
		map(({ characterId }) => dpsChanged({ characterId: characterId! })
	)));

	socketStatusNeedsUpdate$ = createEffect(() => this.actions$.pipe(
		ofType(
			equipItemBestVariantSuccess,
			equipItemGroupSuccess,
			equipGemSuccess,
			resetEquipmentSuccess,
		),
		map(({ characterId }) => loadSocketStatus({ characterId }))
	));

	// log$ = createEffect(() => this.actions$.pipe(
	// 	tap(x => console.log(x.type, x)),
	// ), { functional: true, dispatch: false });
}
