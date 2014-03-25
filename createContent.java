import com.componize.alfresco.repo.node.NodePathResolver;
  
 +import org.alfresco.model.ContentModel;
  import org.alfresco.repo.content.MimetypeMap;
  import org.alfresco.repo.model.Repository;
  import org.alfresco.service.ServiceRegistry;
  import org.alfresco.service.cmr.model.FileFolderService;
 +import org.alfresco.service.cmr.repository.NodeRef;
  import org.alfresco.web.scripts.AbstractWebScript;
  import org.alfresco.web.scripts.WebScriptRequest;
  import org.alfresco.web.scripts.WebScriptResponse;
 @@ -38,9 +40,13 @@ public void execute(final WebScriptRequest request, final WebScriptResponse resp
  
          String nodeRefStr = WebScriptUtil.getNodeRef(request);
          String contentName = WebScriptUtil.getContentName(request);
 +        NodeRef nodeRef = new NodeRef(nodeRefStr);
  
          try {
              CreateContentBean createContBean = new CreateContentBean();
 +            if (contentName!= null && !contentName.isEmpty()) {
 +                fileService.create(nodeRef, contentName, ContentModel.TYPE_CONTENT);
 +            }
  
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