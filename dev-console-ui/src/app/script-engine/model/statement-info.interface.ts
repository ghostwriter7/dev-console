import { SymbolEntry } from './symbol-entry.interface';

export interface StatementInfo {
  line: number;
  raw: string;
  declaredSymbol?: SymbolEntry;
}
