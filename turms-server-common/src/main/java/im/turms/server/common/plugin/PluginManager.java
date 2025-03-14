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

package im.turms.server.common.plugin;

import im.turms.server.common.context.TurmsApplicationContext;
import im.turms.server.common.logging.core.logger.Logger;
import im.turms.server.common.logging.core.logger.LoggerFactory;
import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.property.env.common.PluginProperties;
import im.turms.server.common.security.MessageDigestPool;
import im.turms.server.common.util.ClassUtil;
import im.turms.server.common.util.CollectionUtil;
import im.turms.server.common.util.ThrowableUtil;
import io.lettuce.core.codec.Base16;
import lombok.Getter;
import lombok.SneakyThrows;
import org.graalvm.polyglot.Engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

/**
 * @author James Chen
 */
@Component
public class PluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

    @Getter
    private final boolean enabled;
    private final boolean isJsScriptEnabled;
    private final Path pluginDir;

    private final PluginRepository pluginRepository;
    private final ApplicationContext context;
    /**
     * The object is {@link Engine} in fact,
     * but we don't declare it as {@link Engine} because it's an optional class
     * and Spring will throw when creating the bean when it cannot find the class
     */
    @Nullable
    private Object engine;

    @SneakyThrows
    public PluginManager(ApplicationContext context,
                         TurmsApplicationContext applicationContext,
                         TurmsPropertiesManager propertiesManager,
                         @Autowired(required = false) Set<Class<? extends ExtensionPoint>> singletonExtensionPoints) {
        this.context = context;
        PluginProperties pluginProperties = propertiesManager.getLocalProperties().getPlugin();
        enabled = pluginProperties.isEnabled();
        pluginRepository = new PluginRepository(singletonExtensionPoints == null
                ? Collections.emptySet()
                : singletonExtensionPoints);
        pluginDir = getPluginDir(applicationContext.getHome(), pluginProperties.getDir());
        isJsScriptEnabled = ClassUtil.exists("org.graalvm.polyglot.Engine");
        PluginFinder.FindResult findResult = PluginFinder.find(pluginDir, isJsScriptEnabled);
        loadJavaPlugins(findResult.zipFiles());
        if (isJsScriptEnabled) {
            engine = Engine.newBuilder()
                    .option("engine.WarnInterpreterOnly", "false")
                    .build();
            loadJsPlugins(findResult.jsFiles());
        }
        if (enabled) {
            startPlugins();
        }
    }

    private Path getPluginDir(Path home, String pluginsDir) {
        return home.resolve(pluginsDir).toAbsolutePath();
    }

    private void startPlugins() {
        for (Plugin plugin : getPlugins()) {
            for (TurmsExtension extension : plugin.extensions()) {
                try {
                    extension.start();
                } catch (Exception e) {
                    LOGGER.error("Caught an error while starting the extension " + extension.getClass().getName(), e);
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        for (Plugin plugin : getPlugins()) {
            for (TurmsExtension extension : plugin.extensions()) {
                try {
                    extension.stop();
                } catch (Exception e) {
                    LOGGER.error("Caught an error while stopping the extension " + extension.getClass().getName(), e);
                }
            }
        }
        if (engine != null) {
            ((Engine) engine).close();
        }
    }

    public void loadJavaPlugins(List<ZipFile> zipFiles) throws ClassNotFoundException {
        List<JavaPluginDescriptor> descriptors = JavaPluginDescriptorFactory.load(zipFiles);
        for (JavaPluginDescriptor descriptor : descriptors) {
            Plugin plugin = JavaPluginFactory.create(descriptor, context);
            pluginRepository.register(plugin);
        }
    }

    public void loadJsPlugins(Collection<JsFile> files) {
        if (CollectionUtil.isEmpty(files)) {
            return;
        }
        for (JsFile file : files) {
            loadJsPlugin(file.script(), file.path());
        }
    }

    public void loadJsPlugins(Collection<String> scripts, boolean save) {
        if (CollectionUtil.isEmpty(scripts)) {
            return;
        }
        for (String script : scripts) {
            Path path = null;
            if (save) {
                path = savePlugin(script);
            }
            loadJsPlugin(script, path);
        }
    }

    public void loadJsPlugin(String script, Path path) {
        if (!isJsScriptEnabled) {
            throw new UnsupportedOperationException("JavaScript plugins are disabled because the classes of GraalJS aren't loaded");
        }
        JsPlugin jsPlugin = JsPluginFactory.create((Engine) engine, script, path);
        Plugin plugin = JavaPluginFactory.create(jsPlugin.descriptor(), jsPlugin.extensions(), context);
        pluginRepository.register(plugin);
    }

    @SneakyThrows
    public <T> T loadProperties(Class<T> propertiesClass) {
        T properties = propertiesClass.getDeclaredConstructor().newInstance();
        String s = propertiesClass.getName() + UUID.randomUUID();
        return (T) context.getBean(ConfigurationPropertiesBindingPostProcessor.class)
                .postProcessBeforeInitialization(properties, s);
    }

    @SneakyThrows
    private Path savePlugin(String script) {
        byte[] bytes = script.getBytes(StandardCharsets.UTF_8);
        byte[] digest = MessageDigestPool.getSha1().digest(bytes);
        String name = new String(Base16.encode(digest, false));
        Path path = pluginDir.resolve("_network_" + name + ".js");
        synchronized (this) {
            if (Files.notExists(path)) {
                Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
            }
        }
        return path;
    }

    public <T extends ExtensionPoint> List<T> getExtensionPoints(Class<T> clazz) {
        return pluginRepository.getExtensionPoints(clazz);
    }

    public List<Class<? extends ExtensionPoint>> getExtensionPoints(TurmsExtension extension) {
        return extension.getExtensionPointClasses();
    }

    public Collection<Plugin> getPlugins() {
        return pluginRepository.getPlugins();
    }

    public List<Plugin> getPlugins(Set<String> ids) {
        return pluginRepository.getPlugins(ids);
    }

    public int startPlugins(Set<String> ids) {
        List<Plugin> plugins = pluginRepository.getPlugins(ids);
        return executeOnExtensions(plugins, TurmsExtension::start, "starting", "Caught errors while starting some extensions");
    }

    public int stopPlugins(Set<String> ids) {
        List<Plugin> plugins = pluginRepository.getPlugins(ids);
        return stopPlugins(plugins);
    }

    public int stopPlugins(List<Plugin> plugins) {
        return executeOnExtensions(plugins, TurmsExtension::stop, "stopping", "Caught errors while stopping some extensions");
    }

    public int resumePlugins(Set<String> ids) {
        List<Plugin> plugins = pluginRepository.getPlugins(ids);
        return executeOnExtensions(plugins, TurmsExtension::resume, "resuming", "Caught errors while resuming some extensions");
    }

    public int pausePlugins(Set<String> ids) {
        List<Plugin> plugins = pluginRepository.getPlugins(ids);
        return executeOnExtensions(plugins, TurmsExtension::pause, "pausing", "Caught errors while pausing some extensions");
    }

    private int executeOnExtensions(List<Plugin> plugins,
                                    Consumer<TurmsExtension> consumer,
                                    String action,
                                    String errorMessage) {
        List<Runnable> runnables = new ArrayList<>(plugins.size() * 2);
        for (Plugin plugin : plugins) {
            for (TurmsExtension extension : plugin.extensions()) {
                runnables.add(() -> {
                    try {
                        consumer.accept(extension);
                    } catch (Exception e) {
                        throw new RuntimeException("Caught an error while %s the extension %s from the plugin %s"
                                .formatted(action, extension.getClass().getName(), plugin.descriptor().getId()), e);
                    }
                });
            }
        }
        ThrowableUtil.delayError(runnables, errorMessage);
        return plugins.size();
    }

    public void deletePlugins(Set<String> ids, boolean deleteLocalFiles) {
        List<Plugin> plugins = pluginRepository.removePlugins(ids);
        stopPlugins(plugins);
        if (deleteLocalFiles) {
            for (Plugin plugin : plugins) {
                try {
                    Path path = plugin.descriptor().getPath();
                    if (path != null) {
                        Files.deleteIfExists(path);
                    }
                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }

    public <T extends ExtensionPoint> boolean hasRunningExtensions(Class<T> extensionPointClass) {
        return pluginRepository.hasRunningExtensions(extensionPointClass);
    }

    public <T extends ExtensionPoint, R> Mono<R> invokeFirstExtensionPoint(Class<T> extensionPointClass,
                                                                           String methodName,
                                                                           @Nullable Mono<R> defaultValue,
                                                                           FirstExtensionPointInvoker<T, R> invoker) {
        List<T> extensionPoints = pluginRepository.getExtensionPoints(extensionPointClass);
        int size = extensionPoints.size();
        if (size == 0) {
            return defaultValue == null ? Mono.empty() : defaultValue;
        }
        T extensionPoint = extensionPoints.get(0);
        TurmsExtension extension = (TurmsExtension) extensionPoint;
        if (!extension.isRunning()) {
            return defaultValue == null ? Mono.empty() : defaultValue;
        }
        try {
            return invoker.invoke(extensionPoint)
                    .onErrorMap(t -> translateException(t, methodName, extension));
        } catch (Exception e) {
            return Mono.error(translateException(e, methodName, extension));
        }
    }

    public <T extends ExtensionPoint, R> Mono<R> invokeExtensionPointsSequentially(Class<T> extensionPointClass,
                                                                                   String methodName,
                                                                                   SequentialExtensionPointInvoker<T, R> invoker) {
        return invokeExtensionPointsSequentially(extensionPointClass, methodName, null, invoker);
    }

    public <T extends ExtensionPoint, R> Mono<R> invokeExtensionPointsSequentially(Class<T> extensionPointClass,
                                                                                   String methodName,
                                                                                   @Nullable R initialValue,
                                                                                   SequentialExtensionPointInvoker<T, R> invoker) {
        List<T> extensionPoints = pluginRepository.getExtensionPoints(extensionPointClass);
        int size = extensionPoints.size();
        if (size == 0) {
            return Mono.empty();
        }
        Mono<R> result = initialValue == null ? Mono.empty() : Mono.just(initialValue);
        for (ExtensionPoint extensionPoint : extensionPoints) {
            TurmsExtension extension = (TurmsExtension) extensionPoint;
            if (!extension.isRunning()) {
                continue;
            }
            try {
                result = invoker.invoke((T) extensionPoint, result)
                        .onErrorMap(t -> translateException(t, methodName, extension));
            } catch (Exception e) {
                Mono<R> error = Mono.error(translateException(e, methodName, extension));
                return result.then(error);
            }
        }
        return result;
    }

    public <T extends ExtensionPoint> Mono<Void> invokeExtensionPoints(Class<T> extensionPointClass,
                                                                       String methodName,
                                                                       ExtensionPointInvoker<T> invoker) {
        List<T> extensionPoints = pluginRepository.getExtensionPoints(extensionPointClass);
        int size = extensionPoints.size();
        if (size == 0) {
            return Mono.empty();
        }
        List<Mono<?>> list = new ArrayList<>(size);
        for (ExtensionPoint extensionPoint : extensionPoints) {
            TurmsExtension extension = (TurmsExtension) extensionPoint;
            if (!extension.isRunning()) {
                continue;
            }
            try {
                Mono<Void> mono = invoker.invoke((T) extensionPoint)
                        .onErrorMap(t -> translateException(t, methodName, extension));
                list.add(mono);
            } catch (Exception e) {
                Mono<Void> error = Mono.error(translateException(e, methodName, extension));
                list.add(error);
            }
        }
        if (list.isEmpty()) {
            return Mono.empty();
        }
        return Mono.whenDelayError(list);
    }

    private Exception translateException(Throwable t, String methodName, TurmsExtension extension) {
        // TODO: add plugin ID
        String message = "Failed to invoke the method \"%s\" in the extension %s"
                .formatted(methodName, extension.getClass().getName());
        return new ExtensionPointExecutionException(message, t);
    }

}