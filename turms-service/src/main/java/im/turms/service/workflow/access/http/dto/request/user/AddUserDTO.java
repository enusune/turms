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

package im.turms.service.workflow.access.http.dto.request.user;

import im.turms.common.constant.ProfileAccessStrategy;
import im.turms.service.constant.SecurityConstant;

import java.util.Date;

/**
 * @author James Chen
 */
public record AddUserDTO(
        Long id,
        String password,
        String name,
        String intro,
        ProfileAccessStrategy profileAccess,
        Long permissionGroupId,
        Date registrationDate,
        Boolean isActive
) {
    @Override
    public String toString() {
        return "AddUserDTO[" +
                "id=" + id +
                ", password=" + SecurityConstant.SENSITIVE_VALUE +
                ", name=" + name +
                ", intro=" + intro +
                ", profileAccess=" + profileAccess +
                ", permissionGroupId=" + permissionGroupId +
                ", registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ']';
    }
}
