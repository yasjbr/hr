package ps.gov.epsilon.hr.firm.suspension

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
import ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import spock.lang.Shared

/**
 * unit test for SuspensionRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionRequest])
@Build([SuspensionRequest, Firm, Employee, JoinedFirmOperationDocument])
@TestFor(SuspensionRequestController)
class SuspensionRequestControllerSpec extends CommonUnitSpec {

    EmployeeService employeeService = mockService(EmployeeService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    PersonService personService = mockService(PersonService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)


    def setupSpec() {
        domain_class = SuspensionRequest
        service_domain = SuspensionRequestService
        entity_name = "suspensionRequest"
        required_properties = PCPUtils.getRequiredFields(SuspensionRequest)
        filtered_parameters = ["id"]
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["list", "delete"]
        is_virtual_delete = true
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()


        if (!employeeService) {
            serviceInstance.employeeService = employeeService
        }

        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }


        if (!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }
    }

    /**
     * @goal test list action.
     * @expectedResult known response redirect with attachment model.
     */
    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.SUSPENSION_REQUEST
        model.referenceObject == SuspensionRequest.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test selectEmployee action.
     * @expectedResult response employee id & suspension type id
     */
    def "test_selectEmployee_success"() {
        setup:
        println("************************test_selectEmployee_success********************************")
        Firm firm = Firm.build()
        Employee employee = Employee.build(firm: firm)

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.params["employee.id"] = employee?.id
        controller.params["suspensionType"] = EnumSuspensionType.MEDICAL
        controller.selectEmployee()

        then:
        response.json.success == true
        println("test selectEmployee success done with : ${response.json.message}")

    }

    /**
     * @goal test selectEmployee action.
     * @expectedResult not found
     */
    def "test_selectEmployee_failed"() {
        setup:
        println("************************test_selectEmployee_failed********************************")
        when:
        controller.params["employee.id"] = null
        controller.params["suspensionType"] = null
        controller.selectEmployee()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test selectEmployee failed done with message: ${response.status}")
    }

    /**
     * @goal test createNewSuspensionRequest action.
     * @expectedResult response with suspension request instance
     */
    def "test_createNewSuspensionRequest"() {

        setup:
        println("************************test_createNewSuspensionRequest********************************")
        Firm firm = Firm.build()
        Employee employee = Employee.build(firm: firm, personId: 1055206)
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.params["employee.id"] = employee?.id
        controller.params["suspensionType"] = EnumSuspensionType.MEDICAL
        controller.createNewSuspensionRequest()

        then:

//        model.suspensionRequest != null
        println("test createNewSuspensionRequest done with model: ${model}")
    }

    /**
     * @goal test createNewSuspensionRequest action.
     * @expectedResult not found
     */
    def "test_createNewSuspensionRequest_not_found"() {
        setup:
        println("************************test_createNewSuspensionRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.params["suspensionType"] = null
        controller.createNewSuspensionRequest()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test test createNewSuspensionRequest not found done with response: ${response.status}")
    }


}