import { v4 as uuid } from 'uuid';
import { ComputeTypeMetadataCommand, TypeMetadataComputedEvent } from '../../model/command-or-event.interface';
import { COMPUTE_TYPE_METADATA_COMMAND } from '../../model/commands';
import { WebSocketService } from '../../services/web-socket/web-socket.service';
import { SymbolEntry } from '../model/symbol-entry.interface';
import { TypeInfo, TypeMetadata } from '../model/type-info.interface';
import { ScriptEngineStore } from '../script-engine.store';

export class SymbolResolver {

  constructor(private readonly scriptEngineStore: InstanceType<typeof ScriptEngineStore>,
              private readonly webSocketService: WebSocketService,) {
  }

  public resolve(symbol: string, localVars: SymbolEntry[], service: string): Promise<TypeMetadata | null> {
    const localSymbol = localVars.find(({ name }) => name === symbol);
    if (localSymbol) return this.resolveTypeInfo(localSymbol.type, service);

    const bindings = this.scriptEngineStore.bindings()[service];
    if (bindings[symbol]) return this.resolveTypeInfo(bindings[symbol], service);

    return Promise.resolve(null);
  }

  public async resolveTypeInfo(type: TypeInfo, service: string): Promise<TypeMetadata> {
    const types = this.scriptEngineStore.typeMetadata()[service];
    const typeInfo = types[type.type];

    if (typeInfo) return Promise.resolve(typeInfo);

    const command: ComputeTypeMetadataCommand = {
      id: uuid(),
      type: COMPUTE_TYPE_METADATA_COMMAND,
      fullName: type.fullName,
      targetServiceName: service,
    }

    const response = await this.webSocketService.sendSync<TypeMetadataComputedEvent>(command);
    this.scriptEngineStore.insertTypeMetadata(service, response.typeMetadata);

    return response.typeMetadata;
  }
}
