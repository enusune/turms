/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.redis;

import im.turms.server.common.bo.location.Coordinates;
import im.turms.server.common.constant.ThreadNameConstant;
import im.turms.server.common.redis.codec.TurmsRedisCodecAdapter;
import im.turms.server.common.redis.codec.context.RedisCodecContext;
import im.turms.server.common.redis.command.TurmsCommandEncoder;
import im.turms.server.common.redis.script.RedisScript;
import im.turms.server.common.util.ByteBufUtil;
import io.lettuce.core.AbstractRedisReactiveCommands;
import io.lettuce.core.GeoArgs;
import io.lettuce.core.GeoCoordinates;
import io.lettuce.core.GeoWithin;
import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisCommandExecutionException;
import io.lettuce.core.RedisNoScriptException;
import io.lettuce.core.RedisReactiveCommandsImpl;
import io.lettuce.core.TurmsRedisCommandBuilder;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.protocol.CommandEncoder;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.resource.DefaultEventLoopGroupProvider;
import io.lettuce.core.resource.NettyCustomizer;
import io.lettuce.core.resource.ThreadFactoryProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

import static io.lettuce.core.protocol.CommandType.GEORADIUSBYMEMBER;

/**
 * @author James Chen
 * @implNote 1. For Redis commands, we MUST ensure the key/val buffers are released in "doFinally"
 * by ourselves because if a command is cancelled or fails, Lettuce won't release these buffers
 * because it hasn't flushed the buffers.
 * 2. Ensure encoding data in a cold publisher instead of a hot publisher,
 * or the memory may leak because the cold finalizer will never be called
 * if it's won't be subscribed (this may happen in the scenario: when the previous
 * publisher fail, the next publisher will never be subscribed)
 * @see AbstractRedisReactiveCommands
 */
@Data
public class TurmsRedisClient {

    private final DefaultClientResources resources;

    private final RedisClient nativeClient;

    private final StatefulRedisConnection<ByteBuf, ByteBuf> nativeConnection;

    private final RedisReactiveCommandsImpl<ByteBuf, ByteBuf> commands;

    private final TurmsRedisCommandBuilder commandBuilder;

    private final RedisCodecContext serializationContext;

    public TurmsRedisClient(String uri,
                            RedisCodecContext serializationContext) {
        this.serializationContext = serializationContext;
        commandBuilder = new TurmsRedisCommandBuilder(serializationContext);
        DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(DefaultClientResources.MIN_COMPUTATION_THREADS,
                new DefaultThreadFactory(ThreadNameConstant.LETTUCE_EVENT_LOOP));
        DefaultEventLoopGroupProvider eventLoopGroupProvider =
                new DefaultEventLoopGroupProvider(DefaultClientResources.MIN_IO_THREADS,
                        (ThreadFactoryProvider) poolName -> new DefaultThreadFactory(poolName, true));
        resources = DefaultClientResources
                .builder()
                // For event bus: io.lettuce.core.event.DefaultEventBus
                .eventExecutorGroup(eventExecutorGroup)
                .eventLoopGroupProvider(eventLoopGroupProvider)
                .nettyCustomizer(new NettyCustomizer() {
                    /**
                     * ConnectionBuilder$PlainChannelInitializer
                     * ChannelGroupListener
                     * CommandEncoder
                     * RedisHandshakeHandler
                     * CommandHandler
                     * ConnectionEventTrigger
                     * ConnectionWatchdog
                     * @see io.lettuce.core.SslConnectionBuilder.SslChannelInitializer
                     * @see io.lettuce.core.SslConnectionBuilder.PlainChannelInitializer
                     */
                    @Override
                    public void afterChannelInitialized(Channel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.replace(CommandEncoder.class, null, new TurmsCommandEncoder());
                    }
                })
                .build();
        nativeClient = RedisClient.create(resources, uri);
        nativeConnection = nativeClient.connect(TurmsRedisCodecAdapter.DEFAULT);
        commands = (RedisReactiveCommandsImpl<ByteBuf, ByteBuf>) nativeConnection.reactive();
    }

    public void destroy() {
        nativeClient.shutdown();
    }

    public Mono<Long> del(Collection<ByteBuf> keys) {
        return commands.del(keys);
    }

    public Mono<Long> incr(ByteBuf key) {
        return commands.incr(key);
    }

    // Hashes

