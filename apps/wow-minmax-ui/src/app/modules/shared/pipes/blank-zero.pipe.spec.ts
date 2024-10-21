import { BlankZeroPipe } from './blank-zero.pipe';

describe('BlankZeroPipe', () => {
	it('create an instance', () => {
		const pipe = new BlankZeroPipe();
		expect(pipe).toBeTruthy();
	});
});
