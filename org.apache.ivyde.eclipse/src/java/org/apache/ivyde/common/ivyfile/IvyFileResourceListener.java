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
package org.apache.ivyde.common.ivyfile;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.ivyde.eclipse.IvyPlugin;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class IvyFileResourceListener implements IResourceChangeListener {

    private static class IvyVisitor implements IResourceDeltaVisitor, IResourceVisitor {

        public boolean visit(IResource resource) throws CoreException {
            resourceChanged(resource);
            return true;
        }

        public boolean visit(IResourceDelta delta) {
            IResource resource = delta.getResource();

            if (IResourceDelta.CHANGED == delta.getKind()) {
                if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT) {
                    resourceChanged(resource);
                }
            }

            return true;
        }

        private static void resourceChanged(IResource resource) {
            if (!(resource instanceof IFile)
                    || !IvyPlugin.getPreferenceStoreHelper().getAutoResolveOnChange()) {
                return;
            }
            IFile resourceFile = (IFile) resource;
            IJavaProject javaProject = JavaCore.create(resource.getProject());
            if (javaProject == null) {
                return;
            }
            List/* <IvyClasspathContainer> */containers = IvyClasspathUtil
                    .getIvyClasspathContainers(javaProject);
            Iterator containerIter = containers.iterator();
            while (containerIter.hasNext()) {
                IvyClasspathContainer container = (IvyClasspathContainer) containerIter.next();
                File containerIvyFile = container.getState().getIvyFile();
                if (containerIvyFile.equals(resourceFile.getLocation().toFile())) {
                    container.launchResolve(false, false, null);
                    return;
                }
            }
        }
    }

    private static final IResourceDeltaVisitor visitor = new IvyVisitor();

    public void resourceChanged(IResourceChangeEvent event) {
        switch (event.getType()) {
            case IResourceChangeEvent.PRE_BUILD:
                try {
                    event.getDelta().accept(visitor);
                } catch (CoreException e) {
                    IvyPlugin.log(e);
                }
                break;
        }
    }
}
