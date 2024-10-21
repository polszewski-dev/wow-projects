import { TestBed } from '@angular/core/testing';

import { UpgradeStateService } from './upgrade-state.service';

describe('UpgradeStateService', () => {
	let service: UpgradeStateService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(UpgradeStateService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
