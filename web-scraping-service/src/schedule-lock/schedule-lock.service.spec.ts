import { Test, TestingModule } from '@nestjs/testing';
import { ScheduleLockService } from './schedule-lock.service';
import { Model } from 'mongoose';
import { ScheduleLock } from './schema/schedule-lock.schema';
import { getModelToken } from '@nestjs/mongoose';

describe('ScheduleLockService', () => {
  let service: ScheduleLockService;
  let mockModel: Model<ScheduleLock>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScheduleLockService,
        {
          provide: getModelToken(ScheduleLock.name),
          useValue: Model<ScheduleLock>,
        },
      ],
    }).compile();

    mockModel = module.get<Model<ScheduleLock>>(getModelToken(ScheduleLock.name));
    service = module.get<ScheduleLockService>(ScheduleLockService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should return true if uuid matches', async () => {
    const id = 'UUID';
    const taskName = 'task';

    const createSpy = jest.spyOn(mockModel, 'create').mockImplementation((params: any) => ({
      ...params,
      id,
    }));

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation((params: any) => {
      expect(params.task).toBe(taskName);
      return {
        exec: jest.fn(() => ({ process: service.uuid })),
      } as any;
    });

    const deleteManySpy = jest.spyOn(mockModel, 'deleteMany').mockImplementation();

    const locked = await service.lock(taskName, new Date('2100-01-01 00:00:00'));

    expect(locked).toBe(true);
    expect(createSpy).toBeCalled();
    expect(findOneSpy).toBeCalled();
    expect(deleteManySpy).toBeCalled();
  });
});
