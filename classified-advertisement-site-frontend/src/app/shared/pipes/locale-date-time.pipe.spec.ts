import { LocaleDateTimePipe } from './locale-date-time.pipe';

describe('LocaleDateTimePipe', () => {
  it('create an instance', () => {
    const pipe = new LocaleDateTimePipe();
    expect(pipe).toBeTruthy();
  });
});
