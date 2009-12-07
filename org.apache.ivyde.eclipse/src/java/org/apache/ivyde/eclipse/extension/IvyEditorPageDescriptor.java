/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ivyde.eclipse.extension;

import org.apache.ivyde.eclipse.IvyPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * A factory proxy for creating a IFormPage contributed to Ivy module descriptor editor.
 * 
 * @author <a href="mailto:jerome@benois.fr">Jerome Benois</a>
 */
public class IvyEditorPageDescriptor {
    private static final String ID_ATTRIBUTE = "id";

    private static final String CLASS_ATTRIBUTE = "pageClass";

    private IConfigurationElement element;

    public IvyEditorPageDescriptor(final IConfigurationElement element) {
        this.element = element;
    }

    public IvyEditorPage createPage() {
        try {
            return (IvyEditorPage) element.createExecutableExtension(CLASS_ATTRIBUTE);
        } catch (final CoreException e) {
            IvyPlugin.log(IStatus.ERROR, "Impossible to create the page "
                    + element.getAttribute(CLASS_ATTRIBUTE), e);
        } catch (final ClassCastException e) {
            IvyPlugin.log(IStatus.ERROR, "Impossible to create the page "
                    + element.getAttribute(CLASS_ATTRIBUTE), e);
        }
        return null;
    }

    public String getId() {
        return element.getAttribute(ID_ATTRIBUTE);
    }

}
