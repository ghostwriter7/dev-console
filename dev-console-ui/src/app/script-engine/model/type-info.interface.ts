export interface TypeMetadata extends TypeInfo {
  methods: MethodInfo[];
}

export interface MethodInfo {
  name: string;
  parameters: TypeInfo[];
  returnType: TypeInfo;
}

export interface TypeInfo {
  type: string;
  fullName: string;
}

export const UnknownTypeInfo: TypeMetadata = {
  type: 'unknown',
  fullName: 'unknown',
  methods: [],
}
