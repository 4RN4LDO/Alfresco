package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ieee.sa.x1ng.webscripts.bean.CheckPropertiesBean;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class CheckProperties extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(CheckProperties.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        NodeService nodeService = m_serviceRegistry.getNodeService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {
            

            response.getWriter().write(mapper.writeValueAsString(nodeRef));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        }catch (Throwable e) {
            String errorMsg = "Unable to check node  properties";
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
