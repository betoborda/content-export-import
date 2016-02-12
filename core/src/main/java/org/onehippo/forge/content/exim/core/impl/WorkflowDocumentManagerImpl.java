/*
 * Copyright 2016-2016 Hippo B.V. (http://www.onehippo.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onehippo.forge.content.exim.core.impl;

import java.rmi.RemoteException;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.Document;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.standardworkflow.DefaultWorkflow;
import org.hippoecm.repository.standardworkflow.FolderWorkflow;
import org.hippoecm.repository.translation.TranslationWorkflow;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.DocumentManagerException;
import org.onehippo.forge.content.pojo.binder.ContentNodeBinder;
import org.onehippo.forge.content.pojo.binder.ContentNodeBindingItemFilter;
import org.onehippo.forge.content.pojo.binder.jcr.DefaultContentNodeJcrBindingItemFilter;
import org.onehippo.forge.content.pojo.binder.jcr.DefaultJcrContentNodeBinder;
import org.onehippo.forge.content.pojo.model.ContentItem;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for {@link DocumentManager} using Hippo Workflow APIs.
 */
public class WorkflowDocumentManagerImpl implements DocumentManager {

    private Logger logger = LoggerFactory.getLogger(WorkflowDocumentManagerImpl.class);

    private ContentNodeBinder<Node, ContentItem, Value> contentNodeBinder;

    private ContentNodeBindingItemFilter<ContentItem> contentNodeBindingItemFilter;

    /**
     * The workflow category name to get a folder workflow. We use threepane as this is the same as the CMS uses
     */
    private String folderWorkflowCategory = "threepane";

    /**
     * The workflow category name to get a document workflow. 
     */
    private String documentWorkflowCategory = "default";

    private String defaultWorkflowCategory = "core";

    /**
     * The workflow category name to translate a folder.
     */
    private String folderTranslationWorkflowCategory = "translation";

    /**
     * The workflow category name to translate a document.
     */
    private String documentTranslationWorkflowCategory = "translation";

    private final Session session;

