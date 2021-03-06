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
package org.apache.ivyde.eclipse.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ivyde.eclipse.IvyPlugin;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cpcontainer.IvydeContainerPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.Wizard;

public class NewIvyDEContainerWizard extends Wizard {

    private IvydeContainerPage containerPage;

    public NewIvyDEContainerWizard(IJavaProject project, IFile ivyfile) {
        containerPage = new IvydeContainerPage();
        containerPage.initialize(project, null);
        containerPage.setSelection(ivyfile);
    }

    public void addPages() {
        addPage(containerPage);
    }

    public boolean performFinish() {
        containerPage.finish();
        IClasspathEntry newEntry = containerPage.getSelection();
        IPath path = newEntry.getPath();
        IJavaProject project = containerPage.getProject();
        try {
            IvyClasspathContainer ivycp = new IvyClasspathContainer(project, path,
                    new IClasspathEntry[0], null);
            JavaCore.setClasspathContainer(path, new IJavaProject[] {project},
                new IClasspathContainer[] {ivycp}, null);
            IClasspathEntry[] entries = project.getRawClasspath();
            List newEntries = new ArrayList(Arrays.asList(entries));
            newEntries.add(newEntry);
            entries = (IClasspathEntry[]) newEntries
                    .toArray(new IClasspathEntry[newEntries.size()]);
            project.setRawClasspath(entries, project.getOutputLocation(), null);
            ivycp.launchResolve(false, true, null);
        } catch (JavaModelException e) {
            IvyPlugin.log(e);
            return false;
        }
        return true;
    }

}
