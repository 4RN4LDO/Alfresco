package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ieee.sa.x1ng.webscripts.bean.CreateContentBean;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import com.componize.alfresco.repo.node.NodePathResolver;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class CreateContent extends AbstractWebScript{
    private static Logger LOG = Logger.getLogger(NodePropertySvc.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;
    protected NodePathResolver m_nodePathResolver = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
        LOG.debug("Start executeImpl()");

        FileFolderService fileService = m_serviceRegistry.getFileFolderService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        String contentName = WebScriptUtil.getContentName(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {
            CreateContentBean createContBean = new CreateContentBean();
            if (contentName!= null && !contentName.isEmpty()) {
                fileService.create(nodeRef, contentName, ContentModel.TYPE_CONTENT);
            }

            response.getWriter().write(mapper.writeValueAsString(createContBean));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        }catch (Throwable e) {
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