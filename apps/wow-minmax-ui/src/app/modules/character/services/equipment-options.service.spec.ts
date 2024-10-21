import { TestBed } from '@angular/core/testing';

import { EquipmentOptionsService } from './equipment-options.service';

describe('EquipmentOptionsService', () => {
	let service: EquipmentOptionsService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(EquipmentOptionsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
