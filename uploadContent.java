package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.WebScriptException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;


public class UploadContent extends AbstractWebScript {


        private static Logger LOG = Logger.getLogger(UploadContent.class);
        protected ContentService m_contentService = null;
        protected NodeRef m_nodeRef = null;
        protected Repository m_repository;
        protected ServiceRegistry m_serviceRegistry = null;

        @Override
        public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

            LOG.debug("Start executeImpl()");
            FileFolderService fileService = m_serviceRegistry.getFileFolderService();
            ContentService contService = m_serviceRegistry.getContentService();
            NodeService nodeService = m_serviceRegistry.getNodeService();
           FileFolderService fileFolderService = m_serviceRegistry.getFileFolderService();

            String nodeRefStr = request.getParameter("NodeRef");
            String uploadName = request.getParameter("Name");
            NodeRef nodeRef = new NodeRef(nodeRefStr);

            try {
                FileInfo info;
                if (uploadName != null && !uploadName.isEmpty()) {
                    info = fileService.create(nodeRef, uploadName, ContentModel.TYPE_CONTENT);
                    Map<QName,Serializable> props = new HashMap<QName,Serializable>();
                    props.put(ContentModel.PROP_NAME, uploadName);
                    props.put(ContentModel.PROP_TITLE, request.getParameter("Title"));
                    props.put(ContentModel.PROP_DESCRIPTION, request.getParameter("Description"));
                    props.put(ContentModel.PROP_AUTHOR, request.getParameter("Author"));
                    nodeService.setProperties(info.getNodeRef(), props);
                    ContentWriter writer = m_serviceRegistry.getFileFolderService().getWriter(info.getNodeRef());
                    writer.setLocale(Locale.ENGLISH);

                }
            } catch (Throwable e) {
                String errorMsg = "Unable to save content to node ";
                throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
            }

        }

        public void setRepository(final Repository repository) {

            m_repository = repository;
        }

        public void setServiceRegistry(final ServiceRegistry registry) {

            m_serviceRegistry = registry;
        }

    }