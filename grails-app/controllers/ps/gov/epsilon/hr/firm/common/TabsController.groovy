package ps.gov.epsilon.hr.firm.common

import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.web.util.WebUtils
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.FirmService
import ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayRequest
import ps.gov.epsilon.hr.firm.loan.LoanNoticeService
import ps.gov.epsilon.hr.firm.profile.EmployeeInternalAssignation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.profile.EmploymentRecordService
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.gov.epsilon.hr.firm.training.TrainingRecord
import ps.gov.epsilon.workflow.WorkFlowProcessService
import ps.gov.epsilon.workflow.WorkflowPathHeader
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.person.EnumDomainName

class TabsController {

    GrailsApplication grailsApplication
    EmployeeService employeeService
    EmploymentRecordService employmentRecordService
    LoanNoticeService loanNoticeService
    SharedService sharedService
    WorkFlowProcessService workFlowProcessService
    FirmSettingService firmSettingService

    def listInLine = {
        String domainClassName = params.remove("tabEntityName");

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }

//        //support attachment into tabs.
//        Map mapAttach = [:]
//        if (domainClassName == "employmentRecordTab") {
//            mapAttach = sharedService.getAttachmentTypeListAsMap(EmploymentRecord.getName(), EnumOperation.EMPLOYMENT_RECORD)
//        } else if (domainClassName == "trainingRecordTab") {
//            mapAttach = sharedService.getAttachmentTypeListAsMap(TrainingRecord.getName(), EnumOperation.TRAINING_RECORD)
//        } else if (domainClassName == "employeeInternalAssignationTab") {
//            mapAttach = sharedService.getAttachmentTypeListAsMap(EmployeeInternalAssignation.getName(), EnumOperation.EMPLOYEE_INTERNAL_ASSIGNATION)
//        } else {
//            mapAttach = [:]
//        }
//        map.putAll(mapAttach)


