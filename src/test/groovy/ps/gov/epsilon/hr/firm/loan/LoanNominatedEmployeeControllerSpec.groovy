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
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import spock.lang.Shared

/**
 * unit test for LoanNominatedEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanNominatedEmployee])
@Build([LoanNominatedEmployee,JoinedFirmOperationDocument])
@TestFor(LoanNominatedEmployeeController)
class LoanNominatedEmployeeControllerSpec extends CommonUnitSpec {

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
        domain_class = LoanNominatedEmployee
        service_domain = LoanNominatedEmployeeService
        entity_name = "loanNominatedEmployee"
        required_properties = PCPUtils.getRequiredFields(LoanNominatedEmployee)
        filtered_parameters = ["employee.id"];
        autocomplete_property = "employee.id"
        primary_key_values = ["encodedId"]
        exclude_actions = ["list","autocomplete","create","save","edit","update"]

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(LoanNominatedEmployee?.class)
        }
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            serviceInstance.organizationService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.LOAN_NOMINATED_EMPLOYEE
        model.referenceObject == LoanNominatedEmployee.name
        println("test_list done with data : ${model}")
    }

}