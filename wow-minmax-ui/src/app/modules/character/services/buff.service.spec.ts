import { TestBed } from '@angular/core/testing';

import { BuffService } from './buff.service';

describe('BuffService', () => {
	let service: BuffService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(BuffService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
