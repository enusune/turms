import 'dart:async';
import 'dart:math';

import '../../model/notification/turms_notification.pb.dart';
import '../../model/turms_business_exception.dart';
import '../../model/turms_status_code.dart';
import '../state_store.dart';
import 'base_service.dart';

class HeartbeatService extends BaseService {
  static const _defaultHeartbeatIntervalMillis = 120 * 1000;
  static const _heartbeatFailureRequestId = -100;
  static final List<int> _heartbeatRequest = [0];

  final int _heartbeatIntervalMillis;
  final Duration _heartbeatTimerInterval;
  int _lastHeartbeatRequestDate = 0;
  Timer? _heartbeatTimer;
  final List<Completer<void>> _heartbeatCompleters = [];

  HeartbeatService(StateStore stateStore, int? heartbeatIntervalMillis)
      : _heartbeatIntervalMillis =
            heartbeatIntervalMillis ?? _defaultHeartbeatIntervalMillis,
        _heartbeatTimerInterval = Duration(
            milliseconds: max(
                1,
                (heartbeatIntervalMillis ?? _defaultHeartbeatIntervalMillis) ~/
                    10)),
        super(stateStore);

  bool get isRunning => _heartbeatTimer?.isActive == true;

  void start() {
    if (isRunning) {
      return;
    }
    _heartbeatTimer = Timer.periodic(
      _heartbeatTimerInterval,
      (_) {
        final now = DateTime.now().millisecondsSinceEpoch;
        final difference = min(
            now - stateStore.lastRequestDate, now - _lastHeartbeatRequestDate);
        if (difference > _heartbeatIntervalMillis) {
          send();
          _lastHeartbeatRequestDate = now;
        }
      },
    );
  }

  void stop() => _heartbeatTimer?.cancel();

  Future<void> send() async {
    if (!stateStore.isConnected || !stateStore.isSessionOpen) {
      throw TurmsBusinessException.fromCode(
          TurmsStatusCode.clientSessionHasBeenClosed);
    }
    stateStore.tcp!.write(_heartbeatRequest);
    final completer = Completer<void>();
    _heartbeatCompleters.add(completer);
    return completer.future;
  }

  void resolveHeartbeatCompleters() {
    _heartbeatCompleters.removeWhere((completer) {
      completer.complete();
      return true;
    });
  }

  bool rejectHeartbeatCompletersIfFail(TurmsNotification notification) {
    if (_heartbeatFailureRequestId == notification.requestId.toInt()) {
      _rejectHeartbeatCompleters(
          TurmsBusinessException.fromNotification(notification));
      return true;
    }
    return false;
  }

  void _rejectHeartbeatCompleters(TurmsBusinessException exception) {
    _heartbeatCompleters.removeWhere((completer) {
      completer.completeError(exception);
      return true;
    });
  }

  @override
  Future<void> close() {
    onDisconnected();
    return Future.value();
  }

  @override
  void onDisconnected() {
    stop();
    final exception = TurmsBusinessException.fromCode(
        TurmsStatusCode.clientSessionHasBeenClosed);
    _rejectHeartbeatCompleters(exception);
  }
}
