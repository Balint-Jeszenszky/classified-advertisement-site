import { Test, TestingModule } from '@nestjs/testing';
import { ScheduleLockService } from './schedule-lock.service';

describe('ScheduleLockService', () => {
  let service: ScheduleLockService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ScheduleLockService],
    }).compile();

    service = module.get<ScheduleLockService>(ScheduleLockService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
