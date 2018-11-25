package ps.gov.epsilon.hr

import grails.plugin.springsecurity.SpringSecurityService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import ps.gov.epsilon.hr.common.SettingService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.security.commands.v1.UserCommand

import java.text.SimpleDateFormat

class SessionInterceptor {

    SpringSecurityService springSecurityService
    def logoutHandlers
    SettingService settingService
    MessageSource messageSource
    static final _sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")


    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone()
    }

    SessionInterceptor(){
        matchAll()
    }

    boolean before() {
        try {
            def isLoggedIn = springSecurityService.isLoggedIn();
            if (!isLoggedIn && !request.xhr) {
                // Resolves the infinite redirect ..
                if (controllerName.equals('logout')) {
                    return true
                }
                if (controllerName.equals('login') && actionName.equals('authAjax')) {
                    return true
                }
                if (!controllerName.equals('login')) {
//                    redirect(action: 'auth', controller: 'login', params: params);
                    return true
                }
            }

            if (isLoggedIn && !PCPSessionUtils.getValue("sessionUserInfo")) {
                UserCommand user = springSecurityService?.principal // not null as user has logged in.
                if (user && user?.username){
                    try {
                        //set user additional info
                        settingService.initUserSession(user, session)
                        return true
                    }
                    catch (Exception e) {
                        def auth = springSecurityService?.getAuthentication()
                        if (auth) {
                            logoutHandlers.each { handler ->
                                handler.logout(request, response, auth)
                            }
                        }
                        def session = request.getSession(false);
                        if (session != null) {
                            session.invalidate();
                        }
                        flash.status = 'fail'
                        flash.message= messageSource.getMessage(e.getMessage(), null, e.getMessage(), LocaleContextHolder.getLocale())
                        redirect(action: 'auth', controller: 'login', params: params);
                    }
                }
            }else{
                return true
            }
        } catch (Exception e) {
            e.printStackTrace()
            println("Session Filter Exception occured at: ${_sdf.format(new Date())}\n" + e.getMessage())
            return false;
        }
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
