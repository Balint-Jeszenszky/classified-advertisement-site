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
      _id: id,
    }));

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation((params: any) => {
      expect(params.task).toBe(taskName);
      return {
        exec: jest.fn(() => ({ process: service.uuid })),
      } as any;
    });

    const deleteManySpy = jest.spyOn(mockModel, 'deleteMany').mockImplementation((filter: any) => {
      expect(filter.$and[0].task).toBe(taskName);
      expect(filter.$and[1].lockedUntil.$lt).toBeDefined();
      return {
        exec: jest.fn(),
      } as any;
    });

    const locked = await service.lock(taskName, new Date('2100-01-01 00:00:00'));

    expect(locked).toBe(true);
    expect(createSpy).toBeCalled();
    expect(findOneSpy).toBeCalled();
    expect(deleteManySpy).toBeCalled();
  });

  it('should return false if uuid not matches', async () => {
    const id = 'UUID';
    const taskName = 'task';

    const createSpy = jest.spyOn(mockModel, 'create').mockImplementation((params: any) => ({
      ...params,
      _id: id,
    }));

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation((params: any) => {
      expect(params.task).toBe(taskName);
      return {
        exec: jest.fn(() => ({ process: 'OTHER-UUID' })),
      } as any;
    });

    const deleteManySpy = jest.spyOn(mockModel, 'deleteMany').mockImplementation((filter: any) => {
      expect(filter.$and[0].task).toBe(taskName);
      expect(filter.$and[1].$or[0]._id).toBe(id);
      expect(filter.$and[1].$or[1].lockedUntil.$lt).toBeDefined();
      return {
        exec: jest.fn(),
      } as any;
    });

    const locked = await service.lock(taskName, new Date('2100-01-01 00:00:00'));

    expect(locked).toBe(false);
    expect(createSpy).toBeCalled();
    expect(findOneSpy).toBeCalled();
    expect(deleteManySpy).toBeCalled();
  });
});
