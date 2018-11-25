package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsClass
import grails.core.GrailsApplication
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferList
import ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequestService
import ps.gov.epsilon.hr.firm.transfer.InternalTransferRequest
import ps.gov.epsilon.hr.firm.transfer.InternalTransferRequestService
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

/**
 * unit test for LoanNoticeReplayRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanNoticeReplayRequest])
@Build([LoanNoticeReplayRequest,LoanNoticeReplayList,LoanNominatedEmployee,JoinedFirmOperationDocument,
        Employee,EmploymentRecord,JobTitle,EmploymentCategory,EmployeePromotion,
        MilitaryRank,LoanNotice])
@TestFor(LoanNoticeReplayRequestController)
class LoanNoticeReplayRequestControllerSpec extends CommonUnitSpec {

    EmployeeService employeeService = mockService(EmployeeService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    GovernorateService governorateService = mockService(GovernorateService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)

    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = LoanNoticeReplayRequest
        service_domain = LoanNoticeReplayRequestService
        entity_name = "loanNoticeReplayRequest"
        required_properties = PCPUtils.getRequiredFields(LoanNoticeReplayRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        exclude_actions = ["list","save"]
        primary_key_values = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true


        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(LoanNoticeReplayList?.class)
        }
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.employeeService) {
            serviceInstance.employeeService = employeeService
            serviceInstance.employeeService.personService = personService
            serviceInstance.employeeService.personMaritalStatusService = personMaritalStatusService
            serviceInstance.employeeService.governorateService = governorateService
            serviceInstance.employeeService.organizationService = organizationService
            serviceInstance.employeeService.personService.proxyFactoryService = proxyFactoryService
        }


        sharedService.grailsApplication = grailsApplication

        if(!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.grailsApplication = grailsApplication
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
    }



    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.LOAN_NOTICE_REPLAY_REQUEST
        model.referenceObject == LoanNoticeReplayRequest.name
        println("test_list done with data : ${model}")
    }


    /**
     * @goal test save action.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save"() {
        setup:
        println("************************test_fail_save********************************")

        when:
        request.method = 'POST'
        controller.save()

        then:
        view == 'create'
        required_properties.each { String property ->
            model?."${entity_name}"?."${property}" == null
        }
        model?."${entity_name}"?.errors?.allErrors?.size() >= 1
        println("test save fail with: ${model} and error size: ${model?."${entity_name}"?.errors?.allErrors?.size()}")
    }

    /**
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save"() {

        setup:
        println("************************test_success_save********************************")
        saveEntity()
        def previousCount = domain_class.count()
        def instanceToSave = fillEntity()
        when:
        request.method = 'POST'

        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }
        sendParams("employee.id", instanceToSave, controller.params)
        sendParams("loanNotice.encodedId", instanceToSave, controller.params)

        counter++;
        controller.save()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test save success and new count is ${newCount}")
    }

    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save_ajax"() {
        setup:
        println("************************test_fail_save_ajax********************************")
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        include_save_properties.each{ TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each{property ->
                controller.params[object.paramName + "." + property] = null
            }
        }
        controller.save()

        then:
        def errorList = []
        required_properties.each { String property ->
            if(!exclude_save_properties.contains(property)){
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each{
            response.json.message.contains(it)
        }
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test save ajax fail with error size: ${response.json.errorList.size()}")
    }

    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save_ajax"() {

        setup:
        println("************************test_success_save_ajax********************************")
        saveEntity()
        def instanceToSave = fillEntity()
        def previousCount = domain_class.count()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }

        sendParams("employee.id", instanceToSave, controller.params)
        sendParams("loanNotice.encodedId", instanceToSave, controller.params)

        controller.save()

        then:
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount != newCount
        println("test save ajax success and new count is ${newCount}")
        counter++;
    }

    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList"() {

        setup:
        println("************************test_goToList********************************")

        LoanNoticeReplayRequest loanNoticeReplayRequest = saveEntity()
        LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.build()
        LoanNominatedEmployee.build(loanNoticeReplayList:loanNoticeReplayList,loanNoticeReplayRequest:loanNoticeReplayRequest)

        when:
        controller.params["encodedId"] = loanNoticeReplayRequest?.encodedId
        controller.goToList()

        then:
        response.redirectedUrl.toString().contains("/loanNoticeReplayList/manageList?encodedId=")
        println("test goToList done with model: ${model}")
    }

    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList_not_found"() {

        setup:
        println("************************test_goToList_not_found********************************")
        when:
        controller.goToList()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test goToList done with response: ${response.status}")
    }



    public Object fillEntity(TestDataObject tableData = null) {

        JobTitle jobTitle = JobTitle.build()
        MilitaryRank militaryRank = MilitaryRank.build()
        EmploymentCategory employmentCategory = EmploymentCategory.build()
        LoanNotice loanNotice = LoanNotice.build()

        EmployeePromotion currentEmployeeMilitaryRank = EmployeePromotion.build(
                militaryRank: militaryRank,
                dueReason: EnumPromotionReason.EXCEPTIONAL,
                actualDueDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                dueDate: PCPUtils.parseZonedDateTime("20/12/2016")
        )

        EmploymentRecord currentEmploymentRecord = EmploymentRecord.build(
                fromDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                jobTitle:jobTitle,
                employmentCategory:employmentCategory,

        )

        Employee employee = Employee.build(
                currentEmploymentRecord:currentEmploymentRecord,
                currentEmployeeMilitaryRank: currentEmployeeMilitaryRank
        )

        def instanceToSave = LoanNoticeReplayRequest.buildWithoutSave(loanNotice:loanNotice,requestStatus:  EnumRequestStatus.CREATED,currentEmploymentRecord:currentEmploymentRecord,employee: employee)

        return instanceToSave
    }
}