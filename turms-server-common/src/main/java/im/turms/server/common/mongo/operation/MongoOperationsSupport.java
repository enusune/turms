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

package im.turms.server.common.mongo.operation;

import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import im.turms.server.common.mongo.entity.MongoEntity;
import im.turms.server.common.mongo.entity.annotation.CompoundIndex;
import im.turms.server.common.mongo.model.Tag;
import im.turms.server.common.mongo.operation.option.Filter;
import im.turms.server.common.mongo.operation.option.QueryOptions;
import im.turms.server.common.mongo.operation.option.Update;
import org.bson.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author James Chen
 */
public interface MongoOperationsSupport {

    <T> Mono<T> findById(Class<T> clazz, Object id);

    <T> Mono<T> findOne(Class<T> clazz);

    <T> Mono<T> findOne(Class<T> clazz, Filter filter);

    <T> Mono<T> findOne(Class<T> clazz, Filter filter, @Nullable QueryOptions options);

    <T> Flux<T> findMany(Class<T> clazz, Filter filter);

    <T> Flux<T> findMany(Class<T> clazz, Filter filter, @Nullable QueryOptions options);

    <T> Flux<T> findAll(Class<T> clazz);

    <T> Flux<T> findAll(Class<T> clazz, @Nullable QueryOptions options);

    <T> Mono<Boolean> exists(Class<T> clazz, Filter filter);

    <T> Mono<Long> count(Class<T> clazz, Filter filter);

    <T> Mono<Long> countAll(Class<T> clazz);

    <T> Mono<Void> upsert(T o);

    <T> Mono<Void> upsert(ClientSession session, T o);

    <T> Mono<Void> upsert(Class<T> clazz, Filter filter, Update update);

    <T> Mono<Void> insert(T value);

    <T> Mono<Void> insert(@Nullable ClientSession session, T value);

    Mono<Void> insertAll(List<?> values);

    Mono<Void> insertAllOfSameType(List<?> values);

    <T> Mono<UpdateResult> updateOne(Class<T> clazz, Filter filter, Update update);

    <T> Mono<UpdateResult> updateOne(@Nullable ClientSession session, Class<T> clazz, Filter filter, Update update);

    <T> Mono<UpdateResult> updateMany(Class<T> clazz, Filter filter, Update update);

    <T> Mono<UpdateResult> updateMany(@Nullable ClientSession session, Class<T> clazz, Filter filter, Update update);

    <T> Mono<DeleteResult> deleteOne(@Nullable ClientSession session, Class<T> clazz, Filter filter);

    <T> Mono<DeleteResult> deleteOne(Class<T> clazz, Filter filter);

    <T> Mono<DeleteResult> deleteMany(@Nullable ClientSession session, Class<T> clazz, Filter filter);

    <T> Mono<DeleteResult> deleteMany(Class<T> clazz, Filter filter);

    <T> Mono<DeleteResult> deleteAll(Class<T> clazz);

    <T> Flux<ChangeStreamDocument<T>> watch(Class<T> clazz, FullDocument fullDocument);

    <T> Mono<Long> countDistinct(Class<T> clazz, Filter filter, String groupByFieldName);

    <T> Mono<Void> ensureIndexes(Class<T> clazz, List<IndexModel> indexModels);

    <T> Flux<Document> listIndexes(Class<T> clazz);

    Mono<List<Tag>> findTags(String collectionName);

    Mono<Void> deleteTags(String collectionName);

    Mono<Void> enableSharding();

    Mono<Void> shard(MongoEntity<?> entity);

    Mono<Void> ensureIndexesAndShard(Collection<Class<?>> classes);

    Mono<Void> ensureIndexesAndShard(Collection<Class<?>> classes,
                                     @Nullable BiPredicate<Class<?>, CompoundIndex> customCompoundIndexFilter,
                                     @Nullable BiPredicate<Class<?>, Field> customIndexFilter);

    Mono<Void> addShardToZone(String shardName, String zoneName);

    Mono<Void> updateZoneKeyRange(String collectionName,
                                  String zoneName,
                                  Document minimum,
                                  Document maximum);

    Mono<Void> createCollection(Class<?> clazz);

    Mono<Boolean> collectionExists(Class<?> clazz);

    Mono<Void> dropDatabase();

    Mono<Boolean> validate(Class<?> clazz, String jsonSchema);

    <T> Mono<T> inTransaction(Function<ClientSession, Mono<T>> execute);

    Mono<Void> disableBalancing(String collectionName);

    Mono<Void> enableBalancing(String collectionName);

    Mono<Boolean> isBalancerRunning();

}