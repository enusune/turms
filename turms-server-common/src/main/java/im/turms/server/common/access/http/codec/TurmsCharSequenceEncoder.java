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

package im.turms.server.common.access.http.codec;

import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.nio.charset.Charset;
import java.nio.charset.CoderMalfunctionError;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author James Chen
 * @implNote We don't use {@link CharSequenceEncoder} because it will multiply the actual size of text by 3 for UTF-8,
 * and it's disaster for system. e.g. If the size of text output of Prometheus metrics is actually 6000,
 * {@link CharSequenceEncoder} will request a ByteBuf of the size of 18000
 */
public final class TurmsCharSequenceEncoder extends AbstractEncoder<CharSequence> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TurmsCharSequenceEncoder(MimeType... mimeTypes) {
        super(mimeTypes);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> clazz = elementType.toClass();
        return super.canEncode(elementType, mimeType) && CharSequence.class.isAssignableFrom(clazz);
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends CharSequence> inputStream,
                                   DataBufferFactory bufferFactory, ResolvableType elementType,
                                   @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(charSequence ->
                encodeValue(charSequence, bufferFactory, elementType, mimeType, hints));
    }

    @Override
    public DataBuffer encodeValue(CharSequence charSequence, DataBufferFactory bufferFactory,
                                  ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(charSequence, !traceOn);
                return Hints.getLogPrefix(hints) + "Writing " + formatted;
            });
        }
        boolean release = true;
        Charset charset = getCharset(mimeType);
        DataBuffer dataBuffer = bufferFactory.allocateBuffer(charSequence.length());
        try {
            dataBuffer.write(charSequence, charset);
            release = false;
        } catch (CoderMalfunctionError ex) {
            throw new EncodingException("String encoding error: " + ex.getMessage(), ex);
        } finally {
            if (release) {
                DataBufferUtils.release(dataBuffer);
            }
        }
        return dataBuffer;
    }

    private Charset getCharset(@Nullable MimeType mimeType) {
        return mimeType == null || mimeType.getCharset() == null
                ? DEFAULT_CHARSET
                : mimeType.getCharset();
    }

}