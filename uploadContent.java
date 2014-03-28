package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
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

            try {

               
            } catch (Throwable e) {
                String errorMsg = "Unable to save content to node " + nodeRefStr.toString();
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