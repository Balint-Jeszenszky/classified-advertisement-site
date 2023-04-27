import { Controller, Logger } from '@nestjs/common';
import { Ctx, MessagePattern, Payload, RmqContext, Transport } from '@nestjs/microservices';
import { Advertisement, MessageType } from './dto/Advertisement.dto';
import { ScraperService } from './scraper.service';

@Controller()
export class MessageQueueController {
  private readonly logger: Logger = new Logger(MessageQueueController.name);

  constructor(
    private readonly scraperService: ScraperService,
  ) { }

  @MessagePattern('advertisement', Transport.RMQ)
  async processAdvertisementMessage(@Payload() advertisement: Advertisement, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    switch (advertisement.type) {
      case MessageType.CREATE:
        await this.scraperService.addAdvertisement(advertisement);
        break;
      case MessageType.UPDATE:
        await this.scraperService.editAdvertisement(advertisement);
        break;
      case MessageType.DELETE:
        await this.scraperService.deleteAdvertisement(advertisement.advertisementId);
        break;
      default:
        this.logger.error(`Message rejected, unknown type: ${JSON.stringify(advertisement)}`);
        channel.reject(originalMsg, false);
        return;
    }

    channel.ack(originalMsg);
  }
}
