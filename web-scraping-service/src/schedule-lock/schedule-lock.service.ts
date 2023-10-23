import { Injectable } from '@nestjs/common';
import { Model } from 'mongoose';
import { ScheduleLock } from './schema/schedule-lock.schema';
import { InjectModel } from '@nestjs/mongoose';
import { v4 as uuidv4 } from 'uuid';

@Injectable()
export class ScheduleLockService {
  private readonly serviceUUID: string = uuidv4();

  constructor(
    @InjectModel(ScheduleLock.name) private readonly scheduleLockModel: Model<ScheduleLock>,
  ) { }

  get uuid() {
    return this.serviceUUID;
  }

  async lock(task: string, lockedUntil: Date) {
    const lockData = new this.scheduleLockModel({
      task,
      process: this.uuid,
      lockedAt: new Date(),
      lockedUntil,
    });

    const lockAttempt = await lockData.save();
    const lock = await this.scheduleLockModel.findOne({ task, lockedUntil: { $gt: new Date() } }, undefined, { sort: { lockedAt: 'asc' } }).exec();

    if (lock.process === this.uuid) {
      return true;
    }

    await this.scheduleLockModel.deleteMany({ $or: [{ _id: lockAttempt._id }, { lockedUntil: { $lt: new Date() } }] }).exec();

    return false;
  }
}
