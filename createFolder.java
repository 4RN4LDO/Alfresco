package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.extensions.webscripts.WebScriptException;

import com.componize.alfresco.repo.node.NodePathResolver;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class CreateFolder extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(NodePropertySvc.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;
    protected NodePathResolver m_nodePathResolver = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        ObjectMapper mapper = new ObjectMapper();

        try {




            response.getWriter().write(mapper.writeValueAsString(permBean));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }


    public void setRepository(final Repository repository) {

        m_repository = repository;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {

        m_serviceRegistry = registry;
    }

    public final void setNodePathResolver(final NodePathResolver nodePathResolver) {

        m_nodePathResolver = nodePathResolver;
    }
}
