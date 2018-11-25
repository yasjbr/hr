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
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for ExternalTransferListEmployee controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ExternalTransferListEmployee])
@Build([ExternalTransferListEmployee, ExternalTransferRequest])
@TestFor(ExternalTransferListEmployeeController)
class ExternalTransferListEmployeeControllerSpec extends CommonUnitSpec {

    PersonService personService = mockService(PersonService)
    OrganizationService organizationService = mockService(OrganizationService)
    GovernorateService governorateService = mockService(GovernorateService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)

    def setupSpec() {
        domain_class = ExternalTransferListEmployee
        service_domain = ExternalTransferListEmployeeService
        entity_name = "externalTransferListEmployee"
        required_properties = PCPUtils.getRequiredFields(ExternalTransferListEmployee)
        filtered_parameters = ["id"]
        exclude_actions = ["autocomplete", "delete"]
        primary_key_values = ["encodedId", "id"]


    }

    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            serviceInstance.organizationService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.governorateService) {
            serviceInstance.governorateService = governorateService
            serviceInstance.governorateService.proxyFactoryService = proxyFactoryService
        }

    }

    /**
     * @goal test delete action.
     * @expectedResult request with params and response with success deleted result.
     */
    def "test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        saveEntity()
        def testInstance = saveEntity()
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
        controller.delete()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))
        newCount < previousCount
        def deletedInstance
        if (with_hashing_flag == true) {
            deletedInstance = serviceInstance.search(searchMap, true)[0]
        } else {
            deletedInstance = serviceInstance.search(searchMap)[0]
        }
        deletedInstance == null
        response.redirectedUrl == "/${entity_name}/list"
        println("test delete success and new count is ${newCount}")
    }

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request with params and response with success deleted result.
     */
    def "test_success_delete_ajax "() {

        setup:
        println("************************test_success_delete_ajax********************************")
        saveEntity()
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        def searchMap = controller?.params?.clone()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                if (hashing_entity && hashing_entity == key) {
                    propertyId = "encodedId"
                }
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                sendParams(key, testInstance, searchMap, true)
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    sendParams(key, testInstance, controller.params, true)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    if (hashing_entity && hashing_entity == key) {
                        propertyId = "encodedId"
                    }
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                sendParams(key, testInstance, controller.params, true)
            }
        }
        controller.delete()

        then:
        def newCount = domain_class.count()
        def deletedInstance
        if (with_hashing_flag == true) {
            deletedInstance = serviceInstance.search(searchMap, true)[0]
        } else {
            deletedInstance = serviceInstance.search(searchMap)[0]
        }
        deletedInstance == null
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message"))).toString()
        newCount < previousCount
        println("test delete ajax success and new count is ${newCount}")
    }

    /**
     * @goal test delete action.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        ExternalTransferListEmployee testInstance = saveEntity()
        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build()
        externalTransferRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
        testInstance.externalTransferRequest = externalTransferRequest
        when:
        request.method = 'POST'
        controller.params["id"] = testInstance?.id
        println "testInstance:${testInstance?.id}"
        controller.delete()

        then:
//        response.redirectedUrl == "/${entity_name}/list"
        flash.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message")))
        println("test_delete fail done")
    }

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "test_fail_delete_ajax"() {

        setup:
        println("************************test_fail_delete_ajax********************************")
        ExternalTransferListEmployee testInstance = saveEntity()
        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build()
        externalTransferRequest.requestStatus = EnumRequestStatus.ADD_TO_LIST
        testInstance.externalTransferRequest = externalTransferRequest

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.delete()

        then:
        response.json.success == null
        response.json.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message"))).toString()
        println("test_delete ajax fail done")
    }


}