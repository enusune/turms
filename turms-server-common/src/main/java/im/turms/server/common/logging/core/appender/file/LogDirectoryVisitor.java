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

package im.turms.server.common.logging.core.appender.file;

import im.turms.server.common.constant.TimeZoneConstant;
import im.turms.server.common.logging.core.logger.InternalLogger;
import lombok.SneakyThrows;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.TreeSet;

import static im.turms.server.common.logging.core.appender.file.RollingFileAppender.FIELD_DELIMITER;

/**
 * @author James Chen
 */
public class LogDirectoryVisitor extends SimpleFileVisitor<Path> {

    private final TreeSet<LogFile> files = new TreeSet<>(Comparator.comparingLong(LogFile::index));

    private final String filePrefix;
    private final String fileSuffix;
    private final String fileMiddle;

    private final DateTimeFormatter fileDateTimeFormatter;

    private final int maxFilesToKeep;
    private final boolean deleteExceedFiles;

    public LogDirectoryVisitor(String filePrefix,
                               String fileSuffix,
                               String fileMiddle,
                               DateTimeFormatter fileDateTimeFormatter,
                               int maxFiles) {
        this.filePrefix = filePrefix;
        this.fileSuffix = fileSuffix;
        this.fileMiddle = fileMiddle;
        this.fileDateTimeFormatter = fileDateTimeFormatter;
        this.maxFilesToKeep = Math.max(1, maxFiles);
        this.deleteExceedFiles = maxFiles > 0;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        try {
            String name = path.getFileName().toString();
            if (!isLogFile(name)) {
                return FileVisitResult.CONTINUE;
            }
            int indexEnd = name.length() - fileSuffix.length();
            int indexStart = name.lastIndexOf(FIELD_DELIMITER, indexEnd - 1);
            if (indexStart == filePrefix.length() + fileMiddle.length() + 1) {
                long index = Long.parseUnsignedLong(name.substring(indexStart + 1, indexEnd));
                TemporalAccessor time = fileDateTimeFormatter
                        .parse(name.substring(filePrefix.length() + 1, indexStart));
                ZonedDateTime timestamp = LocalDate.from(time)
                        .atStartOfDay(TimeZoneConstant.ZONE_ID);
                handleLogFile(path, timestamp, index);
            }
        } catch (Exception e) {
            InternalLogger.INSTANCE.warn("Cannot figure out if a file matches the template: " + path, e);
        }
        return FileVisitResult.CONTINUE;
    }

    private boolean isLogFile(String name) {
        return (name.length() > filePrefix.length() + fileSuffix.length() + fileMiddle.length() + 1)
                && name.startsWith(filePrefix)
                && name.endsWith(fileSuffix);
    }

    private void handleLogFile(Path path, ZonedDateTime timestamp, long index) {
        LogFile file = new LogFile(path, timestamp, index);
        files.add(file);
        if (files.size() > maxFilesToKeep) {
            LogFile firstLogFile = files.first();
            files.remove(firstLogFile);
            if (deleteExceedFiles) {
                try {
                    Files.deleteIfExists(firstLogFile.path());
                } catch (Exception ignored) {
                }
            }
        }
    }

    @SneakyThrows
    public static Deque<LogFile> visit(Path directory,
                                       String prefix,
                                       String suffix,
                                       String middle,
                                       DateTimeFormatter template,
                                       int maxFiles) {
        LogDirectoryVisitor visitor = new LogDirectoryVisitor(prefix, suffix, middle, template, maxFiles);
        Files.walkFileTree(directory, Collections.emptySet(), 1, visitor);
        return new ArrayDeque<>(visitor.files);
    }

}