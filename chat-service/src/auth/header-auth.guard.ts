import { CanActivate, ExecutionContext, Injectable, UnauthorizedException } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { IS_PUBLIC_KEY } from './public.decorator';
import { GqlExecutionContext } from '@nestjs/graphql';

@Injectable()
export class HeaderAuthGuard implements CanActivate {

  constructor(private readonly reflector: Reflector) { }

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (isPublic) {
      return true;
    }

    const ctx = GqlExecutionContext.create(context);
    const { req } = ctx.getContext();
    const header = req.headers['x-user-data'];

    try {
      ctx.getContext().user = JSON.parse(Buffer.from(header, 'base64').toString());
    } catch {
      throw new UnauthorizedException();
    }

    return true;
  }
}