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
package org.apache.ivyde.eclipse.ui.editors;

import java.util.Iterator;
import java.util.List;

import org.apache.ivyde.common.ivyfile.IvyModuleDescriptorModel;
import org.apache.ivyde.common.model.IvyModel;
import org.apache.ivyde.eclipse.IvyPlugin;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathUtil;
import org.apache.ivyde.eclipse.ui.core.IvyFileEditorInput;
import org.apache.ivyde.eclipse.ui.editors.pages.OverviewFormPage;
import org.apache.ivyde.eclipse.ui.editors.xml.EclipseIvyModelSettings;
import org.apache.ivyde.eclipse.ui.editors.xml.IvyContentAssistProcessor;
import org.apache.ivyde.eclipse.ui.editors.xml.XMLEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

public class IvyModuleDescriptorEditor extends FormEditor implements IResourceChangeListener {
    public static final String ID = "org.apache.ivyde.editors.IvyEditor";

    private XMLEditor xmlEditor;

    private Browser browser;

    /**
     * Creates a multi-page editor example.
     */
    public IvyModuleDescriptorEditor() {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    protected void setInput(IEditorInput input) {
        IvyFileEditorInput ivyFileEditorInput = null;
        if (input instanceof FileEditorInput) {
            FileEditorInput fei = (FileEditorInput) input;
            IFile file = fei.getFile();
            ivyFileEditorInput = new IvyFileEditorInput(file);
        } else if (input instanceof IvyFileEditorInput) {
            ivyFileEditorInput = (IvyFileEditorInput) input;
        }
        super.setInput(ivyFileEditorInput);
        if (ivyFileEditorInput.getFile() != null) {
            if (xmlEditor != null) {
                xmlEditor.setFile(ivyFileEditorInput.getFile());
            }
        }
        setPartName(ivyFileEditorInput.getFile().getName());
    }

    void createPageXML() {
        try {
            xmlEditor = new XMLEditor(new IvyContentAssistProcessor() {
                protected IvyModel newCompletionModel(IFile file) {
                    return new IvyModuleDescriptorModel(new EclipseIvyModelSettings(
                            file));
                }
            }) {
                public void doSave(IProgressMonitor progressMonitor) {
                    super.doSave(progressMonitor);
                    triggerResolve();
                }
            };
            xmlEditor.setFile(((IvyFileEditorInput) getEditorInput()).getFile());
            int index = addPage(xmlEditor, getEditorInput());
            setPageText(index, xmlEditor.getTitle());
        } catch (PartInitException e) {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null,
                e.getStatus());
        }
    }

    void createPageOverView() {
        try {
            int index = addPage(new OverviewFormPage(this));
            setPageText(index, "Information");
        } catch (PartInitException e) {
            // Should not happen
            IvyPlugin.log(IStatus.ERROR, "The overview page could not be created", e);
        }

    }

    void createPagePreview() {
        try {
            browser = new Browser(getContainer(), SWT.NONE);
            browser.setUrl(((IvyFileEditorInput) getEditorInput()).getPath().toOSString());
            int index = addPage(browser);
            setPageText(index, "Preview");
        } catch (SWTError e) {
            // IVYDE-10: under Linux if MOZILLA_FIVE_HOME is not set, it fails badly
            MessageDialog.openError(IvyPlugin.getActiveWorkbenchShell(),
                "Fail to create the preview", "The page preview could not be created :"
                        + e.getMessage());
            IvyPlugin.log(IStatus.ERROR,
                "The preview page in the ivy.xml editor could not be created", e);
        }
    }

    /**
     * Creates the pages of the multi-page editor.
     */
    protected void addPages() {
        // createPageOverView();
        createPageXML();
        // createPagePreview();
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code>
     * method disposes all nested editors. Subclasses may extend.
     */
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor) {
        xmlEditor.doSave(monitor);
    }

    private void triggerResolve() {
        IFile file = ((IvyFileEditorInput) getEditorInput()).getFile();
        List/* <IvyClasspathContainer> */containers = IvyClasspathUtil
                .getIvyFileClasspathContainers(file);
        Iterator/* <IvyClasspathContainer> */itContainers = containers.iterator();
        if (IvyPlugin.getPreferenceStoreHelper().getAutoResolveOnChange()) {
            while (itContainers.hasNext()) {
                IvyClasspathContainer ivycp = (IvyClasspathContainer) itContainers.next();
                ivycp.launchResolve(false, true, null);
            }
        }
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the text for page 0's
     * tab, and updates this multi-page editor's input to correspond to the nested editor's.
     */
    public void doSaveAs() {
        xmlEditor.doSaveAs();
        setPageText(0, xmlEditor.getTitle());
        setInput(xmlEditor.getEditorInput());
    }

    public void gotoMarker(IMarker marker) {
        setActivePage(0);
        IDE.gotoMarker(getEditor(0), marker);
    }

    /**
     * The <code>MultiPageEditorExample</code> implementation of this method checks that the input
     * is an instance of <code>IFileEditorInput</code>.
     */
    public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
        if (!(editorInput instanceof IFileEditorInput)) {
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");
        }
        super.init(site, editorInput);
    }

    public boolean isSaveAsAllowed() {
        return xmlEditor.isSaveAsAllowed();
    }

    /**
     * Calculates the contents of page 2 when the it is activated.
     */
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (newPageIndex == 1) {
            browser.refresh();
        }
    }

    /**
     * Closes all project files on project close.
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
            final IResource res = event.getResource();
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                    for (int i = 0; i < pages.length; i++) {
                        if (((IFileEditorInput) xmlEditor.getEditorInput()).getFile().getProject()
                                .equals(res)) {
                            IEditorPart editorPart = pages[i]
                                    .findEditor(xmlEditor.getEditorInput());
                            pages[i].closeEditor(editorPart, true);
                        }
                    }
                }
            });
        }
    }

}
