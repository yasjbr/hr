package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.lookups.InspectionCategory
import ps.gov.epsilon.hr.firm.lookups.InspectionCategoryService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.lookups.ProfessionTypeService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.pcore.v2.entity.person.commands.v1.PersonCommand
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import java.time.ZonedDateTime

/**
 * unit test for Applicant controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([Applicant])
@Build([Applicant, JoinedFirmOperationDocument, InspectionCategory])
@TestFor(ApplicantController)
class ApplicantControllerSpec extends CommonUnitSpec {


    ManagePersonService managePersonService = mockService(ManagePersonService)
    SharedService sharedService = mockService(SharedService)
    InspectionCategoryService inspectionCategoryService = mockService(InspectionCategoryService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    ProfessionTypeService professionTypeService = mockService(ProfessionTypeService)
    PersonService personService = mockService(PersonService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)
    LocationService locationService = mockService(LocationService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    def setupSpec() {
        domain_class = Applicant
        service_domain = ApplicantService
        entity_name = "applicant"
        required_properties = PCPUtils.getRequiredFields(Applicant)
        filtered_parameters = ["id"];
        autocomplete_property = "personName"
        exclude_actions = ["autocomplete", "list", "delete"]
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }


    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()



        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }


        if (!controller.managePersonService) {
            controller.managePersonService = managePersonService
        }

        if (!controller.inspectionCategoryService) {
            controller.inspectionCategoryService = inspectionCategoryService
        }

        if (!controller.inspectionCategoryService) {
            controller.inspectionCategoryService = inspectionCategoryService
        }

        if (!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.professionTypeService) {
            serviceInstance.professionTypeService = professionTypeService
            serviceInstance.professionTypeService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.personMaritalStatusService) {
            serviceInstance.personMaritalStatusService = personMaritalStatusService
            serviceInstance.personMaritalStatusService.proxyFactoryService = proxyFactoryService
        }
        if (!serviceInstance.locationService) {
            serviceInstance.locationService = locationService
            serviceInstance.locationService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.APPLICANT
        model.referenceObject == Applicant.name
        println("test_list done with data : ${model}")
    }


    def "test_filterApplicant"() {
        setup:
        println("************************test_filterApplicant********************************")
        when:
        params.applicantCurrentStatus = new ApplicantStatusHistory(applicantStatus: EnumApplicantStatus.ACCEPTED, fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now())
        controller.filterApplicant()
        then:
        def PagedResultList = serviceInstance.search(params)
        PagedResultList.size() >= 0
        println("test_filterApplicant done with data : ${PagedResultList}")
    }

    def "test_saveNewPerson"() {
        setup:
        println("************************test_saveNewPerson********************************")
        def personCommand = new PersonCommand(id: 1L, dateOfBirth: ZonedDateTime.now())
        when:
        controller.saveNewPerson(personCommand)
        then:
        def personCommand1 = managePersonService.saveNewPerson(personCommand, params)
        if (personCommand1.validate()) {
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
        } else {
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.not.created.message"))).toString()

        }
        println("test_saveNewPerson done with data : ${personCommand1}")
    }


    def "test_createNewApplicant"() {
        setup:
        println("************************test_createNewApplicant********************************")
        def testInstance = saveEntity()
        when:
        params.personId = testInstance?.personId
        controller.createNewApplicant()
        then:
        def instance = serviceInstance.getPersonInstanceWithRemotingValues(params)
        instance != null
        println("test_createNewApplicant done with data : ${instance}")
    }

    def "test_getInspectionCategoryByApplicant"() {
        setup:
        println("************************test_getInspectionCategoryByApplicant********************************")
        def testInstance = saveEntity()
        when:
        params["firm.id"] = testInstance?.firm?.id
        params["isRequiredByFirmPolicy"] = true
        params["allInspectionCategory"] = true
        controller.getInspectionCategoryByApplicant()
        then:
        def instance = serviceInstance.getInstance(params)
        if (instance) {
            model.ids == instance?.vacancy?.inspectionCategories?.id?.toList()
            println("test_getInspectionCategoryByApplicant with data : ${instance}")
        } else {
            def inspectionInstance = inspectionCategoryService.autoComplete(params)
            inspectionInstance != null
            println("test_getInspectionCategoryByApplicant with data : ${inspectionInstance}")
        }
    }

    def "test_getInterview"() {
        setup:
        println("************************test_getInterview********************************")
        def testInstance = saveEntity()
        when:
        params["applicant.encodedId"] = testInstance.id
        controller.getInterview()
        then:
        def instance = serviceInstance.getInstance(params)
        model != [:]
        model.tabEntityName == "applicant"
        model.interview == instance?.interview
        model.errorType == "success"
        println("test_getInterview with data : ${testInstance}")
    }


}