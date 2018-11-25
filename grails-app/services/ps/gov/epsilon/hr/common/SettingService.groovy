package ps.gov.epsilon.hr.common

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import grails.util.Environment
import grails.util.Holders
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.security.commands.v1.UserCommand

import javax.servlet.http.HttpSession

@Transactional
class SettingService {

    SpringSecurityService springSecurityService


    void initUserSession(UserCommand user, HttpSession session) {
        if (!PCPSessionUtils.getValue("sessionUserInfo")) {
            println(user?.getInfo())
            Boolean developmentModeWithServiceCatalog = Holders.grailsApplication.config.grails.developmentModeWithServiceCatalog
            if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {

                //don't change it (FOR DEPLOYMENT)
                PCPSessionUtils.setValue("sessionUserInfo", "sessionUserInfo")
                PCPSessionUtils.setValue("governorateId", user?.governorateId)
                PCPSessionUtils.setValue("governorateName", user?.governorateName)
                PCPSessionUtils.setValue("firmId", user?.firmId)
                PCPSessionUtils.setValue("firmCode", user?.firmCode)
                PCPSessionUtils.setValue("firmName", user?.firmName)
                PCPSessionUtils.setValue("departmentId", user?.departmentId)
                PCPSessionUtils.setValue("departmentName", user?.departmentName)
                PCPSessionUtils.setValue("jobTitleId", user?.jobTitleId)
                PCPSessionUtils.setValue("jobTitleName", user?.jobTitleName)
                PCPSessionUtils.setValue("personId", user?.personId)

            }else{

                //implement your custom code here
                PCPSessionUtils.setValue("sessionUserInfo", "sessionUserInfo")
                PCPSessionUtils.setValue("governorateId", user?.governorateId)
                PCPSessionUtils.setValue("governorateName", user?.governorateName)
                PCPSessionUtils.setValue("firmId", user?.firmId)
                PCPSessionUtils.setValue("firmCode", user?.firmCode)
                PCPSessionUtils.setValue("firmName", user?.firmName)
                PCPSessionUtils.setValue("departmentId", user?.departmentId)
                PCPSessionUtils.setValue("departmentName", user?.departmentName)
                PCPSessionUtils.setValue("jobTitleId", user?.jobTitleId)
                PCPSessionUtils.setValue("jobTitleName", user?.jobTitleName)
                PCPSessionUtils.setValue("personId", user?.personId)

            }
        }
    }
}
