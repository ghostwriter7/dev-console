export interface TypeInfo {
  type: string;
  methods: MethodInfo[];
}

export interface MethodInfo {
  name: string;
  parameters: string[];
  returnType: string;
}

export const UnknownTypeInfo: TypeInfo = {
  type: 'unknown',
  methods: [],
}
