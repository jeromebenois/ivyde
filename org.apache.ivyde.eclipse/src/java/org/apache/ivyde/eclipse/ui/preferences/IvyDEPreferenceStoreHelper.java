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
package org.apache.ivyde.eclipse.ui.preferences;

import org.apache.ivyde.eclipse.cpcontainer.ContainerMappingSetup;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathUtil;
import org.apache.ivyde.eclipse.cpcontainer.IvySettingsSetup;
import org.apache.ivyde.eclipse.cpcontainer.RetrieveSetup;
import org.eclipse.jface.preference.IPreferenceStore;

public class IvyDEPreferenceStoreHelper {

    private final IPreferenceStore prefStore;

    public IvyDEPreferenceStoreHelper(IPreferenceStore prefStore) {
        this.prefStore = prefStore;
    }

    public String getIvyOrg() {
        return prefStore.getString(PreferenceConstants.ORGANISATION);
    }

    public void setIvyOrg(String org) {
        prefStore.setValue(PreferenceConstants.ORGANISATION, org);
    }

    public String getIvyOrgUrl() {
        return prefStore.getString(PreferenceConstants.ORGANISATION_URL);
    }

    public void setIvyOrgUrl(String url) {
        prefStore.setValue(PreferenceConstants.ORGANISATION_URL, url);
    }

    public IvySettingsSetup getIvySettingsSetup() {
        IvySettingsSetup setup = new IvySettingsSetup();
        setup.setIvySettingsPath(prefStore.getString(PreferenceConstants.IVYSETTINGS_PATH));
        setup.setLoadSettingsOnDemand(prefStore
                .getBoolean(PreferenceConstants.LOAD_SETTINGS_ON_DEMAND));
        setup.setPropertyFiles(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.PROPERTY_FILES)));
        return setup;
    }

    public void setIvySettingsSetup(IvySettingsSetup setup) {
        prefStore.setValue(PreferenceConstants.IVYSETTINGS_PATH, setup.getIvySettingsPath());
        prefStore.setValue(PreferenceConstants.PROPERTY_FILES, IvyClasspathUtil.concat(setup
                .getPropertyFiles()));
        prefStore.setValue(PreferenceConstants.LOAD_SETTINGS_ON_DEMAND, setup
                .isLoadSettingsOnDemand());
    }

    public ContainerMappingSetup getContainerMappingSetup() {
        ContainerMappingSetup setup = new ContainerMappingSetup();
        setup.setAcceptedTypes(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.ACCEPTED_TYPES)));
        setup.setSourceTypes(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.SOURCES_TYPES)));
        setup.setJavadocTypes(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.JAVADOC_TYPES)));
        setup.setSourceSuffixes(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.SOURCES_SUFFIXES)));
        setup.setJavadocSuffixes(IvyClasspathUtil.split(prefStore
                .getString(PreferenceConstants.JAVADOC_SUFFIXES)));
        return setup;
    }

    public void setContainerMappingSetup(ContainerMappingSetup setup) {
        prefStore.setValue(PreferenceConstants.ACCEPTED_TYPES, IvyClasspathUtil.concat(setup
                .getAcceptedTypes()));
        prefStore.setValue(PreferenceConstants.SOURCES_TYPES, IvyClasspathUtil.concat(setup
                .getSourceTypes()));
        prefStore.setValue(PreferenceConstants.JAVADOC_TYPES, IvyClasspathUtil.concat(setup
                .getJavadocTypes()));
        prefStore.setValue(PreferenceConstants.SOURCES_SUFFIXES, IvyClasspathUtil.concat(setup
                .getSourceSuffixes()));
        prefStore.setValue(PreferenceConstants.JAVADOC_SUFFIXES, IvyClasspathUtil.concat(setup
                .getJavadocSuffixes()));
    }

    public RetrieveSetup getRetrieveSetup() {
        RetrieveSetup setup = new RetrieveSetup();
        setup.setDoRetrieve(prefStore.getBoolean(PreferenceConstants.DO_RETRIEVE));
        setup.setRetrieveConfs(prefStore.getString(PreferenceConstants.RETRIEVE_CONFS));
        setup.setRetrievePattern(prefStore.getString(PreferenceConstants.RETRIEVE_PATTERN));
        setup.setRetrieveSync(prefStore.getBoolean(PreferenceConstants.RETRIEVE_SYNC));
        setup.setRetrieveTypes(prefStore.getString(PreferenceConstants.RETRIEVE_TYPES));
        return setup;
    }

    public void setRetrieveSetup(RetrieveSetup setup) {
        prefStore.setValue(PreferenceConstants.DO_RETRIEVE, setup.isDoRetrieve());
        prefStore.setValue(PreferenceConstants.RETRIEVE_PATTERN, setup.getRetrievePattern());
        prefStore.setValue(PreferenceConstants.RETRIEVE_SYNC, setup.isRetrieveSync());
        prefStore.setValue(PreferenceConstants.RETRIEVE_CONFS, setup.getRetrieveConfs());
        prefStore.setValue(PreferenceConstants.RETRIEVE_TYPES, setup.getRetrieveTypes());
    }

    public boolean isAlphOrder() {
        return prefStore.getBoolean(PreferenceConstants.ALPHABETICAL_ORDER);
    }

    public void setAlphOrder(boolean alpha) {
        prefStore.setValue(PreferenceConstants.ALPHABETICAL_ORDER, alpha);
    }

    public boolean isResolveInWorkspace() {
        return prefStore.getBoolean(PreferenceConstants.RESOLVE_IN_WORKSPACE);
    }

    public void setResolveInWorkspace(boolean inWorkspace) {
        prefStore.setValue(PreferenceConstants.RESOLVE_IN_WORKSPACE, inWorkspace);
    }

    public String getOrganization() {
        return prefStore.getString(PreferenceConstants.ORGANISATION);
    }

    public void setOrganization(String org) {
        prefStore.setValue(PreferenceConstants.ORGANISATION, org);
    }

    public String getOrganizationUrl() {
        return prefStore.getString(PreferenceConstants.ORGANISATION_URL);
    }

    public void setOrganizationUrl(String url) {
        prefStore.setValue(PreferenceConstants.ORGANISATION_URL, url);
    }

    public int getResolveOnStartup() {
        return prefStore.getInt(PreferenceConstants.RESOLVE_ON_STARTUP);
    }

    public void setResolveOnStartup(int resolveOnStartup) {
        prefStore.setValue(PreferenceConstants.RESOLVE_ON_STARTUP, resolveOnStartup);
    }

    public boolean getAutoResolveOnClose() {
        return prefStore.getBoolean(PreferenceConstants.AUTO_RESOLVE_ON_CLOSE);
    }

    public void setAutoResolveOnClose(boolean autoResolveOnOpen) {
        prefStore.setValue(PreferenceConstants.AUTO_RESOLVE_ON_CLOSE, autoResolveOnOpen);
    }

    public boolean getAutoResolveOnOpen() {
        return prefStore.getBoolean(PreferenceConstants.AUTO_RESOLVE_ON_OPEN);
    }

    public void setAutoResolveOnOpen(boolean autoResolveOnOpen) {
        prefStore.setValue(PreferenceConstants.AUTO_RESOLVE_ON_OPEN, autoResolveOnOpen);
    }

    public boolean getAutoResolveOnChange() {
        return prefStore.getBoolean(PreferenceConstants.AUTO_RESOLVE_ON_CHANGE);
    }

    public void setAutoResolveOnChange(boolean autoResolveChange) {
        prefStore.setValue(PreferenceConstants.AUTO_RESOLVE_ON_CHANGE, autoResolveChange);
    }
}
