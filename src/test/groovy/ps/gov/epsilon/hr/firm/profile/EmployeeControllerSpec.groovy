package ps.gov.epsilon.hr.firm.profile

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.springframework.http.HttpStatus
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.domains.v1.TrackingInfo
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for Employee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([Employee])
@Build([Employee,JoinedFirmOperationDocument,EmploymentRecord,EmployeePromotion,MilitaryRank,Department, LoanRequestRelatedPerson])
@TestFor(EmployeeController)
class EmployeeControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    ManagePersonService managePersonService = mockService(ManagePersonService)
    PersonService personService = mockService(PersonService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    GovernorateService governorateService = mockService(GovernorateService)
    ManageLocationService manageLocationService = mockService(ManageLocationService)
    OrganizationService organizationService = mockService(OrganizationService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)

    def setupSpec() {
        domain_class = Employee
        service_domain = EmployeeService
        entity_name = "employee"
        hashing_entity = "id"
        required_properties = PCPUtils.getRequiredFields(Employee)
        filtered_parameters = ["personId"];
        autocomplete_property = "transientData.personDTO.localFullName"
        exclude_actions = ["delete","list"]
        primary_key_values = ["encodedId","id"]

        //currentEmploymentRecord
        TestDataObject currentEmploymentRecord = new TestDataObject()
        currentEmploymentRecord.domain = EmploymentRecord
        currentEmploymentRecord.objectName = "employmentRecord"
        currentEmploymentRecord.paramName = "employmentRecordData"
        currentEmploymentRecord.requiredProperties = PCPUtils.getRequiredFields(EmploymentRecord)

        //currentEmployeeMilitaryRank
        TestDataObject currentEmployeeMilitaryRank = new TestDataObject()
        currentEmployeeMilitaryRank.domain = EmployeePromotion
        currentEmployeeMilitaryRank.objectName = "employeePromotion"
        currentEmployeeMilitaryRank.paramName = "militaryRankData"
        currentEmployeeMilitaryRank.requiredProperties = PCPUtils.getRequiredFields(EmployeePromotion)

        include_save_properties = [currentEmploymentRecord,currentEmployeeMilitaryRank]

    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
        if(!controller.managePersonService) {
            managePersonService.manageLocationService = manageLocationService
            controller.managePersonService = managePersonService
        }

        if(!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }
        if(!serviceInstance.governorateService) {
            serviceInstance.governorateService = governorateService
            serviceInstance.governorateService.proxyFactoryService = proxyFactoryService
        }
        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            serviceInstance.organizationService.proxyFactoryService = proxyFactoryService
        }
        if(!serviceInstance.personMaritalStatusService) {
            serviceInstance.personMaritalStatusService = personMaritalStatusService
            serviceInstance.personMaritalStatusService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.EMPLOYEE
        model.referenceObject == Employee.name
        println("test_list done with data : ${model}")
    }

    def "test_getPerson_success"() {
        setup:
        println("************************test_getPerson_success********************************")
        when:
        controller.params["personId"] = 100L
        controller.getPerson()

        then:
        response.json.success == true
        response.json.personId == 100L
        println("test getPerson success done with personId: ${response.json.personId}")
    }

    def "test_getPerson_failed"() {
        setup:
        println("************************test_getPerson_failed********************************")
        when:
        controller.params["personId"] = null
        controller.getPerson()

        then:
        response.json.success == false
        response.json.personId == null
        println("test getPerson failed done with message: ${response.json.message}")
    }

    def "test_getPerson_failed_duplicated_employee"() {
        setup:
        Employee.build(personId: 100L)
        println("************************test_getPerson_failed_duplicated_employee********************************")
        when:
        controller.params["personId"] = 100L
        controller.getPerson()

        then:
        response.json.success == false
        response.json.personId == null
        println("test getPerson duplicated done with message: ${response.json.message}")
    }


    /**
     * @goal test createNewEmployee action.
     * @expectedResult response with known model.
     */
    def "test_createNewEmployee"() {

        setup:
        println("************************test_createNewEmployee********************************")
        when:
        controller.params["personId"] = 1750L
        controller.createNewEmployee()

        then:
        model != null
        model != [:]
        model.employee.personId == 1750L
        println("test createNewEmployee done with model: ${model}")
    }

    /**
     * @goal test createNewEmployee action.
     * @expectedResult response with known model.
     */
    def "test_createNewEmployee_not_found"() {

        setup:
        println("************************test_createNewEmployee_not_found********************************")
        when:
        controller.params["personId"] = null
        controller.createNewEmployee()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test createNewEmployee done with response: ${response.status}")
    }

    def "test_createNewPerson"() {
        setup:
        println("************************test_createNewPerson********************************")
        when:
        controller.createNewPerson()

        then:
        model == [:]
        println("test createNewPerson done no data : ${model}")
    }

    def "test_saveNewPerson_success"() {
        setup:
        println("************************test_saveNewPerson_success********************************")

        controller.params["localFirstName"] = "first"
        controller.params["localMotherName"] = "mother"
        controller.params["localSecondName"] = "second"
        controller.params["localFourthName"] = "fourth"
        controller.params["recentCardNo"] = "112233"
        controller.params["recentPassportNo"] = "445566"
        controller.params["localOldName"] = "old"
        controller.params["countryId"] = 45L

        when:
        request.makeAjaxRequest()
        controller.saveNewPerson()

        then:
        response.json.success == true
        response.json.data != null
        response.json.errorList.size() == 0
        println("test saveNewPerson success with data size: ${response.json.data}")
    }

    def "test_saveNewPerson_failed"() {
        setup:
        println("************************test_saveNewPerson_failed********************************")

        when:
        request.makeAjaxRequest()
        controller.saveNewPerson()

        then:
        response.json.success == false
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test saveNewPerson failed with error size: ${response.json.errorList.size()}")
    }

}