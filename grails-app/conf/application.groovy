//disable spring security
//grails.plugin.springsecurity.active = true

//Zoned Date Time Config
grails.gorm.default.mapping = {
    // New JDK8 ISO ThreeTen JSR-310
    "user-type" type: ps.police.userTypes.persistent.dateTime.PersistentDependencyDate, class: java.time.ZonedDateTime
    // Default mapping to Dependency Date
    "user-type" type: ps.police.userTypes.persistent.dateTime.PersistentTimestampWithTimeZone, class: java.time.LocalDateTime
    // Map LocalDateTime to PostgreSQL Timestamp with Timezone
//    id generator: 'ps.police.postgresql.PCPSequenceGenerator', params: [prefer_sequence_per_entity: true]
    id generator: 'ps.gov.epsilon.hr.config.EPHRSequenceGenerate', type: String, params: [prefer_sequence_per_entity: true]
    //make all id in system as string
}

//Application Config
grails.applicationName = "EPHR"
grails.applicationKey = "EPHR"
grails.applicationTempDir = "tempDir/"
grails.securityAdminRole = "ROLE_EPHR_SECURITY_ADMIN"

//enable/disable boot strap execute
grails.initBootStrapData = true

//Server Config
server.contextPath = "/${grails.applicationName}"
server.port = 8078
server.ipAddress = "localhost"

esb.host = "192.168.1.114"
esb.port = 9091
esb.context = "PESB"

//Spring Security Core Plugin
grails.permission.execluded.controllers = ['login', 'logout', 'example', 'home']
grails.permission.builtin.controllers = ["navigationItem", "role", "permission", "secGroup", "joinedGroupPermission", "joinedRoleGroup", "user", "joinedUserRole", "requestmap", "example", "auditLogEvent"]
grails.plugin.springsecurity.requestMap.className = 'ps.police.security.Requestmap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'
grails.plugin.springsecurity.rejectIfNoRule = true
grails.plugin.springsecurity.logout.postOnly = false
//grails.plugin.springsecurity.logout.handlerNames = ['customSessionLogoutHandler','securityContextLogoutHandler']
grails.plugin.springsecurity.successHandler.alwaysUseDefault = true
grails.plugin.springsecurity.successHandler.defaultTargetUrl = '/home/index'

