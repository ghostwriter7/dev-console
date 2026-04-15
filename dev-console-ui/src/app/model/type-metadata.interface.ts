export interface TypeMetadata {
  type: string;
  methods: MethodMetadata[];
}

export interface MethodMetadata {
  name: string;
  returnType: string;
  parameters: string[];
}
