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

package im.turms.server.common.access.http.controller;

import im.turms.server.common.access.http.dto.request.AddJsPluginDTO;
import im.turms.server.common.access.http.dto.request.UpdatePluginDTO;
import im.turms.server.common.access.http.dto.response.ExtensionDTO;
import im.turms.server.common.access.http.dto.response.PluginDTO;
import im.turms.server.common.access.http.dto.response.ResponseDTO;
import im.turms.server.common.access.http.dto.response.ResponseFactory;
import im.turms.server.common.access.http.dto.response.UpdateResultDTO;
import im.turms.server.common.access.http.permission.AdminPermission;
import im.turms.server.common.access.http.permission.RequiredPermission;
import im.turms.server.common.plugin.ExtensionPoint;
import im.turms.server.common.plugin.Plugin;
import im.turms.server.common.plugin.PluginDescriptor;
import im.turms.server.common.plugin.PluginManager;
import im.turms.server.common.plugin.TurmsExtension;
import im.turms.server.common.util.CollectionUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author James Chen
 */
@RestController
@RequestMapping("/plugins")
public class PluginController {

    private final PluginManager pluginManager;

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @GetMapping
    @RequiredPermission(AdminPermission.PLUGIN_QUERY)
    public ResponseEntity<ResponseDTO<Collection<PluginDTO>>> getPlugins(@RequestParam(required = false) Set<String> ids) {
        Collection<Plugin> plugins = CollectionUtil.isEmpty(ids)
                ? pluginManager.getPlugins()
                : pluginManager.getPlugins(ids);
        List<PluginDTO> pluginInfoList = new ArrayList<>(plugins.size());
        for (Plugin plugin : plugins) {
            List<TurmsExtension> extensions = plugin.extensions();
            List<ExtensionDTO> extensionDTOs = new ArrayList<>(extensions.size());
            for (TurmsExtension extension : extensions) {
                List<Class<? extends ExtensionPoint>> classes = pluginManager.getExtensionPoints(extension);
                List<String> classNames = new ArrayList<>(classes.size());
                for (Class<? extends ExtensionPoint> clazz : classes) {
                    classNames.add(clazz.getName());
                }
                extensionDTOs.add(new ExtensionDTO(extension.getClass().getName(),
                        extension.isStarted(),
                        extension.isRunning(),
                        classNames));
            }
            PluginDescriptor descriptor = plugin.descriptor();
            pluginInfoList.add(new PluginDTO(
                    descriptor.getId(),
                    descriptor.getVersion(),
                    descriptor.getProvider(),
                    descriptor.getLicense(),
                    descriptor.getDescription(),
                    extensionDTOs));
        }
        return ResponseFactory.okIfTruthy(pluginInfoList);
    }

    @PutMapping
    @RequiredPermission(AdminPermission.PLUGIN_UPDATE)
    public ResponseEntity<ResponseDTO<UpdateResultDTO>> updatePlugins(
            @RequestParam Set<String> ids,
            @RequestBody UpdatePluginDTO updatePluginDTO) {
        UpdatePluginDTO.PluginStatus status = updatePluginDTO.status();
        if (status == null) {
            return ResponseFactory.okIfTruthy(UpdateResultDTO.NONE);
        }
        long count = switch (status) {
            case STARTED -> pluginManager.startPlugins(ids);
            case STOPPED -> pluginManager.stopPlugins(ids);
            case RESUMED -> pluginManager.resumePlugins(ids);
            case PAUSED -> pluginManager.pausePlugins(ids);
        };
        return ResponseFactory.okIfTruthy(new UpdateResultDTO(count, count));
    }

    @PostMapping("/js")
    @RequiredPermission(AdminPermission.PLUGIN_CREATE)
    public ResponseEntity<ResponseDTO<Void>> createJsPlugins(
            @RequestParam(defaultValue = "false") boolean save,
            @RequestBody AddJsPluginDTO addJsPluginDTO) {
        Set<String> scripts = addJsPluginDTO.scripts();
        pluginManager.loadJsPlugins(scripts, save);
        return ResponseFactory.OK;
    }

    @DeleteMapping
    @RequiredPermission(AdminPermission.PLUGIN_DELETE)
    public ResponseEntity<ResponseDTO<Void>> deletePlugins(
            @RequestParam Set<String> ids,
            @RequestParam(required = false) boolean deleteLocalFiles) {
        pluginManager.deletePlugins(ids, deleteLocalFiles);
        return ResponseFactory.OK;
    }

}