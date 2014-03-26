package org.ieee.sa.x1ng.webscripts.node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentWriter;
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
        FileInfo info;

        try {
            CreateContentBean createContBean = new CreateContentBean();

            try {
                if (contentName != null && !contentName.isEmpty()) {
                    info = fileService.create(nodeRef, contentName, ContentModel.TYPE_CONTENT);
                    ContentWriter writer = m_serviceRegistry.getFileFolderService().getWriter(info.getNodeRef());
                    writer.setLocale(Locale.ENGLISH);
                    File file = new File("C:/Users/Administrator/Desktop/8/test.xml");
                    writer.setMimetype("text/xml");
                    writer.putContent(file);
                    createContBean.setStatus(contentName + " was created");
                } else {
                    createContBean.setStatus("The content needs a name");
                }
            }catch (FileExistsException  exception) {
                String errorMsg = "Content already exists, unable to create. Try Other name.";
                createContBean.setStatus(errorMsg);
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