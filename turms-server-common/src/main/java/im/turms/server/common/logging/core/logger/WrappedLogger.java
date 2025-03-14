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

package im.turms.server.common.logging.core.logger;

import im.turms.server.common.logging.core.model.LogLevel;
import io.netty.buffer.ByteBuf;
import lombok.Setter;

import javax.annotation.Nullable;

/**
 * @author James Chen
 */
public class WrappedLogger implements Logger {

    @Setter
    private Logger logger = NoOpLogger.INSTANCE;

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    @Override
    public boolean isEnabled(LogLevel logLevel) {
        return logger.isEnabled(logLevel);
    }

    @Override
    public void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    @Override
    public void log(LogLevel level, String message, Object... objects) {
        logger.log(level, message, objects);
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void info(ByteBuf message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void error(Throwable throwable) {
        logger.error(throwable);
    }

    @Override
    public void error(String message, @Nullable Throwable throwable) {
        logger.error(message, throwable);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void error(ByteBuf message) {
        logger.error(message);
    }

    @Override
    public void fatal(String message, Object... args) {
        logger.fatal(message, args);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        logger.fatal(message, throwable);
    }

    @Override
    public void fatal(String message) {
        logger.fatal(message);
    }
}