//environment config
environments {
    development {
        environment = "${server.ipAddress}:${server.port}"
        grails.plugin.springsecurity.cas.active = false
        grails.serverURL = "http://${environment}/${grails.applicationName}"
        grails.plugin.springsecurity.cas.serverUrlPrefix = "${grails.serverURL}"
        grails.plugin.springsecurity.logout.afterLogoutUrl = "${grails.serverURL}"
        grails.plugin.springsecurity.cas.serviceUrl = "https://${environment}/${grails.applicationName}/j_spring_cas_security_check"
        grails.plugin.springsecurity.cas.proxyCallbackUrl = "https://${environment}/${grails.applicationName}/secure/receptor"


        grails.developmentModeWithServiceCatalog = false

        //Used for security plugin and service catalog
//        remoting.SERVICE_CATALOG_URL = "http://${server.ipAddress}:9090/ServiceCatalog-II"
//        remoting.CORE_URL = "http://${server.ipAddress}:8070/PCore-II"
//        remoting.HR_URL = "http://${server.ipAddress}:8078/EPHR"

        remoting.SERVICE_CATALOG_URL = "http://${esb.host}:${esb.port}/${esb.context}"
        remoting.CORE_URL = "http://${esb.host}:${esb.port}/${esb.context}"
        remoting.HR_URL = "http://${esb.host}:${esb.port}/${esb.context}"
    }
    test {
        environment = "${server.ipAddress}:${server.port}"
        grails.plugin.springsecurity.cas.active = false
        grails.plugin.springsecurity.active = false
        grails.serverURL = "http://${environment}/${grails.applicationName}"
        grails.plugin.springsecurity.cas.serverUrlPrefix = "https://${environment}/cas"
        grails.plugin.springsecurity.logout.afterLogoutUrl = "https://${environment}/cas/logout?url=${grails.serverURL}&service=${grails.serverURL}"
        grails.plugin.springsecurity.cas.serviceUrl = "https://${environment}/${grails.applicationName}/j_spring_cas_security_check"
        grails.plugin.springsecurity.cas.proxyCallbackUrl = "https://${environment}/${grails.applicationName}/secure/receptor"

        //Service Catalog Url's Config
        remoting.SERVICE_CATALOG_URL = "http://${esb.host}:${esb.port}/${esb.context}"
        remoting.CORE_URL = "http://${esb.host}:${esb.port}/${esb.context}"
    }
    production {
        environment = "epsilon.test"
        grails.plugin.springsecurity.cas.active = true
        grails.serverURL = "http://${environment}/${grails.applicationName}"
        grails.plugin.springsecurity.cas.serverUrlPrefix = "https://${environment}/cas"
        grails.plugin.springsecurity.logout.afterLogoutUrl = "https://${environment}/cas/logout?url=${grails.serverURL}&service=${grails.serverURL}"
        // grails.plugin.springsecurity.cas.serviceUrl = "http://${environment}/${grails.applicationName}/j_spring_cas_security_check"
        grails.plugin.springsecurity.cas.serviceUrl = "http://${environment}/${grails.applicationName}/login/cas"
        grails.plugin.springsecurity.cas.proxyCallbackUrl = "http://${environment}/${grails.applicationName}/secure/receptor"

        //Used for security plugin and service catalog
        remoting.SERVICE_CATALOG_URL = "http://${esb.host}:${esb.port}/${esb.context}"
        remoting.CORE_URL = "http://${esb.host}:${esb.port}/${esb.context}"
        remoting.HR_URL = "http://${esb.host}:${esb.port}/${esb.context}"

        grails.applicationTempDir = "/opt/webapps/EPHR/temp/"

        quartz.autoStartup = true;
    }
//    worker2{
//        environment = "epsilon-p.sec.ps"
//        grails.plugin.springsecurity.cas.active = true
//        grails.serverURL = "http://${environment}/${grails.applicationName}"
//        grails.plugin.springsecurity.cas.serverUrlPrefix = "https://${environment}/cas"
//        grails.plugin.springsecurity.logout.afterLogoutUrl = "https://${environment}/cas/logout?url=${grails.serverURL}&service=${grails.serverURL}"
//        // grails.plugin.springsecurity.cas.serviceUrl = "http://${environment}/${grails.applicationName}/j_spring_cas_security_check"
//        grails.plugin.springsecurity.cas.serviceUrl = "http://${environment}/${grails.applicationName}/login/cas"
//        grails.plugin.springsecurity.cas.proxyCallbackUrl = "http://${environment}/${grails.applicationName}/secure/receptor"
//
//        //Used for security plugin and service catalog
//        remoting.SERVICE_CATALOG_URL = "http://${esb.host}:${esb.port}/${esb.context}"
//        remoting.CORE_URL = "http://${esb.host}:${esb.port}/${esb.context}"
//        remoting.HR_URL = "http://${esb.host}:${esb.port}/${esb.context}"
//
//        grails.applicationTempDir = "/opt/webapps/EPHR/temp/"
//
//        quartz.autoStartup=false;
//    }

    //Spring Security CAS Plugin Config:
    grails.plugin.springsecurity.cas.serverUrlEncoding = 'UTF-8'
    grails.plugin.springsecurity.cas.loginUri = '/login?locale=ar'
    grails.plugin.springsecurity.cas.sendRenew = false
    grails.plugin.springsecurity.cas.key = "${grails.applicationName}"
//	grails.plugin.springsecurity.cas.filterProcessesUrl = '/j_spring_cas_security_check'
    grails.plugin.springsecurity.cas.filterProcessesUrl = '/login/cas'
    grails.plugin.springsecurity.cas.artifactParameter = 'ticket'
    grails.plugin.springsecurity.cas.serviceParameter = 'service'
    grails.plugin.springsecurity.cas.useSingleSignout = true
    grails.plugin.springsecurity.cas.proxyReceptorUrl = '/secure/receptor'


}

grails {
    plugin {
        //Added by the Audit-Logging plugin:
        auditLog {
            actorClosure = { request, session ->
                request.applicationContext.springSecurityService.principal?.username
            }
            TRUNCATE_LENGTH = 1000
            auditDomainClassName = "ps.gov.epsilon.hr.firm.audit.AuditLog"
            auditPackage = "ps.gov.epsilon.hr.firm.audit"
        }

        // Added for workflow plugin
        workflow {
            departmentDomain = "ps.gov.epsilon.hr.firm.Department"
            jobTitleDomain = "ps.gov.epsilon.hr.firm.lookups.JobTitle"
            jobCategoryDomain = "ps.gov.epsilon.hr.firm.lookups.JobCategory"

            departmentService = "departmentService"
            jobTitleService = "jobTitleService"
            jobCategoryService = "jobCategoryService"
            notificationService = "workflowNotificationService"
        }
    }
}
