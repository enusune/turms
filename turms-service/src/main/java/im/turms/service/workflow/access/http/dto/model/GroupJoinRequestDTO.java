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

package im.turms.service.workflow.access.http.dto.model;

import im.turms.service.workflow.dao.domain.group.GroupJoinRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

/**
 * @author James Chen
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupJoinRequestDTO extends GroupJoinRequest {

    @Getter
    private final Date expirationDate;

    public GroupJoinRequestDTO(GroupJoinRequest request, Date expirationDate) {
        super(request.getId(),
                request.getContent(),
                request.getStatus(),
                request.getCreationDate(),
                request.getResponseDate(),
                request.getGroupId(),
                request.getRequesterId(),
                request.getResponderId());
        this.expirationDate = expirationDate;
    }

}