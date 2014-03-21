import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.xxxxxxxx.webscripts.auth.SetAuthority;
import org.xxxxxxxx.webscripts.bean.SearchGroupUsersBean;
import org.xxxxxxxx.webscripts.util.WebScriptUtil;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class SearchGroupUsers extends AbstractWebScript {
    private final static Logger LOG = Logger.getLogger(SetAuthority.class);

    protected Repository m_repository = null;

    protected ServiceRegistry m_serviceRegistry = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        AuthorityService authService = m_serviceRegistry.getAuthorityService();
        PersonService personService = m_serviceRegistry.getPersonService();

        ObjectMapper mapper = new ObjectMapper();

        String groupName = WebScriptUtil.getGroupName(request);
        List<QName> list = new ArrayList<QName>();
        List<Pair<QName, Boolean>> order = new ArrayList<Pair<QName, Boolean>>();
        PagingRequest pagingRequest = new PagingRequest(10);

        try {
            SearchGroupUsersBean groupUsers = groupUser(authService, personService, groupName, list, order, pagingRequest);

            response.getWriter().write(mapper.writeValueAsString(groupUsers));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }

    /**
     * @param authService
     * @param personService
     * @param groupName
     * @param list
     * @param order
     * @param pagingRequest
     * @return
     */
    public SearchGroupUsersBean groupUser(AuthorityService authService, PersonService personService, String groupName,
            List<QName> list, List<Pair<QName, Boolean>> order, PagingRequest pagingRequest) {

        SearchGroupUsersBean groupUsers = new SearchGroupUsersBean();

        if (groupName.equalsIgnoreCase("everyone")) {
            PagingResults<PersonInfo> results = personService.getPeople("", list, order, pagingRequest);
            List<PersonInfo> personList = results.getPage();
            Set<String> authoritiesList = new HashSet<String>();
            for (PersonInfo person : personList) {
                authoritiesList.add(person.getUserName());
            }
            groupUsers.setUserName(authoritiesList);
        } else {
            Set<String> authoritiesList =
                    authService.getContainedAuthorities(AuthorityType.USER, "GROUP_" + groupName, false);
            groupUsers.setUserName(authoritiesList);
        }
        return groupUsers;
    }

    public void setRepository(final Repository repository) {

        m_repository = repository;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {

        m_serviceRegistry = registry;
    }
}
