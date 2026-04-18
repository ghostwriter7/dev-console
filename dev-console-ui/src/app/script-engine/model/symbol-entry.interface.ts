import { TypeInfo } from './type-info.interface';

/**
 * Symbol represents a local or global binding.
 * @Example:
 *  {
 *    "name": "admin,
 *    "type": "User"
 *  }
 */
export interface SymbolEntry {
  name: string;
  type: TypeInfo;
}
