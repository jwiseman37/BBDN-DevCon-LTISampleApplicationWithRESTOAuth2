package com.mycompany.controller;

import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.plugin.PlugInUtil;
import blackboard.platform.servlet.InlineReceiptUtil;
import blackboard.platform.context.ContextManagerFactory;
import com.mycompany.Constants;
import com.mycompany.data.CourseDetails;
import com.mycompany.data.Token;
import com.mycompany.data.UserDetails;
import com.mycompany.exception.GenericLtiException;
import com.mycompany.model.LaunchModel;
import com.mycompany.model.SettingsModel;
import com.mycompany.model.CourseDetailsModel;
import com.mycompany.service.DataFetcherService;
import com.mycompany.service.SettingsService;
import net.oauth.*;
import net.oauth.server.HttpRequestMessage;
import net.oauth.server.OAuthServlet;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

@Controller
public class LaunchController {

    @Autowired
    SettingsService settingsService;

    @Autowired
    DataFetcherService dataFetcherService;

    /**
     * @param request
     * @return
     */
    @RequestMapping(value = {"/launch"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {

        String parmsForDisplay = "";
        Map parameters = request.getParameterMap();
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            parmsForDisplay += "<b>" + key + "</b>: ";
            String[] values = (String[]) (parameters.get(key));
            for (int j = 0; j < values.length; j++) {
                if (j != 0) {
                    parmsForDisplay += ",";
                }
                parmsForDisplay += values[j];
            }
            parmsForDisplay += "<br>";
        }

        String action = request.getParameter("custom_action");

        LaunchModel launchModel = new LaunchModel();
        launchModel.setLtiParms(parmsForDisplay);
        launchModel.setAction(action);

        String returnUrl = request.getParameter("launch_presentation_return_url");
        if (returnUrl == null) {
            returnUrl = "NONE";
        }
        launchModel.setReturnUrl(returnUrl);

        SettingsModel settingsModel = null;
        try {
            settingsModel = settingsService.loadSettings();
            String crs_uuid = request.getParameter("context_id");
            String user_uuid = request.getParameter("user_id");

            Course crs = CourseDbLoader.Default.getInstance().loadByUuid(crs_uuid);
            launchModel.setCourseId(crs.getCourseId());

            User user = UserDbLoader.Default.getInstance().loadByUuid(user_uuid);
            launchModel.setUserId(user.getUserName());

            String restUrl = settingsModel.getLearnUrl() + "/" + PlugInUtil.getUri(Constants.B2Vendor, Constants.B2Handle, "app/courseDetails?cid="
                + crs_uuid + "&uid=" + user_uuid);
            launchModel.setRestUrl(restUrl);

            String restAuthUrl = settingsModel.getLearnUrl() + "/" + PlugInUtil.getUri(Constants.B2Vendor, Constants.B2Handle, "app/getAuthToken?cid="
                + crs_uuid + "&uid=" + user_uuid);
            launchModel.setRestAuthUrl(restAuthUrl);

            if (authenticate(request, settingsModel)) {
                InlineReceiptUtil.addSuccessReceiptToRequest("Validatation succeeded and course and user loaded successfully.");
            }
        } catch (Exception e) {
            InlineReceiptUtil.addErrorReceiptToRequest(e.getMessage());
            return launchModel.getLaunchView();
        }

        return launchModel.getLaunchView();
    }

    @RequestMapping(value = {"/courseDetails"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView courseDetails(HttpServletRequest request, HttpServletResponse response) {
        SettingsModel settingsModel = null;
        CourseDetailsModel courseDetailsModel = new CourseDetailsModel ();
        try {
            settingsModel = settingsService.loadSettings();
            loadData(courseDetailsModel, settingsModel, request.getParameter("cid"), request.getParameter("uid"), "client_credentials", null, null);
            InlineReceiptUtil.addSuccessReceiptToRequest("Course and user REST details loaded successfully.");
        } catch (Exception e) {
            InlineReceiptUtil.addErrorReceiptToRequest(e.getMessage());
        }

        return courseDetailsModel.getCourseDetailsView();
    }

    private void loadData(CourseDetailsModel courseDetailsModel, SettingsModel settingsModel, String crs_uuid, String user_uuid,
                          String grantType, String code, String requestUri) throws Exception {

        Token token = dataFetcherService.authorize(settingsModel, grantType, code, requestUri);
        if (token != null) {
            CourseDetails crsDetails = dataFetcherService.getCourseDetails(settingsModel, token, crs_uuid);
            if (crsDetails != null) {
                courseDetailsModel.setCourseName(crsDetails.getName());
                courseDetailsModel.setCourseDescription(crsDetails.getDescription());
            }

            UserDetails userDetails = dataFetcherService.getUserDetails(settingsModel, token, user_uuid);
            if (userDetails != null) {
                courseDetailsModel.setStudentId(userDetails.getStudentId());
            }
        }
    }

    /**
     * Construct a service provider based on the URL of the request. (That would
     * not happen in real life of course)
     *
     * @param request
     * @return
     */
    OAuthServiceProvider getServiceProvider(HttpServletRequest request) {
        String URL = request.getRequestURL().toString();
        return new OAuthServiceProvider(URL + "/token", URL + "authorize", URL + "/access");
    }


    @RequestMapping(value = {"/handleAuthResponse"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView handleAuthResponse(HttpServletRequest request, HttpServletResponse response) throws GenericLtiException {
        CourseDetailsModel courseDetailsModel = new CourseDetailsModel();
        courseDetailsModel.setCode (request.getParameter("code"));
        SettingsModel settingsModel = null;
        try {
            String cid = ContextManagerFactory.getInstance().getContext().getSession().getGlobalKey(Constants.B2Vendor + "." + Constants.B2Handle + ".cid");
            String uid = ContextManagerFactory.getInstance().getContext().getSession().getGlobalKey(Constants.B2Vendor + "." + Constants.B2Handle + ".uid");

            settingsModel = settingsService.loadSettings();
            loadData(courseDetailsModel, settingsModel, cid, uid, "authorization_code", request.getParameter("code"),
                    makeRedirectUrl(settingsModel));
            InlineReceiptUtil.addSuccessReceiptToRequest("Validatation succeeded and course and user loaded successfully.");
        } catch (Exception e) {
            throw new GenericLtiException(e);
        }

        return courseDetailsModel.getCourseDetailsView();
    }

    private String makeRedirectUrl(SettingsModel settingsModel) throws GenericLtiException {
        settingsModel = settingsService.loadSettings();
        String requestUrl = settingsModel.getLearnUrl() + "/" + PlugInUtil.getUri(Constants.B2Vendor, Constants.B2Handle, "app/handleAuthResponse");
        return requestUrl;
    }

    @RequestMapping(value = {"/getAuthToken"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView authToken(HttpServletRequest request, HttpServletResponse response) throws GenericLtiException {
        ModelAndView mav = new ModelAndView();

        SettingsModel settingsModel = null;
        try {
            ContextManagerFactory.getInstance().getContext().getSession().setGlobalKey(Constants.B2Vendor + "." + Constants.B2Handle + ".cid", request.getParameter("cid"));
            ContextManagerFactory.getInstance().getContext().getSession().setGlobalKey(Constants.B2Vendor + "." + Constants.B2Handle + ".uid", request.getParameter("uid"));

            settingsModel = settingsService.loadSettings();
            OAuthClientRequest oRequest = OAuthClientRequest
                    .authorizationLocation(settingsModel.getLearnUrl() + "/learn/api/public/v1/oauth2/authorizationcode")
                    .setClientId(settingsModel.getKey())
                    .setRedirectURI(makeRedirectUrl(settingsModel)).setResponseType("code").setScope("read write")
                    .buildQueryMessage();

            mav.setViewName("redirect:" + oRequest.getLocationUri());
            return mav;
        } catch (Exception e) {
            throw new GenericLtiException(e);
        }
    }

    public boolean authenticate(HttpServletRequest request, SettingsModel settingsModel) throws GenericLtiException {

        OAuthValidator validator = new SimpleOAuthValidator();
        String oauth_consumer_key = null;

        try {
            OAuthMessage oauthMessage = OAuthServlet.getMessage(request, null);
            oauth_consumer_key = oauthMessage.getConsumerKey();

            // callback URL syntax per LTI spec
            OAuthConsumer consumer = new OAuthConsumer(
                    "about:blank",
                    oauth_consumer_key,
                    settingsModel.getSecret(),
                    null);

            String signatureMethod = oauthMessage.getSignatureMethod();
            String signature = URLDecoder.decode(oauthMessage.getSignature(), "UTF-8");

            // all tokens are empty
            OAuthAccessor accessor = new OAuthAccessor(consumer);
            validator.validateMessage(oauthMessage, accessor);
        } catch (Exception e) {
            throw new GenericLtiException(e);
        }

        return true;
    }

    // Everything below was constructed  based on the OAuth code example below:
    // http://oauth.googlecode.com/svn/code/java/core/
    //
    /**
     * Extract the parts of the given request that are relevant to OAuth.
     * Parameters include OAuth Authorization headers and the usual request
     * parameters in the query string and/or form encoded body. The header
     * parameters come first, followed by the rest in the order they came from
     * request.getParameterMap().
     *
     * @param URL the official URL of this service; that is the URL a legitimate
     *            client would use to compute the digital signature. If this parameter is
     *            null, this method will try to reconstruct the URL from the HTTP request;
     *            which may be wrong in some cases.
     */
    public static OAuthMessage getMessage(HttpServletRequest request, String URL) {
        if (URL == null) {
            URL = request.getRequestURL().toString();
        }
        int q = URL.indexOf('?');
        if (q >= 0) {
            URL = URL.substring(0, q);
            // The query string parameters will be included in
            // the result from getParameters(request).
        }
        return new HttpRequestMessage(request, URL);
    }

    /**
     * Reconstruct the requested URL, complete with query string (if any).
     */
    public static String getRequestURL(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }


}
