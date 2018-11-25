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
import ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

/**
 * unit test for LoanRequestRelatedPerson controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanRequestRelatedPerson])
@Build([LoanRequestRelatedPerson,LoanListPerson,JoinedFirmOperationDocument,LoanRequest,LoanList,CorrespondenceListStatus])
@TestFor(LoanRequestRelatedPersonController)
class LoanRequestRelatedPersonControllerSpec extends CommonUnitSpec {

    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)

    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = LoanRequestRelatedPerson
        service_domain = LoanRequestRelatedPersonService
        required_properties = PCPUtils.getRequiredFields(LoanRequestRelatedPerson)
        filtered_parameters = ["id"];
        autocomplete_property = "loanRequest.id"
        exclude_actions = ["list", "create", "autocomplete"]

        primary_key_values = ["encodedId"]
        session_parameters = ["firmId": "firm.id", "firm.id": "firm.id"]
        once_save_properties = ["firm"]


        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(LoanList?.class)
        }
    }


    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        sharedService.grailsApplication = grailsApplication

        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.grailsApplication = grailsApplication
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }

        if (!serviceInstance.personService) {
            serviceInstance.personService = personService
            personService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.LOAN_REQUEST_RELATED_PERSON
        model.referenceObject == LoanRequestRelatedPerson.name
        println("test_list done with data : ${model}")
    }


    public LoanRequestRelatedPerson fillEntity(TestDataObject tableData = null) {

        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        LoanRequestRelatedPerson instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            Map addedMap = [:]
            if (tableData.hasSecurity) {
                addedMap.put("springSecurityService", springSecurityService)
            }
            if (tableData.isJoinTable) {
                addedMap.putAll(props)
            }
            instance = tableData?.domain?.buildWithoutValidation(addedMap)
        }

        //to allow get data when list is closed and request approved
        instance.requestedPersonId = (counter+10)
        instance.recordSource = EnumPersonSource.RECEIVED
        instance.loanRequest = LoanRequest.build()
        CorrespondenceListStatus currentStatus = CorrespondenceListStatus.build(correspondenceListStatus:EnumCorrespondenceListStatus.CLOSED)
        LoanList loanList = LoanList.build(currentStatus: currentStatus)
        LoanRequest loanRequest = LoanRequest.build(requestStatus:EnumRequestStatus.APPROVED )
        LoanListPerson loanListPerson = LoanListPerson.build(loanList:loanList,loanRequest:loanRequest)

        instance.loanRequest = loanRequest

        return instance

    }

}