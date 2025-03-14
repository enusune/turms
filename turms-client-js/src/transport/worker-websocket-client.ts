import WebSocketClient, { EventListener } from './websocket-client';
import SharedContextService, { NotificationType, RequestType } from '../driver/service/shared-context-service';
import WebSocketMetrics from './websocket-metrics';

export default class WorkerWebSocketClient extends WebSocketClient {

    private readonly _sharedContextService: SharedContextService;

    private _isConnecting: boolean;
    private _isConnected: boolean;
    private _metrics = new WebSocketMetrics();

    constructor(url: string, listener: EventListener, sharedContextService: SharedContextService) {
        super(url, listener);
        let connectStart;
        sharedContextService.addNotificationListener(NotificationType.WEBSOCKET_CONNECTING, () => {
            connectStart = new Date().getTime();
            this._isConnecting = true;
            this._isConnected = false;
        });
        sharedContextService.addNotificationListener(NotificationType.WEBSOCKET_CONNECTED, () => {
            this._isConnecting = false;
            this._isConnected = true;
            this._metrics.connectTime = new Date().getTime() - connectStart;
            this.notifyOnOpen();
        });
        sharedContextService.addNotificationListener(NotificationType.WEBSOCKET_CLOSED, notification => {
            this._isConnecting = false;
            this._isConnected = false;
            this._metrics = new WebSocketMetrics();
            this.notifyOnClose({
                code: notification.data.code,
                reason: notification.data.reason
            });
        });
        sharedContextService.addNotificationListener(NotificationType.WEBSOCKET_MESSAGE_RECEIVED, notification => {
            const data = notification.data as ArrayBufferLike;
            this._metrics.dataReceived += data.byteLength;
            this.notifyOnMessage(data);
        });
        this._sharedContextService = sharedContextService;
        sharedContextService.request({
            type: RequestType.CONNECT,
            data: url
        }).then(alreadyCreated => {
            if (alreadyCreated) {
                this.notifyOnOpen();
            }
        }).catch((error: Error) => {
            this.notifyOnClose({
                code: 1006,
                reason: error.message
            });
        });
    }

    override get isConnecting(): boolean {
        return this._isConnecting;
    }

    override get isConnected(): boolean {
        return this._isConnected;
    }

    override get metrics(): WebSocketMetrics {
        return this._metrics;
    }

    override close(): void {
        // do nothing
    }

    override send(data: ArrayBufferLike | Blob | ArrayBufferView): Promise<void> {
        return this._sharedContextService.request({
            type: RequestType.SEND_DATA,
            data
        }).then(() => {
            this._metrics.dataSent += data instanceof Blob ? data.size : data.byteLength;
        });
    }

}