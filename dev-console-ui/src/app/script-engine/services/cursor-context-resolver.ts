import { CursorContext } from '../cursor-context.enum';

export class CursorContextResolver {

  public resolve(statement: string, cursorIndex: number): CursorContext {
    if (cursorIndex === -1) return CursorContext.UNKNOWN;

    const relevantPart = statement.substring(0, cursorIndex - 1).trim();

    if (relevantPart.endsWith('.')) return CursorContext.MEMBER_ACCESS;

    if (relevantPart.endsWith('(')) return CursorContext.INSIDE_CALL;

    return CursorContext.ROOT_SYMBOL;
  }
}
