package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
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
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.Department
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
 * unit test for InternalTransferRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([InternalTransferRequest])
@Build([InternalTransferRequest,JoinedFirmOperationDocument,Employee,EmploymentRecord,LoanRequestRelatedPerson,
        JobTitle,EmploymentCategory,EmployeePromotion,MilitaryRank,Department,ExternalTransferRequest])
@TestFor(InternalTransferRequestController)
class InternalTransferRequestControllerSpec extends CommonUnitSpec {


    EmployeeService employeeService = mockService(EmployeeService)
    GovernorateService governorateService = mockService(GovernorateService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    ExternalTransferRequestService externalTransferRequestService = mockService(ExternalTransferRequestService)

    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    SharedService sharedService = mockService(SharedService)
    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = InternalTransferRequest
        service_domain = InternalTransferRequestService
        entity_name = "internalTransferRequest"
        required_properties = PCPUtils.getRequiredFields(InternalTransferRequest)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        primary_key_values = ["encodedId"]
        exclude_actions = ["list","create"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.externalTransferRequestService) {
            serviceInstance.externalTransferRequestService = externalTransferRequestService
        }

        if(!serviceInstance.governorateService) {
            serviceInstance.governorateService = governorateService
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
        model.operationType == EnumOperation.INTERNAL_TRANSFER
        model.referenceObject == InternalTransferRequest.name
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
     * @goal test createNewInternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewInternalTransferRequest"() {

        setup:
        println("************************test_createNewInternalTransferRequest********************************")
        Employee employee = Employee.build()
        when:
        controller.params["employeeId"] = employee?.id
        controller.createNewInternalTransferRequest()

        then:
        model != null
        model != [:]
        model.internalTransferRequest.employee.id == employee?.id
        println("test createNewInternalTransferRequest done with model: ${model}")
    }

    /**
     * @goal test createNewInternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewInternalTransferRequest_already_progress"() {

        setup:
        println("************************test_createNewInternalTransferRequest_already_progress********************************")
        Employee employee = Employee.build()
        InternalTransferRequest internalTransferRequestSaved = InternalTransferRequest.build(employee:employee,requestStatus:EnumRequestStatus.APPROVED_BY_WORKFLOW)
        InternalTransferRequest internalTransferRequest = InternalTransferRequest.build(employee:employee)
        when:
        controller.params["employeeId"] = internalTransferRequest?.employee?.id
        controller.createNewInternalTransferRequest()

        then:
        flash.message == alertTagLib.errorList(data: [(validationTagLib.message(code: "internalTransferRequest.employeeHasRequest.label"))])
        response.redirectedUrl == "/${entity_name}/create"
        println("test createNewInternalTransferRequest already progress done with model: ${response.redirectedUrl}")
    }


    /**
     * @goal test createNewInternalTransferRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewInternalTransferRequest_not_found"() {

        setup:
        println("************************test_createNewInternalTransferRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewInternalTransferRequest()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test createNewInternalTransferRequest done with response: ${response.status}")
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

        EmploymentRecord toEmploymentRecord = EmploymentRecord.build(
                fromDate: PCPUtils.parseZonedDateTime("20/12/2016"),
                jobTitle:jobTitle,
                employmentCategory:employmentCategory,
                employee:employee
        )

        def instanceToSave = InternalTransferRequest.buildWithoutSave(toEmploymentRecord:toEmploymentRecord,employee: employee)

        return instanceToSave
    }
}