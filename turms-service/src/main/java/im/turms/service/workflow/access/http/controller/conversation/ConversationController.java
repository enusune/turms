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

package im.turms.service.workflow.access.http.controller.conversation;

import com.mongodb.client.result.DeleteResult;
import im.turms.server.common.access.http.dto.response.DeleteResultDTO;
import im.turms.server.common.access.http.dto.response.ResponseDTO;
import im.turms.server.common.access.http.dto.response.ResponseFactory;
import im.turms.server.common.access.http.permission.AdminPermission;
import im.turms.server.common.access.http.permission.RequiredPermission;
import im.turms.server.common.dao.util.OperationResultUtil;
import im.turms.server.common.util.CollectionUtil;
import im.turms.server.common.util.CollectorUtil;
import im.turms.service.constant.OperationResultConstant;
import im.turms.service.workflow.access.http.dto.request.conversation.UpdateConversationDTO;
import im.turms.service.workflow.access.http.dto.response.ConversationsDTO;
import im.turms.service.workflow.dao.domain.conversation.GroupConversation;
import im.turms.service.workflow.dao.domain.conversation.PrivateConversation;
import im.turms.service.workflow.service.impl.conversation.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author James Chen
 */
@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    @RequiredPermission(AdminPermission.CONVERSATION_QUERY)
    public Mono<ResponseEntity<ResponseDTO<ConversationsDTO>>> queryConversations(
            PrivateConversation.KeyList privateConversationKeys,
            @RequestParam(required = false) Set<Long> ownerIds,
            @RequestParam(required = false) Set<Long> groupIds) {
        Flux<PrivateConversation> privateConversationsFlux;
        int privateConversationsSize = 0;
        if (isEmptyPrivateConversationKeys(privateConversationKeys)) {
            privateConversationsFlux = Flux.empty();
        } else {
            List<PrivateConversation.Key> keys = privateConversationKeys.getPrivateConversationKeys();
            privateConversationsFlux = conversationService.queryPrivateConversations(CollectionUtil.newSet(keys));
            privateConversationsSize += keys.size();
        }
        if (ownerIds != null && !ownerIds.isEmpty()) {
            privateConversationsSize += ownerIds.size();
            privateConversationsFlux =
                    privateConversationsFlux.concatWith(conversationService.queryPrivateConversationsByOwnerIds(ownerIds));
        }
        Mono<List<PrivateConversation>> privateConversations =
                privateConversationsFlux.collect(CollectorUtil.toList(privateConversationsSize));
        Mono<List<GroupConversation>> groupConversations = groupIds == null || groupIds.isEmpty()
                ? Mono.just(Collections.emptyList())
                : conversationService.queryGroupConversations(groupIds)
                .collect(CollectorUtil.toList(groupIds.size()));
        Mono<ConversationsDTO> conversationsMono = privateConversations.zipWith(groupConversations)
                .map(tuple -> new ConversationsDTO(tuple.getT1(), tuple.getT2()));
        return ResponseFactory.okIfTruthy(conversationsMono);
    }

    @DeleteMapping
    @RequiredPermission(AdminPermission.CONVERSATION_DELETE)
    public Mono<ResponseEntity<ResponseDTO<DeleteResultDTO>>> deleteConversations(
            PrivateConversation.KeyList privateConversationKeys,
            @RequestParam(required = false) Set<Long> ownerIds,
            @RequestParam(required = false) Set<Long> groupIds) {
        Mono<DeleteResult> resultMono = isEmptyPrivateConversationKeys(privateConversationKeys)
                ? Mono.just(OperationResultConstant.ACKNOWLEDGED_DELETE_RESULT)
                : conversationService
                .deletePrivateConversations(CollectionUtil.newSet(privateConversationKeys.getPrivateConversationKeys()));
        if (!CollectionUtils.isEmpty(ownerIds)) {
            resultMono = resultMono.zipWith(conversationService.deletePrivateConversations(ownerIds, null))
                    .map(tuple -> OperationResultUtil.merge(tuple.getT1(), tuple.getT2()));
        }
        if (!CollectionUtils.isEmpty(groupIds)) {
            resultMono = resultMono.zipWith(conversationService.deleteGroupConversations(groupIds, null))
                    .map(tuple -> OperationResultUtil.merge(tuple.getT1(), tuple.getT2()));
        }
        return ResponseFactory.deleteResult(resultMono);
    }

    @PutMapping
    @RequiredPermission(AdminPermission.CONVERSATION_UPDATE)
    public Mono<ResponseEntity<ResponseDTO<Void>>> updateConversations(
            PrivateConversation.KeyList privateConversationKeys,
            GroupConversation.GroupConversionMemberKey.KeyList groupConversationMemberKeys,
            @RequestBody UpdateConversationDTO updateConversationDTO) {
        Mono<Void> updatePrivateConversions = isEmptyPrivateConversationKeys(privateConversationKeys)
                ? Mono.empty()
                : conversationService
                .upsertPrivateConversationsReadDate(CollectionUtil.newSet(privateConversationKeys.getPrivateConversationKeys()),
                        updateConversationDTO.readDate());
        Mono<Void> updateGroupConversationsMono = isEmptyGroupConversationKeys(groupConversationMemberKeys)
                ? Mono.empty()
                : conversationService
                .upsertGroupConversationsReadDate(CollectionUtil.newSet(groupConversationMemberKeys.getGroupConversationMemberKeys()),
                        updateConversationDTO.readDate());
        return Mono.whenDelayError(updatePrivateConversions, updateGroupConversationsMono)
                .thenReturn(ResponseFactory.OK);
    }

    private boolean isEmptyPrivateConversationKeys(PrivateConversation.KeyList keys) {
        if (keys == null) {
            return true;
        }
        List<PrivateConversation.Key> list = keys.getPrivateConversationKeys();
        return list == null || list.isEmpty();
    }

    private boolean isEmptyGroupConversationKeys(GroupConversation.GroupConversionMemberKey.KeyList keys) {
        if (keys == null) {
            return true;
        }
        List<GroupConversation.GroupConversionMemberKey> list = keys.getGroupConversationMemberKeys();
        return list == null || list.isEmpty();
    }

}