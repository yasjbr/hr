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
 * unit test for LoanRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanRequest])
@Build([LoanRequest,LoanList,LoanListPerson,JoinedFirmOperationDocument,
        Employee,EmploymentRecord,JobTitle,EmploymentCategory,EmployeePromotion,
        MilitaryRank])
@TestFor(LoanRequestController)
class LoanRequestControllerSpec extends CommonUnitSpec {

    OrganizationService organizationService = mockService(OrganizationService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)

    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = LoanRequest
        service_domain = LoanRequestService
        entity_name = "loanRequest"
        required_properties = PCPUtils.getRequiredFields(LoanRequest)
        filtered_parameters = ["requestedJob.id"];
        autocomplete_property = "requestedJob.id"
        exclude_actions = ["list"]
        primary_key_values = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true


        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(LoanList?.class)
        }
    }


    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.personService) {
            serviceInstance.personService = personService
            personService.proxyFactoryService = proxyFactoryService
        }

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            organizationService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.LOAN_REQUEST
        model.referenceObject == LoanRequest.name
        println("test_list done with data : ${model}")
    }


    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList"() {

        setup:
        println("************************test_goToList********************************")

        LoanRequest loanRequest = saveEntity()
        LoanList loanList = LoanList.build()
        LoanListPerson.build(loanList:loanList,loanRequest:loanRequest)

        when:
        controller.params["encodedId"] = loanRequest?.encodedId
        controller.goToList()

        then:
        response.redirectedUrl.toString().contains("/loanList/manageList?encodedId=")
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
}