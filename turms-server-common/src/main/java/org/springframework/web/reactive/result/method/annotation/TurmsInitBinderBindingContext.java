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

package org.springframework.web.reactive.result.method.annotation;

import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.reactive.result.method.SyncInvocableHandlerMethod;

import java.util.List;

/**
 * @author James Chen
 */
public class TurmsInitBinderBindingContext extends InitBinderBindingContext {
    public TurmsInitBinderBindingContext(WebBindingInitializer initializer,
                                         List<SyncInvocableHandlerMethod> binderMethods) {
        super(initializer, binderMethods);
    }
}
