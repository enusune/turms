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

package im.turms.server.common.dao.domain;

import im.turms.server.common.access.http.permission.AdminPermission;
import im.turms.server.common.mongo.entity.annotation.Document;
import im.turms.server.common.mongo.entity.annotation.Field;
import im.turms.server.common.mongo.entity.annotation.Id;
import lombok.Data;

import java.util.Set;

/**
 * <pre>
 * Note that there is always a built-in immutable root role:
 * {
 *     id: 0,
 *     name: "ROOT",
 *     permissions: AdminPermission.ALL,
 *     rank: Integer.MAX_VALUE
 * }
 * </pre>
 *
 * @author James Chen
 */
@Data
@Document(AdminRole.COLLECTION_NAME)
public final class AdminRole {

    public static final String COLLECTION_NAME = "adminRole";

    @Id
    private final Long id;

    @Field(Fields.NAME)
    private final String name;

    @Field(Fields.PERMISSIONS)
    private final Set<AdminPermission> permissions;

    /**
     * Only the higher-ranking admins can add/delete/update lower-ranking admins' information.
     */
    @Field(Fields.RANK)
    private final Integer rank;

    public static final class Fields {
        public static final String NAME = "n";
        public static final String PERMISSIONS = "perm";
        public static final String RANK = "rank";

        private Fields() {
        }
    }
}