    public Mono<Long> hdel(Object key, Object... fields) {
        return Mono.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeHashKey(key);
            ByteBuf[] fieldBuffers = serializationContext.encodeHashFields(fields);
            return commands.hdel(keyBuffer, fieldBuffers)
                    .doFinally(signal -> {
                        ByteBufUtil.ensureReleased(keyBuffer);
                        ByteBufUtil.ensureReleased(fieldBuffers);
                    });
        });
    }

    public <K, V> Flux<Map.Entry<K, V>> hgetall(K key) {
        return Flux.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeHashKey(key);
            Flux<KeyValue<K, V>> flux = commands.createDissolvingFlux(() -> commandBuilder.hgetall(keyBuffer));
            Flux<Map.Entry<K, V>> entryFlux = flux
                    .flatMap(entry -> {
                        if (entry.isEmpty()) {
                            return Mono.empty();
                        }
                        return Mono.just(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
                    });
            return entryFlux
                    .doFinally(signal -> ByteBufUtil.ensureReleased(keyBuffer));
        });
    }

    // Geo

    public Mono<Long> geoadd(Object key, Coordinates coordinates, Object member) {
        return Mono.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeGeoKey(key);
            ByteBuf memberBuffer = serializationContext.encodeGeoMember(member);
            return commands.geoadd(keyBuffer, coordinates.longitude(), coordinates.latitude(), memberBuffer)
                    .doFinally(signal -> {
                        ByteBufUtil.ensureReleased(keyBuffer);
                        ByteBufUtil.ensureReleased(memberBuffer);
                    });
        });
    }

    public Flux<GeoCoordinates> geopos(Object key, Object... members) {
        return Flux.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeGeoKey(key);
            ByteBuf[] memberBuffers = serializationContext.encodeGeoMembers(members);
            return commands.geopos(keyBuffer, memberBuffers)
                    .flatMap(value -> value.isEmpty() ? Mono.empty() : Mono.just(value.getValue()))
                    .doFinally(signal -> {
                        ByteBufUtil.ensureReleased(keyBuffer);
                        ByteBufUtil.ensureReleased(memberBuffers);
                    });
        });
    }

    public <T> Flux<GeoWithin<T>> georadiusbymember(Object key, Object member, double distanceMeters, GeoArgs geoArgs) {
        return Flux.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeGeoKey(key);
            ByteBuf memberBuffer = serializationContext.encodeGeoMember(member);
            Flux<GeoWithin<T>> flux = commands.createDissolvingFlux(() -> commandBuilder
                    .georadiusbymember(GEORADIUSBYMEMBER, keyBuffer, memberBuffer, distanceMeters, GeoArgs.Unit.m.name(), geoArgs));
            return flux
                    .onErrorResume(RedisCommandExecutionException.class, e -> {
                        String message = e.getMessage();
                        if (message != null && message.endsWith("could not decode requested zset member")) {
                            return Flux.empty();
                        }
                        return Flux.error(e);
                    })
                    .doFinally(signal -> {
                        ByteBufUtil.ensureReleased(keyBuffer);
                        ByteBufUtil.ensureReleased(memberBuffer);
                    });
        });
    }

    public Mono<Long> georem(Object key, Object... members) {
        return Mono.defer(() -> {
            ByteBuf keyBuffer = serializationContext.encodeGeoKey(key);
            ByteBuf[] memberBuffers = serializationContext.encodeGeoMembers(members);
            return commands.zrem(keyBuffer, memberBuffers)
                    .doFinally(signal -> {
                        ByteBufUtil.ensureReleased(keyBuffer);
                        ByteBufUtil.ensureReleased(memberBuffers);
                    });
        });
    }

    // Scripting

    public <T> Mono<T> eval(RedisScript script, ByteBuf... keys) {
        return eval(script, keys.length, keys);
    }

    /**
     * @param keyLength the real key length
     */
    public <T> Mono<T> eval(RedisScript script, int keyLength, ByteBuf... keys) {
        return Mono.defer(() -> {
            for (int i = 0; i < keys.length; i++) {
                keys[i] = ByteBufUtil.ensureByteBufRefCnfCorrect(keys[i])
                        .retain();
            }
            return (Mono<T>) commands
                    .createFlux(() -> commandBuilder.evalsha(script.digest(), script.outputType(), keys, keyLength))
                    .onErrorResume(e -> {
                        if (exceptionContainsNoScriptException(e)) {
                            return commands
                                    .createFlux(() -> commandBuilder.eval(script.script(), script.outputType(), keys, keyLength));
                        }
                        return Flux.error(e);
                    })
                    .doFinally(signal -> ByteBufUtil.ensureReleased(keys))
                    .single();
        });
    }

    private boolean exceptionContainsNoScriptException(Throwable e) {
        if (e instanceof RedisNoScriptException) {
            return true;
        }
        Throwable current = e.getCause();
        while (current != null) {
            if (current instanceof RedisNoScriptException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

}