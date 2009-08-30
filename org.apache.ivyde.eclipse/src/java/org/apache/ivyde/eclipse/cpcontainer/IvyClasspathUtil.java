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
package org.apache.ivyde.eclipse.cpcontainer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.ivyde.eclipse.IvyPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.IStructuredSelection;

public final class IvyClasspathUtil {

    private IvyClasspathUtil() {
        // utility class
    }

    /**
     * Get the Ivy classpath container from the selection in the Java package view
     * 
     * @param selection
     *            the selection
     * @return
     * @throws JavaModelException
     */
    public static IvyClasspathContainer getIvyClasspathContainer(IStructuredSelection selection) {
        if (selection == null) {
            return null;
        }
        for (Iterator it = selection.iterator(); it.hasNext();) {
            Object element = it.next();
            IvyClasspathContainer cp = (IvyClasspathContainer) IvyPlugin.adapt(element,
                IvyClasspathContainer.class);
            if (cp != null) {
                return cp;
            }
            if (element instanceof ClassPathContainer) {
                // FIXME: we shouldn't check against internal JDT API but there are not adaptable to
                // useful class
                return jdt2IvyCPC((ClassPathContainer) element);
            }
        }
        return null;
    }

    /**
     * Work around the non adaptability of ClassPathContainer
     * 
     * @param cpc
     *            the container to transform into an IvyClasspathContainer
     * @return the IvyClasspathContainer is such, null, if not
     */
    public static IvyClasspathContainer jdt2IvyCPC(ClassPathContainer cpc) {
        IClasspathEntry entry = cpc.getClasspathEntry();
        try {
            IClasspathContainer icp = JavaCore.getClasspathContainer(entry.getPath(), cpc
                    .getJavaProject());
            if (icp instanceof IvyClasspathContainer) {
                return (IvyClasspathContainer) icp;
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            IvyPlugin.log(e);
        }
        return null;
    }

    public static boolean isIvyClasspathContainer(IPath containerPath) {
        return containerPath.segment(0).equals(IvyClasspathContainer.CONTAINER_ID);
    }

    /**
     * Search the Ivy classpath containers within the specified Java project
     * 
     * @param javaProject
     *            the project to search into
     * @return the Ivy classpath container if found
     */
    public static List/* <IvyClasspathContainer> */getIvyClasspathContainers(
            IJavaProject javaProject) {
        List/*<IvyClasspathContainer>*/ containers = new ArrayList();
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    IPath path = entry.getPath();
                    if (isIvyClasspathContainer(path)) {
                        IClasspathContainer cp = JavaCore.getClasspathContainer(path, javaProject);
                        if (cp instanceof IvyClasspathContainer) {
                            containers.add(cp);
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            IvyPlugin.log(e);
        }
        return containers;
    }

    public static List/*<IvyClasspathContainer>*/ getIvyFileClasspathContainers(IFile ivyfile) {
        IJavaProject javaProject = JavaCore.create(ivyfile.getProject());
        List/*<IvyClasspathContainer>*/ containers = new ArrayList();
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    IPath path = entry.getPath();
                    if (isIvyClasspathContainer(path)) {
                        IClasspathContainer cp = JavaCore.getClasspathContainer(path, javaProject);
                        if (cp instanceof IvyClasspathContainer) {
                            IvyClasspathContainer ivycp = (IvyClasspathContainer) cp;
                            if (ivycp.getConf().getIvyXmlPath().equals(ivyfile.getProjectRelativePath().toString())) {
                                containers.add(ivycp);
                            }
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            IvyPlugin.log(e);
        }
        return containers;
    }

    public static List/*<IvyClasspathContainer>*/ getIvySettingsClasspathContainers(IFile ivySettings) {
        IJavaProject javaProject = JavaCore.create(ivySettings.getProject());
        List/*<IvyClasspathContainer>*/ containers = new ArrayList();
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    IPath path = entry.getPath();
                    if (isIvyClasspathContainer(path)) {
                        IClasspathContainer cp = JavaCore.getClasspathContainer(path, javaProject);
                        if (cp instanceof IvyClasspathContainer) {
                            IvyClasspathContainer ivycp = (IvyClasspathContainer) cp;
                            if (ivycp.getConf().getInheritedIvySettingsPath().equals(
                                ivySettings.getProjectRelativePath().toString())) {
                                containers.add(ivycp);
                            }
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            IvyPlugin.log(e);
        }
        return containers;
    }

    public static List split(String str) {
        String[] terms = str.split(",");
        List ret = new ArrayList();
        for (int i = 0; i < terms.length; i++) {
            String t = terms[i].trim();
            if (t.length() > 0) {
                ret.add(t);
            }
        }
        return ret;
    }

    public static String concat(Collection/* <String> */list) {
        if (list == null) {
            return "";
        }
        StringBuffer b = new StringBuffer();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(",");
            }
        }
        return b.toString();
    }

    /**
     * Just a verbatim copy of the internal Eclipse function:
     * org.eclipse.jdt.internal.corext.javadoc.JavaDocLocations#getLibraryJavadocLocation(IClasspathEntry)
     * 
     * @param entry
     * @return
     */
    public static URL getLibraryJavadocLocation(IClasspathEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null"); //$NON-NLS-1$
        }

        int kind = entry.getEntryKind();
        if (kind != IClasspathEntry.CPE_LIBRARY && kind != IClasspathEntry.CPE_VARIABLE) {
            throw new IllegalArgumentException(
                    "Entry must be of kind CPE_LIBRARY or " + "CPE_VARIABLE"); //$NON-NLS-1$
        }

        IClasspathAttribute[] extraAttributes = entry.getExtraAttributes();
        for (int i = 0; i < extraAttributes.length; i++) {
            IClasspathAttribute attrib = extraAttributes[i];
            if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attrib.getName())) {
                try {
                    return new URL(attrib.getValue());
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }
        return null;
    }

}