        if (domainClassName) {
            render(template: "/${domainClassName}/inLine/list", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    def createInLine = {
        String domainClassName = params.remove("tabEntityName");
        def domainClassInstance = grailsApplication.getArtefactByLogicalPropertyName("Domain", domainClassName)
        domainClassInstance = domainClassInstance.newInstance()
        if (params.boolean("withOthersData")) {
            Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
            domainClassInstance = serviceObject.getInstanceWithRemotingValues(new GrailsParameterMap([isNewInstance: "true", "employee.id": params["employee.id"]], request))
        }


        domainClassInstance.properties = params

        if (params["loanNotice.encodedId"]) {
            domainClassInstance.loanNotice = loanNoticeService.getInstanceWithRemotingValues(new GrailsParameterMap(["encodedId": params["loanNotice.encodedId"]], request))
        }

        Map map = [:]
        if (domainClassName.equalsIgnoreCase(LoanNoticeReplayRequest.getName().split("\\.").toList().last())) {
            WorkflowPathHeader workflowPathHeader = workFlowProcessService.generateRequestWorkflowPath(
                    null, null, null, null,
                    LoanNoticeReplayRequest.getName(),
                    domainClassInstance?.id,
                    false)
            // sort workflow path details
            workflowPathHeader?.workflowPathDetails = workflowPathHeader?.workflowPathDetails?.sort { a, b -> b.sequence <=> a.sequence }
            // add workflowPathHeader to map
            map.workflowPathHeader = workflowPathHeader
        }



        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }

        if (domainClassName.equalsIgnoreCase("trainingRecord")) {

            /**
             * check if firm sync with AOC or not sync.
             */
            Long firmId = employeeService?.getInstance(new GrailsParameterMap(['id': params["employee.id"]], WebUtils.retrieveGrailsWebRequest().getCurrentRequest()))?.firm?.id
            String isSync = firmSettingService?.getFirmSettingValue("CENTRALIZED_WITH_AOC", firmId) ?: false
            Boolean hasAocRole = SpringSecurityUtils?.ifAnyGranted(ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value)
            if (!isSync.equalsIgnoreCase("true") && hasAocRole) {
                map.put("firmId", PCPSessionUtils.getValue("firmId"))
            }else if (isSync.equalsIgnoreCase("true") && !hasAocRole) {
                map.put("firmId", firmId)
            }else if (!isSync.equalsIgnoreCase("true") && !hasAocRole) {
                map.put("firmId", firmId)
            }
        }


        if (params.boolean("withEmployee")) {
            domainClassInstance.employee = employeeService.getInstanceWithRemotingValues(new GrailsParameterMap([id: domainClassInstance?.employee?.id], request))
        }

        if (params.boolean("withEmploymentRecord")) {
            domainClassInstance.employmentRecord = employmentRecordService.getInstanceWithRemotingValues(new GrailsParameterMap(["isActive": "true", "employee.id": params["employee.id"]], request))
        }

        if (domainClassName) {
            render(template: "/${domainClassName}/inLine/create", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    def editInLine = {
        String domainClassName = params.remove("tabEntityName");
        Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
        Object domainClassInstance = serviceObject.getInstance(params)
        if (params.withRemoting && params.withRemoting != 'false') {
            domainClassInstance = serviceObject.getInstanceWithRemotingValues(params)
        } else {
            domainClassInstance = serviceObject.getInstance(params)
        }
        Map map = [:]
        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)
        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }
        if (domainClassInstance) {
            render(template: "/${domainClassName}/inLine/edit", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    def showInLine = {
        String domainClassName = params.remove("tabEntityName");
        Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
        Object domainClassInstance
        if (params.withRemoting && params.withRemoting != 'false') {
            domainClassInstance = serviceObject.getInstanceWithRemotingValues(params)
        } else {
            domainClassInstance = serviceObject.getInstance(params)
        }

        if (params.boolean("isInterview")) {
            domainClassInstance = domainClassInstance?.interview
            domainClassName = "interview"
        }

        if (params.boolean("isTraineeListEmployee")) {
            domainClassInstance = domainClassInstance?.traineeListEmployee
            domainClassName = "traineeListEmployee"
        }

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        map.put(domainClassName, domainClassInstance)

        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }

        if (params.boolean("isInterview")) {
            render(template: "/interview/inLine/show2", model: map)
        } else if (params.boolean("isTraineeListEmployee")) {
            render(template: "/traineeListEmployee/inLine/show", model: map)
        } else if (domainClassInstance) {
            render(template: "/${domainClassName}/inLine/show", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }

    def loadTab = {
        Map dataModel = [:]
        String tabName = params["tabName"]
        String holderEntityName = params["holderEntityName"]
        String holderEntityId = params["holderEntityId"]
        Long holderPersonId = params.long("holderPersonId")
        String tabEntityName = params["tabEntityName"]
        String phaseName = params["phaseName"]
        Boolean isReadOnly = params.boolean("isReadOnly")
        Boolean loadHolderEntityInformation = params.boolean("loadHolderEntityInformation")
        Boolean preventDataTableTools = params.boolean("preventDataTableTools")
        if (!holderEntityId) {
            render "${message(code: 'default.not.found.message', args: [message(code: holderEntityName + '.entity', default: 'default' + holderEntityName), holderEntityId])}"
        } else {
            Map mapAttach = [:]
            if (tabEntityName == "employmentRecord") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EmploymentRecord.getName(), EnumOperation.EMPLOYMENT_RECORD, EnumOperation.EMPLOYEE)
            } else if (tabEntityName == "trainingRecord") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(TrainingRecord.getName(), EnumOperation.TRAINING_RECORD, EnumOperation.EMPLOYEE)

                /**
                 * check if firm sync with AOC or not sync.
                 */
                Long firmId = employeeService?.getInstance(new GrailsParameterMap(['id': holderEntityId], WebUtils.retrieveGrailsWebRequest().getCurrentRequest()))?.firm?.id
                dataModel.put("isSync", firmSettingService?.getFirmSettingValue("CENTRALIZED_WITH_AOC", firmId) ?: false)


            } else if (tabEntityName == "employeeInternalAssignation") {
                mapAttach = sharedService.getAttachmentTypeListAsMap(EmployeeInternalAssignation.getName(), EnumOperation.EMPLOYEE_INTERNAL_ASSIGNATION, EnumOperation.EMPLOYEE)
            } else {
                mapAttach = [:]
            }
            dataModel.put("entityId", holderEntityId)
            dataModel.put("holderPersonId", holderPersonId)
            dataModel.put("tabEntityName", tabEntityName)
            dataModel.put("phaseName", phaseName)
            dataModel.put("isReadOnly", isReadOnly)
            dataModel.put("preventDataTableTools", preventDataTableTools)
            dataModel.putAll(mapAttach)
            if (loadHolderEntityInformation) {
                Object serviceObject = Holders.applicationContext.getBean(holderEntityName + "Service")
                Object domainClassInstance
                params.encodedId = holderEntityId
                if (params.withRemoting && params.withRemoting != 'false') {
                    domainClassInstance = serviceObject.getInstanceWithRemotingValues(params)
                } else {
                    domainClassInstance = serviceObject.getInstance(params)
                }
                dataModel.put(holderEntityName, domainClassInstance)
            }
            render(template: "/${holderEntityName}/tabs/${tabName}", model: dataModel)
        }
    }

    void removeCommonParams(GrailsParameterMap params) {
        params.remove("id")
    }

    def showThreadInLine = {
        String domainClassName = params.remove("tabEntityName");
        Object serviceObject = Holders.applicationContext.getBean(domainClassName + "Service")
        List threadList = serviceObject.getThreadWithRemotingValues(params)?.resultList

        Map map = [:]
        map.put("tabEntityName", domainClassName)
        map.put('threadList', threadList)

        removeCommonParams(params)
        params.each { k, v ->
            map.put(k, v)
        }

        if (threadList) {
            render(template: "/${domainClassName}/inLine/showThread", model: map)
        } else {
            render "${message(code: 'default.systemError.label')}"
        }
    }
}
