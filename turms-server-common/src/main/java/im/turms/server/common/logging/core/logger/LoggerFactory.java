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

import im.turms.server.common.cluster.node.NodeType;
import im.turms.server.common.logging.core.appender.Appender;
import im.turms.server.common.logging.core.appender.ConsoleAppender;
import im.turms.server.common.logging.core.appender.file.RollingFileAppender;
import im.turms.server.common.logging.core.layout.TurmsTemplateLayout;
import im.turms.server.common.logging.core.model.LogLevel;
import im.turms.server.common.logging.core.model.LogRecord;
import im.turms.server.common.logging.core.processor.LogProcessor;
import im.turms.server.common.property.env.common.logging.ConsoleLoggingProperties;
import im.turms.server.common.property.env.common.logging.FileLoggingProperties;
import im.turms.server.common.property.env.common.logging.LoggingProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jctools.queues.MpscUnboundedArrayQueue;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoggerFactory {

    private static TurmsTemplateLayout layout;

    @Getter
    private static boolean initialized = false;

    private static final List<Appender> ALL_APPENDERS = new CopyOnWriteArrayList<>();
    private static final List<Appender> DEFAULT_APPENDERS = new ArrayList<>(2);
    private static final MpscUnboundedArrayQueue<LogRecord> QUEUE = new MpscUnboundedArrayQueue<>(1024);
    private static final Queue<Pair<LoggerOptions, WrappedLogger>> UNINITIALIZED_LOGGERS = new LinkedList<>();

    private static String homeDir;
    private static String serverTypeName;
    private static FileLoggingProperties fileLoggingProperties;
    private static ConsoleAppender defaultConsoleAppender;

    @SneakyThrows
    public static synchronized void init(NodeType nodeType, String nodeId, LoggingProperties properties) {
        if (initialized) {
            return;
        }
        homeDir = nodeType == NodeType.SERVICE
                ? System.getenv("TURMS_SERVICE_HOME")
                : System.getenv("TURMS_GATEWAY_HOME");
        if (homeDir == null) {
            homeDir = ".";
        }
        serverTypeName = nodeType == NodeType.SERVICE
                ? "turms-service"
                : "turms-gateway";
        ConsoleLoggingProperties consoleLoggingProperties = properties.getConsole();
        FileLoggingProperties fileLoggingProperties = properties.getFile();
        if (consoleLoggingProperties.isEnabled()) {
            ConsoleAppender consoleAppender = new ConsoleAppender(consoleLoggingProperties.getLevel());
            defaultConsoleAppender = consoleAppender;
            DEFAULT_APPENDERS.add(consoleAppender);
        }
        LoggerFactory.fileLoggingProperties = fileLoggingProperties;
        if (fileLoggingProperties.isEnabled()) {
            RollingFileAppender fileAppender = new RollingFileAppender(fileLoggingProperties.getLevel(),
                    getFilePath(fileLoggingProperties.getFilePath()),
                    fileLoggingProperties.getMaxFiles(),
                    fileLoggingProperties.getMaxFileSizeMb());
            DEFAULT_APPENDERS.add(fileAppender);
        }
        layout = new TurmsTemplateLayout(nodeType, nodeId);
        initialized = true;

        InternalLogger.INSTANCE.init();
        Pair<LoggerOptions, WrappedLogger> pair;
        while ((pair = UNINITIALIZED_LOGGERS.poll()) != null) {
            pair.getSecond().setLogger(getLogger(pair.getFirst()));
        }

        new LogProcessor(QUEUE).start();
    }

    private static synchronized void initForTest() {
        NodeType nodeType = NodeType.GATEWAY;
        try {
            Class.forName("im.turms.service.TurmsServiceApplication");
            nodeType = NodeType.SERVICE;
        } catch (ClassNotFoundException ignored) {
        }

        // We use "INFO" level for tests because:
        // 1. If "DEBUG", in fact we never view these logs because they are too many to view.
        // 2. In some tests, Netty will try to init its internal logger and log DEBUG messages when Netty initializing
        // while our logger will require Netty to init so that we can log, so there is a circular dependency.
        // Use "INFO" can just avoid Netty trying to log when initializing
        init(nodeType, "node-id-test", LoggingProperties.builder()
                .console(new ConsoleLoggingProperties().toBuilder().level(LogLevel.INFO).enabled(true).build())
                .file(new FileLoggingProperties().toBuilder().level(LogLevel.INFO).enabled(true).build())
                .build());
    }

    public static Logger getLogger(String name) {
        return getLogger(LoggerOptions.builder()
                .loggerName(name)
                .shouldParse(true)
                .build());
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(LoggerOptions.builder()
                .loggerClass(clazz)
                .shouldParse(true)
                .build());
    }

    public static synchronized Logger getLogger(LoggerOptions options) {
        if (initialized) {
            String loggerName = options.getLoggerName();
            Class<?> loggerClass = options.getLoggerClass();
            if (loggerName == null && loggerClass != null) {
                loggerName = loggerClass.getName();
            }
            String filePath = options.getFilePath();
            List<Appender> appenders = new ArrayList<>(2);
            if (filePath != null) {
                filePath = getFilePath(filePath);
                LogLevel level = options.getLevel();
                if (level == null) {
                    level = fileLoggingProperties.getLevel();
                }
                RollingFileAppender appender = new RollingFileAppender(level,
                        filePath,
                        fileLoggingProperties.getMaxFiles(),
                        fileLoggingProperties.getMaxFileSizeMb());
                appenders.add(appender);
                ALL_APPENDERS.add(appender);
                if (defaultConsoleAppender != null) {
                    appenders.add(defaultConsoleAppender);
                }
            } else {
                appenders = DEFAULT_APPENDERS;
            }
            return new AsyncLogger(loggerName, options.isShouldParse(), appenders, layout, QUEUE);
        } else if (isJUnitTest()) {
            initForTest();
            return getLogger(options);
        }
        // Spring will access this method via SLF4J before we can initialize LoggerFactory
        // (because we need to wait Spring to parse the properties file for logging)
        // so we need the following code to return a WrappedLogger for them
        WrappedLogger logger = new WrappedLogger();
        UNINITIALIZED_LOGGERS.add(Pair.of(options, logger));
        return logger;
    }

    public static List<Appender> getAllAppenders() {
        return ALL_APPENDERS;
    }

    private static String getFilePath(String path) {
        if (path == null) {
            return ".";
        }
        return path
                .replace("@HOME", homeDir)
                .replace("@SERVICE_TYPE_NAME", serverTypeName);
    }

    private static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }

}
