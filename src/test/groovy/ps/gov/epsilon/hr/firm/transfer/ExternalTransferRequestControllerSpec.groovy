package ps.gov.epsilon.hr.firm.transfer

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
import guiplugin.ElementsTagLib
import org.junit.Assume
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryList
import ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson
import ps.gov.epsilon.hr.firm.lookups.EmploymentCategory
import ps.gov.epsilon.hr.firm.lookups.JobTitle
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

import java.time.ZonedDateTime

/**
 * unit test for ExternalTransferRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ExternalTransferRequest])
@Build([ExternalTransferRequest,JoinedFirmOperationDocument,
        Employee,EmploymentRecord,JobTitle,EmploymentCategory,LoanRequestRelatedPerson,
        EmployeePromotion,MilitaryRank,Department,ExternalTransferList,ExternalTransferListEmployee])
@TestFor(ExternalTransferRequestController)
class ExternalTransferRequestControllerSpec extends CommonUnitSpec {


    EmployeeService employeeService = mockService(EmployeeService)
    GovernorateService governorateService = mockService(GovernorateService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)

    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    SharedService sharedService = mockService(SharedService)
    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = ExternalTransferRequest
        service_domain = ExternalTransferRequestService
        entity_name = "externalTransferRequest"
        required_properties = PCPUtils.getRequiredFields(ExternalTransferRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        primary_key_values = ["encodedId"]
        exclude_actions = ["list","create"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(ExternalTransferList?.class)
        }
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.governorateService) {
            serviceInstance.governorateService = governorateService
        }

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
        }

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
        model.operationType == EnumOperation.EXTERNAL_TRANSFER
        model.referenceObject == ExternalTransferRequest.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test create action.
     * @expectedResult response with known model.
     */
    def "test_override_create"() {
        setup:
        println("************************test_create********************************")
        when:
        controller.create()
        then:
        model."${entity_name}" == null
        println("test_create done with initialized model ${model}")
    }

    /**
     * @goal test selectEmployee action.
     * @expectedResult response with known model.
     */
    def "test_selectEmployee"() {

        setup:
        println("************************test_selectEmployee********************************")
        when:
        controller.params["employeeId"] = 100L
        controller.selectEmployee()

        then:
        response.json.success == true
        response.json.employeeId == 100L
        println("test selectEmployee done with employeeId: ${response.json.employeeId}")
    }


    /**
     * @goal test selectEmployee action.
     * @expectedResult response with status 404.
     */
    def "test_selectEmployee_not_found"() {

        setup:
        println("************************test_selectEmployee_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.selectEmployee()

        then:
        response.json.success == false
        response.json.employeeId == null
        response.json.message == alertTagLib.error(label: (validationTagLib.message(code: "employee.notFound.error.label"))).toString()
        println("test selectEmployee not found done with message: ${response.json.message}")
    }


    /**
     * @goal test createNewExternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewExternalTransferRequest"() {

        setup:
        println("************************test_createNewExternalTransferRequest********************************")
        Employee employee = Employee.build()
        when:
        controller.params["employeeId"] = employee?.id
        controller.createNewExternalTransferRequest()

        then:
        model != null
        model != [:]
        model.externalTransferRequest.employee.id == employee?.id
        println("test createNewExternalTransferRequest done with model: ${model}")
    }

    /**
     * @goal test createNewExternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewExternalTransferRequest_already_progress"() {

        setup:
        println("************************test_createNewExternalTransferRequest_already_progress********************************")
        Employee employee = Employee.build()
        ExternalTransferRequest externalTransferRequestSaved = ExternalTransferRequest.build(employee:employee,requestStatus:EnumRequestStatus.APPROVED_BY_WORKFLOW)
        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(employee:employee)
        when:
        controller.params["employeeId"] = externalTransferRequest?.employee?.id
        controller.createNewExternalTransferRequest()

        then:
        flash.message == alertTagLib.errorList(data: [(validationTagLib.message(code: "externalTransferRequest.employeeHasRequest.label"))])
        response.redirectedUrl == "/${entity_name}/create"
        println("test createNewExternalTransferRequest already progress done with model: ${response.redirectedUrl}")
    }


    /**
     * @goal test createNewExternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewExternalTransferRequest_not_found"() {

        setup:
        println("************************test_createNewExternalTransferRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewExternalTransferRequest()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test createNewExternalTransferRequest done with response: ${response.status}")
    }


    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList"() {

        setup:
        println("************************test_goToList********************************")

        Employee employee = Employee.build()
        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(employee: employee)
        ExternalTransferList externalTransferList = ExternalTransferList.build()
        ExternalTransferListEmployee.build(externalTransferList:externalTransferList,employee:employee,externalTransferRequest:externalTransferRequest)

        when:
        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.goToList()

        then:
        response.redirectedUrl.toString().contains("/externalTransferList/manageExternalTransferList?encodedId=")
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

    /**
     * @goal test addClearance action.
     * @expectedResult response with known model.
     */
    def "test_addClearance"() {

        setup:
        println("************************test_addClearance********************************")

        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(requestStatus: EnumRequestStatus.APPROVED)

        when:
        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.addClearance()

        then:
        model != [:]
        model.externalTransferRequest.id == externalTransferRequest?.id
        model.externalTransferRequest.requestStatus == externalTransferRequest?.requestStatus
        println("test addClearance done with model: ${model}")
    }

    /**
     * @goal test addClearance action.
     * @expectedResult response with known model.
     */
    def "test_addClearance_not_approved"() {

        setup:
        println("************************test_addClearance_not_approved********************************")

        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(requestStatus: EnumRequestStatus.APPROVED_BY_WORKFLOW)

        when:
        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.addClearance()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test addClearance not approved done with response: ${response.status}")
    }

    /**
     * @goal test addClearance action.
     * @expectedResult response with known model.
     */
    def "test_addClearance_not_found"() {

        setup:
        println("************************test_addClearance_not_found********************************")
        when:
        controller.addClearance()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test addClearance done with response: ${response.status}")
    }


    /**
     * @goal test saveClearance action with ajax request.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save_clearance_ajax"() {
        setup:
        println("************************test_fail_save_clearance_ajax********************************")
        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.saveClearance()

        then:
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test save clearance ajax fail with error size: ${response.json.errorList.size()}")
    }

    /**
     * @goal test saveClearance action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save_clearance_ajax"() {

        setup:
        println("************************test_success_save_clearance_ajax********************************")
        def externalTransferRequest = saveEntity()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.saveClearance()

        then:
        response.json.success == true
        response.json.message.toString().contains("add clearance success")
        response.json.data != null
        response.json.data.id == externalTransferRequest?.id
        println("test save clearance ajax success and message is ${response.json.message}")
    }

    /**
     * @goal test addTransfer action.
     * @expectedResult response with known model.
     */
    def "test_addTransfer"() {

        setup:
        println("************************test_addTransfer********************************")

        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(requestStatus: EnumRequestStatus.APPROVED)

        when:
        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.addTransfer()

        then:
        model != [:]
        model.externalTransferRequest.id == externalTransferRequest?.id
        model.externalTransferRequest.requestStatus == externalTransferRequest?.requestStatus
        println("test addTransfer done with model: ${model}")
    }

    /**
     * @goal test addTransfer action.
     * @expectedResult response with known model.
     */
    def "test_addTransfer_not_approved"() {

        setup:
        println("************************test_addTransfer_not_approved********************************")

        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build(requestStatus: EnumRequestStatus.APPROVED_BY_WORKFLOW)

        when:
        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.addTransfer()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test addTransfer not approved done with response: ${response.status}")
    }

    /**
     * @goal test addTransfer action.
     * @expectedResult response with known model.
     */
    def "test_addTransfer_not_found"() {

        setup:
        println("************************test_addTransfer_not_found********************************")
        when:
        controller.addTransfer()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test addTransfer done with response: ${response.status}")
    }



    /**
     * @goal test save transfer action with ajax request.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save_transfer_ajax"() {
        setup:
        println("************************test_fail_save_transfer_ajax********************************")
        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.saveTransfer()

        then:
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test save transfer ajax fail with error size: ${response.json.errorList.size()}")
    }

    /**
     * @goal test saveTransfer action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save_transfer_ajax"() {

        setup:
        println("************************test_success_save_transfer_ajax********************************")
        def externalTransferRequest = saveEntity()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'

        controller.params["encodedId"] = externalTransferRequest?.encodedId
        controller.saveTransfer()

        then:
        response.json.success == true
        response.json.message.toString().contains("add transfer success")
        response.json.data != null
        response.json.data.id == externalTransferRequest?.id
        println("test save transfer ajax success and message is ${response.json.message}")
    }


    public Object fillEntity(TestDataObject tableData = null) {
        JobTitle jobTitle = JobTitle.build()
        MilitaryRank militaryRank = MilitaryRank.build()
        EmploymentCategory employmentCategory = EmploymentCategory.build()


        EmploymentRecord currentEmploymentRecord = EmploymentRecord.build(
                fromDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                jobTitle:jobTitle,
                employmentCategory:employmentCategory,

        )

        EmployeePromotion currentEmployeeMilitaryRank = EmployeePromotion.build(
                militaryRank: militaryRank,
                dueReason: EnumPromotionReason.EXCEPTIONAL,
                actualDueDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                dueDate: PCPUtils.parseZonedDateTime("20/12/2016")
        )

        Employee employee = Employee.build(
                currentEmploymentRecord:currentEmploymentRecord,
                currentEmployeeMilitaryRank: currentEmployeeMilitaryRank
        )


        def instanceToSave = ExternalTransferRequest.buildWithoutSave(employee: employee)

        return instanceToSave
    }
}