    /**
     * Constructs with {@code session}.
     * @param session JCR session to use
     */
    public WorkflowDocumentManagerImpl(final Session session) {
        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Returns {@link ContentNodeBinder} instance. If not set, returns a default implementation.
     * @return {@link ContentNodeBinder} instance. If not set, returns a default implementation
     */
    public ContentNodeBinder<Node, ContentItem, Value> getContentNodeBinder() {
        if (contentNodeBinder == null) {
            contentNodeBinder = new DefaultJcrContentNodeBinder();
        }

        return contentNodeBinder;
    }

    /**
     * Sets {@link ContentNodeBinder} instance.
     * @param contentNodeBinder {@link ContentNodeBinder} instance
     */
    public void setContentNodeBinder(ContentNodeBinder<Node, ContentItem, Value> contentNodeBinder) {
        this.contentNodeBinder = contentNodeBinder;
    }

    /**
     * Returns {@link ContentNodeBindingItemFilter} instance. If not set, returns a default implementation.
     * @return {@link ContentNodeBindingItemFilter} instance. If not set, returns a default implementation
     */
    public ContentNodeBindingItemFilter<ContentItem> getContentNodeBindingItemFilter() {
        if (contentNodeBindingItemFilter == null) {
            DefaultContentNodeJcrBindingItemFilter bindingItemFilter = new DefaultContentNodeJcrBindingItemFilter();
            bindingItemFilter.addPropertyPathExclude("hippo:*");
            bindingItemFilter.addPropertyPathExclude("hippostd:*");
            bindingItemFilter.addPropertyPathExclude("hippostdpubwf:*");
            contentNodeBindingItemFilter = bindingItemFilter;
        }

        return contentNodeBindingItemFilter;
    }

    /**
     * Sets {@link ContentNodeBindingItemFilter} instance.
     * @param contentNodeBindingItemFilter {@link ContentNodeBindingItemFilter} instance
     */
    public void setContentNodeBindingItemFilter(
            ContentNodeBindingItemFilter<ContentItem> contentNodeBindingItemFilter) {
        this.contentNodeBindingItemFilter = contentNodeBindingItemFilter;
    }

    /**
     * Returns the document workflow category.
     * @return the document workflow category
     */
    public String getDocumentWorkflowCategory() {
        return documentWorkflowCategory;
    }

    /**
     * Sets the document workflow category
     * @param documentWorkflowCategory the document workflow category
     */
    public void setDocumentWorkflowCategory(String documentWorkflowCategory) {
        this.documentWorkflowCategory = documentWorkflowCategory;
    }

    /**
     * Returns the default workflow category.
     * @return the default workflow category
     */
    public String getDefaultWorkflowCategory() {
        return defaultWorkflowCategory;
    }

    /**
     * Sets the default workflow category.
     * @param defaultWorkflowCategory the default workflow category
     */
    public void setDefaultWorkflowCategory(String defaultWorkflowCategory) {
        this.defaultWorkflowCategory = defaultWorkflowCategory;
    }

    /**
     * Returns the folder workflow category.
     * @return the folder workflow category
     */
    public String getFolderWorkflowCategory() {
        return folderWorkflowCategory;
    }

    /**
     * Sets the folder workflow category.
     * @param folderWorkflowCategory the folder workflow category
     */
    public void setFolderWorkflowCategory(String folderWorkflowCategory) {
        this.folderWorkflowCategory = folderWorkflowCategory;
    }

    /**
     * Returns the folder translation workflow category.
     * @return the folder translation workflow category
     */
    public String getFolderTranslationWorkflowCategory() {
        return folderTranslationWorkflowCategory;
    }

    /**
     * Sets the folder translation workflow category.
     * @param folderTranslationWorkflowCategory the folder translation workflow category
     */
    public void setFolderTranslationWorkflowCategory(String folderTranslationWorkflowCategory) {
        this.folderTranslationWorkflowCategory = folderTranslationWorkflowCategory;
    }

    /**
     * Returns the document translation workflow category.
     * @return the document translation workflow category
     */
    public String getDocumentTranslationWorkflowCategory() {
        return documentTranslationWorkflowCategory;
    }

    /**
     * Sets the document translation workflow category.
     * @param documentTranslationWorkflowCategory the document translation workflow category
     */
    public void setDocumentTranslationWorkflowCategory(String documentTranslationWorkflowCategory) {
        this.documentTranslationWorkflowCategory = documentTranslationWorkflowCategory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createDocument(String folderLocation, String primaryTypeName, String nodeName, String locale, String localizedName)
            throws DocumentManagerException {
        getLogger().debug("##### createDocument under '{}')", folderLocation);

        String createdDocPath = null;

        try {
            final Node folderNode = HippoNodeUtils.createMissingHippoFolders(getSession(), folderLocation);

            if (folderNode == null) {
                throw new IllegalArgumentException("Folder is not available at '" + folderLocation + "'.");
            }

            final FolderWorkflow folderWorkflow = getFolderWorkflow(folderNode);

            Boolean add = (Boolean) folderWorkflow.hints().get("add");

            if (BooleanUtils.isTrue(add)) {
                createdDocPath = folderWorkflow.add("new-document", primaryTypeName, nodeName);
                final DefaultWorkflow defaultWorkflow = getDefaultWorkflow(getSession().getNode(createdDocPath));
                defaultWorkflow.localizeName(localizedName);
            } else {
                throw new IllegalStateException("Folder at '" + folderLocation + "' is not allowed to add a document.");
            }
        } catch (Exception e) {
            getLogger().error("Failed to add a document with '{}' under '{}'.", nodeName, folderLocation, e);
            throw new DocumentManagerException(
                    "Failed to add a document with '" + nodeName + "' under '" + folderLocation + "'." + "'. " + e);
        }

        return createdDocPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document obtainEditableDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### obtainEditableDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        Document document = null;

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean obtainEditableInstance = (Boolean) documentWorkflow.hints().get("obtainEditableInstance");

            if (BooleanUtils.isTrue(obtainEditableInstance)) {
                document = documentWorkflow.obtainEditableInstance();
            } else {
                throw new IllegalStateException(
                        "Document at '" + documentLocation + "' is not allowed to obtain an editable instance.");
            }
        } catch (Exception e) {
            getLogger().error("Failed to obtain editable instance on document.", e);
            throw new DocumentManagerException(
                    "Failed to obtain editable instance on document at '" + documentLocation + "'. " + e);
        }

        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEditableDocument(Document editableDocument, ContentNode sourceContentNode)
            throws DocumentManagerException {
        try {
            final Node editableDocumentNode = editableDocument.getNode(getSession());
            getContentNodeBinder().bind(editableDocumentNode, sourceContentNode, getContentNodeBindingItemFilter());
        } catch (Exception e) {
            getLogger().error("Failed to update editable document.", e);
            throw new DocumentManagerException(
                    "Failed to obtain editable instance on document (id: '" + editableDocument + "'). " + e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document disposeEditableDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### disposeEditableDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        Document document = null;

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean disposeEditableInstance = (Boolean) documentWorkflow.hints().get("disposeEditableInstance");

            if (BooleanUtils.isTrue(disposeEditableInstance)) {
                document = documentWorkflow.disposeEditableInstance();
            } else {
                throw new IllegalStateException(
                        "Document at '" + documentLocation + "' is not allowed to dispose an editable instance.");
            }
        } catch (Exception e) {
            getLogger().error("Failed to dispose editable instance on document.", e);
            throw new DocumentManagerException(
                    "Failed to dispose editable instance on document at '" + documentLocation + "'. " + e);
        }

        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document commitEditableDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### commitEditableDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        Document document = null;

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean commitEditableInstance = (Boolean) documentWorkflow.hints().get("commitEditableInstance");

            if (BooleanUtils.isTrue(commitEditableInstance)) {
                document = documentWorkflow.commitEditableInstance();
            } else {
                throw new IllegalStateException(
                        "Document at '" + documentLocation + "' is not allowed to commit an editable instance.");
            }
        } catch (Exception e) {
            getLogger().error("Failed to commit editable instance on document.", e);
            throw new DocumentManagerException(
                    "Failed to commit editable instance on document at '" + documentLocation + "'. " + e);
        }

        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publishDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### publishDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        boolean published = false;

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean publish = (Boolean) documentWorkflow.hints().get("publish");

            if (!BooleanUtils.isTrue(publish)) {
                throw new IllegalStateException("Document at '" + documentLocation + "' doesn't have publish action.");
            }

            documentWorkflow.publish();
            published = true;
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to publish document at '{}'.", documentLocation, e);
            throw new DocumentManagerException("Failed to publish document at '" + documentLocation + "'. " + e);
        }

        return published;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean depublishDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### depublishDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        boolean depublished = false;

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean isLive = (Boolean) documentWorkflow.hints().get("isLive");

            if (BooleanUtils.isFalse(isLive)) {
                // already offline, so just return true
                depublished = true;
            } else {
                Boolean depublish = (Boolean) documentWorkflow.hints().get("depublish");

                if (!BooleanUtils.isTrue(depublish)) {
                    throw new IllegalStateException(
                            "Document at '" + documentLocation + "' doesn't have depublish action.");
                }

                documentWorkflow.depublish();
                depublished = true;
            }
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to depublish document at '{}'.", documentLocation, e);
            throw new DocumentManagerException("Failed to depublish document at '" + documentLocation + "'. " + e);
        }

        return depublished;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDocument(String documentLocation) throws DocumentManagerException {
        getLogger().debug("##### deleteDocument('{}')", documentLocation);

        if (StringUtils.isBlank(documentLocation)) {
            throw new IllegalArgumentException("Invalid document location: '" + documentLocation + "'.");
        }

        try {
            if (!getSession().nodeExists(documentLocation)) {
                throw new IllegalArgumentException("Document doesn't exist at '" + documentLocation + "'.");
            }

            Node documentHandleNode = HippoNodeUtils.getHippoDocumentHandle(getSession().getNode(documentLocation));

            if (documentHandleNode == null) {
                throw new IllegalArgumentException("Document handle is not found at '" + documentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(documentHandleNode);

            Boolean delete = (Boolean) documentWorkflow.hints().get("delete");

            if (BooleanUtils.isTrue(delete)) {
                documentWorkflow.delete();
            } else {
                throw new IllegalStateException("Document at '" + documentLocation + "' is not allowed to delete.");
            }
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to depublish document at '{}'.", documentLocation, e);
            throw new DocumentManagerException("Failed to depublish document at '" + documentLocation + "'. " + e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String copyDocument(String sourceDocumentLocation, String targetFolderLocation,
            String targetDocumentNodeName) throws DocumentManagerException {
        getLogger().debug("##### copyDocument('{}', '{}', '{}')", sourceDocumentLocation, targetFolderLocation,
                targetDocumentNodeName);

        String targetDocumentLocation = null;

        try {
            if (!getSession().nodeExists(sourceDocumentLocation)) {
                throw new IllegalArgumentException(
                        "Source document doesn't exist at '" + sourceDocumentLocation + "'.");
            }

            final Node targetFolderNode = HippoNodeUtils.createMissingHippoFolders(getSession(), targetFolderLocation);

            if (targetFolderNode == null) {
                throw new IllegalArgumentException("Target folder doesn't exist at '" + targetFolderLocation + "'.");
            }

            Node sourceDocumentHandleNode = HippoNodeUtils
                    .getHippoDocumentHandle(getSession().getNode(sourceDocumentLocation));

            if (sourceDocumentHandleNode == null) {
                throw new IllegalArgumentException(
                        "Source document handle is not found at '" + sourceDocumentLocation + "'.");
            }

            DocumentWorkflow documentWorkflow = getDocumentWorkflow(sourceDocumentHandleNode);
            Boolean copy = (Boolean) documentWorkflow.hints().get("copy");

            if (BooleanUtils.isTrue(copy)) {
                documentWorkflow.copy(new Document(targetFolderNode), targetDocumentNodeName);
                targetDocumentLocation = targetFolderNode.getNode(targetDocumentNodeName).getPath();
            } else {
                throw new IllegalStateException("Copy action not available on document at '" + sourceDocumentLocation
                        + "' to '" + targetFolderLocation + "/" + targetDocumentNodeName + "'.");
            }
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to copy document at '{}' to '{}/{}'.", sourceDocumentLocation, targetFolderLocation,
                    targetDocumentNodeName, e);
            throw new DocumentManagerException("Failed to copy document at '" + sourceDocumentLocation + "' to '"
                    + targetFolderLocation + "/" + targetDocumentNodeName + "'. " + e);
        }

        return targetDocumentLocation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document translateFolder(String sourceFolderLocation, String targetLanguage, String targetFolderNodeName)
            throws DocumentManagerException {
        getLogger().debug("##### translateFolder('{}', '{}', '{}')", sourceFolderLocation, targetLanguage,
                targetFolderNodeName);

        Document translatedFolderDocument = null;

        try {
            if (!getSession().nodeExists(sourceFolderLocation)) {
                throw new IllegalArgumentException("Source folder doesn't exist at '" + sourceFolderLocation + "'.");
            }

            Node sourceFolderNode = getSession().getNode(sourceFolderLocation);

            if (sourceFolderNode == null || !sourceFolderNode.isNodeType(HippoStdNodeType.NT_FOLDER)) {
                throw new IllegalArgumentException("Source folder is not found at '" + sourceFolderLocation + "'.");
            }

            TranslationWorkflow folderTranslationWorkflow = getFolderTranslationWorkflow(sourceFolderNode);
            translatedFolderDocument = folderTranslationWorkflow.addTranslation(targetLanguage, targetFolderNodeName);
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to translate folder at '{}' to '{}' in '{}'.", sourceFolderLocation, targetFolderNodeName,
                    targetLanguage, e);
            throw new DocumentManagerException("Failed to add translated folder of '" + sourceFolderLocation + "' to '"
                    + targetFolderNodeName + "' in '" + targetLanguage + "'. " + e);
        }

        return translatedFolderDocument;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Document translateDocument(String sourceDocumentLocation, String targetLanguage,
            String targetDocumentNodeName) throws DocumentManagerException {
        getLogger().debug("##### translateDocument('{}', '{}', '{}')", sourceDocumentLocation, targetLanguage,
                targetDocumentNodeName);

        Document translatedDocument = null;

        try {
            if (!getSession().nodeExists(sourceDocumentLocation)) {
                throw new IllegalArgumentException(
                        "Source document doesn't exist at '" + sourceDocumentLocation + "'.");
            }

            Node sourceDocumentHandleNode = HippoNodeUtils
                    .getHippoDocumentHandle(getSession().getNode(sourceDocumentLocation));

            if (sourceDocumentHandleNode == null) {
                throw new IllegalArgumentException(
                        "Source document handle is not found at '" + sourceDocumentLocation + "'.");
            }

            Node translationVariantNode = null;
            Map<String, Node> documentVariantsMap = HippoNodeUtils.getDocumentVariantsMap(sourceDocumentHandleNode);

            if (documentVariantsMap.containsKey(HippoStdNodeType.UNPUBLISHED)) {
                translationVariantNode = documentVariantsMap.get(HippoStdNodeType.UNPUBLISHED);
            } else if (documentVariantsMap.containsKey(HippoStdNodeType.PUBLISHED)) {
                translationVariantNode = documentVariantsMap.get(HippoStdNodeType.PUBLISHED);
            }

            if (translationVariantNode == null) {
                throw new IllegalStateException("No available unpublished or published variant in document at '"
                        + sourceDocumentLocation + "'.");
            }

            TranslationWorkflow documentTranslationWorkflow = getDocumentVariantTranslationWorkflow(translationVariantNode);
            translatedDocument = documentTranslationWorkflow.addTranslation(targetLanguage, targetDocumentNodeName);
        } catch (RepositoryException | WorkflowException | RemoteException e) {
            getLogger().error("Failed to translate document at '{}' to '{}' in '{}'.", sourceDocumentLocation,
                    targetDocumentNodeName, targetLanguage, e);
            throw new DocumentManagerException("Failed to add translated document of '" + sourceDocumentLocation
                    + "' to '" + targetDocumentNodeName + "' in '" + targetLanguage + "'. " + e);
        }

        return translatedDocument;
    }

    /**
     * Returns a folder workflow instance on {@code folderNode}.
     * @param folderNode folder node
     * @return a folder workflow instance on {@code folderNode}
     * @throws RepositoryException if unexpected repository exception occurs
     */
    protected FolderWorkflow getFolderWorkflow(final Node folderNode) throws RepositoryException {
        return (FolderWorkflow) HippoNodeUtils.getHippoWorkflow(getSession(), getFolderWorkflowCategory(), folderNode);
    }

    /**
     * Returns a document workflow on {@code documentHandleNode}.
     * @param documentHandleNode document handle node
     * @return a document workflow on {@code documentHandleNode}
     * @throws RepositoryException if unexpected repository exception occurs
     */
    protected DocumentWorkflow getDocumentWorkflow(final Node documentHandleNode) throws RepositoryException {
        return (DocumentWorkflow) HippoNodeUtils.getHippoWorkflow(getSession(), getDocumentWorkflowCategory(),
                documentHandleNode);
    }

    /**
     * Returns a {@link DefaultWorkflow} instance on {@code documentHandleNode}.
     * @param documentHandleNode document handle node
     * @return {@link DefaultWorkflow} instance on {@code documentHandleNode}
     * @throws RepositoryException if unexpected repository exception occurs
     */
    protected DefaultWorkflow getDefaultWorkflow(final Node documentHandleNode) throws RepositoryException {
        return (DefaultWorkflow) HippoNodeUtils.getHippoWorkflow(getSession(), getDefaultWorkflowCategory(),
                documentHandleNode);
    }

    /**
     * Returns a folder {@link TranslationWorkflow} instance on {@code folderNode}.
     * @param folderNode folder node
     * @return a folder {@link TranslationWorkflow} instance on {@code folderNode}
     * @throws RepositoryException if unexpected repository exception occurs
     */
    protected TranslationWorkflow getFolderTranslationWorkflow(final Node folderNode) throws RepositoryException {
        return (TranslationWorkflow) HippoNodeUtils.getHippoWorkflow(getSession(),
                getFolderTranslationWorkflowCategory(), folderNode);
    }

    /**
     * Returns a document {@link TranslationWorkflow} instance on {@code documentVariantNode}.
     * @param documentVariantNode document variant node
     * @return a document {@link TranslationWorkflow} instance on {@code documentVariantNode}
     * @throws RepositoryException if unexpected repository exception occurs
     */
    protected TranslationWorkflow getDocumentVariantTranslationWorkflow(final Node documentVariantNode)
            throws RepositoryException {
        return (TranslationWorkflow) HippoNodeUtils.getHippoWorkflow(getSession(),
                getDocumentTranslationWorkflowCategory(), documentVariantNode);
    }
}