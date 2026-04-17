export interface TypeInfo {
  name: string;
  methodsByName: Record<string, MethodInfo[]>;
}

export interface MethodInfo {
  parameters: string[];
  returnType: string;
}

export const UnknownTypeInfo: TypeInfo = {
  name: 'unknown',
  methodsByName: {},
}
