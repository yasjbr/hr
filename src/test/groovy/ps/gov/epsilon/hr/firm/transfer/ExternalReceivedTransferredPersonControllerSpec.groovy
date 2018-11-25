package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.junit.Assume
import org.springframework.http.HttpStatus
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.core.person.ManagePersonService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.AttendanceType
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
/**
 * unit test for ExternalReceivedTransferredPerson controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ExternalReceivedTransferredPerson])
@Build([ExternalReceivedTransferredPerson,JoinedFirmOperationDocument, Firm])
@TestFor(ExternalReceivedTransferredPersonController)
class ExternalReceivedTransferredPersonControllerSpec extends CommonUnitSpec {


    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    ManagePersonService managePersonService = mockService(ManagePersonService)
    ManageLocationService manageLocationService = mockService(ManageLocationService)


    def setupSpec() {
        domain_class = ExternalReceivedTransferredPerson
        service_domain = ExternalReceivedTransferredPersonService
        entity_name = "externalReceivedTransferredPerson"
        required_properties = PCPUtils.getRequiredFields(ExternalReceivedTransferredPerson)
        filtered_parameters = ["personId"];
        autocomplete_property = "personId"
        exclude_actions = ["list","create","delete"]
        primary_key_values = ["encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    def setup(){

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
        }

        if(!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }

        if(!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            serviceInstance.organizationService.proxyFactoryService = proxyFactoryService
        }

        if(!controller.managePersonService) {
            controller.managePersonService = managePersonService
            managePersonService.manageLocationService = manageLocationService
        }


        sharedService.grailsApplication = grailsApplication

        if(!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.grailsApplication = grailsApplication
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }

    }

    def "test_override_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.EXTERNAL_RECEIVED_TRANSFERRED_PERSON
        model.referenceObject == ExternalReceivedTransferredPerson.name
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

    def "test_getPerson_failed_duplicated_received"() {
        setup:
        ExternalReceivedTransferredPerson.build(personId: 100L)
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
     * @goal test getCreateNewExternalReceived action.
     * @expectedResult response with known model.
     */
    def "test_getCreateNewExternalReceived"() {

        setup:
        println("************************test_getCreateNewExternalReceived********************************")
        when:
        controller.params["personId"] = 1750L
        controller.createNewExternalReceived()

        then:
        model != null
        model != [:]
        model.externalReceivedTransferredPerson.personId == 1750L
        println("test getCreateNewExternalReceived done with model: ${model}")
    }

    /**
     * @goal test getCreateNewExternalReceived action.
     * @expectedResult response with known model.
     */
    def "test_getCreateNewExternalReceived_not_found"() {

        setup:
        println("************************test_getCreateNewExternalReceived_not_found********************************")
        when:
        controller.params["personId"] = null
        controller.createNewExternalReceived()

        then:
        println("model: ${model}")
        response.status == HttpStatus.NOT_FOUND.value()
        println("test getCreateNewExternalReceived done with response: ${response.status}")
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


    /**
     * @goal test delete action.
     * @expectedResult request with params and response with success deleted result.
     */
    def "test_override_success_virtual_delete"() {

        setup:
        println("************************test_success_virtual_delete********************************")
        Firm firm = Firm.build()
        def testInstance = ExternalReceivedTransferredPerson.build(firm: firm)
        def previousCount = domain_class.count()
        def searchMap = controller?.params?.clone()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance, true)))
            }
        }

        when:
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    controller.params["${key}"] = getPropertyValue(key, testInstance, true)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    if (hashing_entity && hashing_entity == key) {
                        propertyId = "encodedId"
                    }
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                controller.params["${key}"] = getPropertyValue(key, testInstance, true)
            }
        }



        PCPSessionUtils.setValue("firmId",firm.id)


        controller.delete()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))
        newCount == previousCount
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        response.redirectedUrl == "/${entity_name}/list"
        println("test virtual delete success and new count is ${newCount}")
    }




    /**
     * @goal test delete action in virtual way.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "test_fail_virtual_delete"() {

        setup:
        if(is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        }else{
            Assume.assumeFalse(true)
        }
        println("************************test_fail_virtual_delete********************************")
        saveEntity()

        when:
        request.method = 'POST'
        controller.delete()

        then:
        response.redirectedUrl == "/${entity_name}/list"
        flash.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message")))
        println("test virtual delete fail done")
    }


    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "test_override_fail_virtual_delete_ajax"() {

        setup:
        if(is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        }else{
            Assume.assumeFalse(true)
        }
        println("************************test_fail_virtual_delete_ajax********************************")
        saveEntity()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.delete()

        then:
        response.json.success == false
        response.json.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message"))).toString()
        println("test virtual delete ajax fail done")
    }

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request with params and response with success deleted result.
     */
    def "test_override_success_virtual_delete_ajax "() {

        setup:
        if(is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        }else{
            Assume.assumeFalse(true)
        }
        println("************************test_success_virtual_delete_ajax********************************")
        saveEntity()
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        def searchMap = controller?.params?.clone()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                if(hashing_entity && hashing_entity == key){
                    propertyId = "encodedId"
                }
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                sendParams(key,testInstance,searchMap,true)
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if(!with_key_join_table){
                    sendParams(key,testInstance,controller.params,true)
                }else{
                    def propertyId = join_table_ids.get(key) ?: "id"
                    if(hashing_entity && hashing_entity == key){
                        propertyId = "encodedId"
                    }
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            }else{
                sendParams(key,testInstance,controller.params,true)
            }
        }
        controller.delete()

        then:
        def newCount = domain_class.count()
        def deletedInstance
        if(with_hashing_flag == true) {
            deletedInstance = serviceInstance.search(searchMap,true)[0]
        }else{
            deletedInstance = serviceInstance.search(searchMap)[0]
        }
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message"))).toString()
        newCount == previousCount
        println("test virtual delete ajax success and new count is ${newCount}")
    }
}