import { TestBed } from '@angular/core/testing';

import { EquipmentOptionsStateService } from './equipment-options-state.service';

describe('EquipmentOptionsStateService', () => {
	let service: EquipmentOptionsStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(EquipmentOptionsStateService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
