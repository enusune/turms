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

package im.turms.server.common.mongo.operation.option;

import im.turms.common.constant.RequestStatus;
import im.turms.server.common.bo.common.DateRange;
import im.turms.server.common.mongo.util.SerializationUtil;
import im.turms.server.common.util.MapUtil;
import org.bson.BsonArrayUtil;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author James Chen
 */
public class Filter implements Bson {

    /**
     * Use {@link BsonDocument} instead of {@link Document}
     * because {@link Document} will be converted to {@link BsonDocument} by mongo-java-driver finally,
     * which is a huge waste of system resources because both documents are heavy
     */
    private final BsonDocument document;

    Filter(int expectedSize) {
        document = new BsonDocument(MapUtil.getCapability(expectedSize));
    }

    public static Filter newBuilder(int expectedSize) {
        return new Filter(expectedSize);
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
        return document;
    }

    /**
     * [start, end)
     */
    public Filter addBetweenIfNotNull(
            @NotNull String key,
            @Nullable DateRange dateRange) {
        if (dateRange != null) {
            Date start = dateRange.start();
            Date end = dateRange.end();
            if (start != null && end == null) {
                document.append(key, new BsonDocument("$gte", new BsonDateTime(start.getTime())));
            } else if (start == null && end != null) {
                document.append(key, new BsonDocument("$lt", new BsonDateTime(end.getTime())));
            } else if (start != null) {
                document.append(key, new BsonDocument()
                        .append("$gte", new BsonDateTime(start.getTime()))
                        .append("$lt", new BsonDateTime(end.getTime())));
            }
        }
        return this;
    }

    public Filter eq(String key, Object value) {
        document.append(key, SerializationUtil.encodeSingleValue(value));
        return this;
    }

    public Filter eqIfFalse(@NotNull String key, @Nullable Object obj, boolean condition) {
        if (!condition) {
            document.append(key, new BsonDocument("$eq", SerializationUtil.encodeSingleValue(obj)));
        }
        return this;
    }

    public Filter eqIfNotNull(@NotNull String key, @Nullable Object obj) {
        if (obj != null) {
            document.append(key, new BsonDocument("$eq", SerializationUtil.encodeSingleValue(obj)));
        }
        return this;
    }

    public Filter gt(String key, Object value) {
        document.append(key, new BsonDocument("$gt", SerializationUtil.encodeSingleValue(value)));
        return this;
    }

    public Filter gtOrNull(String key, Object value) {
        or(Filter.newBuilder(1).eq(key, null),
                Filter.newBuilder(1).gt(key, value));
        return this;
    }

    public Filter gte(String key, Object value) {
        document.append(key, new BsonDocument("$gte", SerializationUtil.encodeSingleValue(value)));
        return this;
    }

    public Filter gteOrNull(String key, Object value) {
        or(Filter.newBuilder(1).eq(key, null),
                Filter.newBuilder(1).gte(key, value));
        return this;
    }

    public <T> Filter in(String key, T... values) {
        document.append(key, new BsonDocument("$in", SerializationUtil.encodeValue(values)));
        return this;
    }

    public <T> Filter in(String key, Collection<T> collection) {
        document.append(key, new BsonDocument("$in", SerializationUtil.encodeValue(collection)));
        return this;
    }

    public Filter inIfNotNull(@NotNull String key, @Nullable Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            document.append(key, new BsonDocument("$in", SerializationUtil.encodeValue(collection)));
        }
        return this;
    }

    public Filter lt(String key, Object value) {
        document.append(key, new BsonDocument("$lt", SerializationUtil.encodeSingleValue(value)));
        return this;
    }

    public Filter ltOrNull(String key, Object value) {
        or(Filter.newBuilder(1).eq(key, null),
                Filter.newBuilder(1).lt(key, value));
        return this;
    }

    public Filter ne(String key, Object value) {
        document.append(key, new BsonDocument("$ne", SerializationUtil.encodeSingleValue(value)));
        return this;
    }

    public Filter neNullIfNotNull(@NotNull String key, @Nullable Object obj) {
        if (obj != null) {
            document.append(key, new BsonDocument("$ne", BsonNull.VALUE));
        }
        return this;
    }

    public Filter or(Filter... filters) {
        List<BsonValue> values = new ArrayList<>(filters.length);
        for (Filter filter : filters) {
            values.add(filter.document);
        }
        document.append("$or", BsonArrayUtil.newArray(values));
        return this;
    }

    // Expiration Support

    public Filter isExpired(String creationDateFieldName,
                            @Nullable Date expirationDate) {
        // If never expire
        if (expirationDate == null) {
            return this;
        }
        BsonValue existingDoc = document.get(creationDateFieldName);
        if (existingDoc instanceof BsonDocument doc) {
            BsonDateTime existingDate = doc.getDateTime("$lt");
            long expirationDateTime = expirationDate.getTime();
            if (expirationDateTime < existingDate.getValue()) {
                doc.append("$lt", new BsonDateTime(expirationDateTime));
            }
        } else {
            lt(creationDateFieldName, expirationDate);
        }
        return this;
    }

    public Filter isExpiredOrNot(Set<RequestStatus> statuses,
                                 String creationDateFieldName,
                                 Date expirationDate) {
        if (statuses == null) {
            return this;
        }
        boolean includesExpired = statuses.contains(RequestStatus.EXPIRED);
        boolean includesNotExpired = statuses.contains(RequestStatus.PENDING);
        if (includesExpired) {
            if (includesNotExpired) {
                return this;
            }
            return isExpired(creationDateFieldName, expirationDate);
        }
        if (includesNotExpired) {
            return isNotExpired(creationDateFieldName, expirationDate);
        }
        return this;
    }

    public Filter isNotExpired(String creationDateFieldName,
                               @Nullable Date expirationDate) {
        // If never expire
        if (expirationDate == null) {
            return this;
        }
        BsonValue existingDoc = document.get(creationDateFieldName);
        if (existingDoc instanceof BsonDocument doc) {
            BsonValue existingDate = doc.get("$gte");
            if (existingDate instanceof BsonDateTime date) {
                if (expirationDate.getTime() > date.getValue()) {
                    doc.append("$gte", new BsonDateTime(expirationDate.getTime()));
                }
            } else {
                if (doc.isEmpty()) {
                    gteOrNull(creationDateFieldName, expirationDate);
                } else {
                    doc.append("$gte", new BsonDateTime(expirationDate.getTime()));
                }
            }
        } else {
            gteOrNull(creationDateFieldName, expirationDate);
        }
        return this;
    }

}