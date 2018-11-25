package ps.gov.epsilon.hr.firm.request

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
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.lookups.BorderCrossingPointService
import ps.police.pcore.v2.entity.lookups.DocumentTypeService
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils

/**
 * unit test for BordersSecurityCoordination controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([BordersSecurityCoordination])
@Build([BordersSecurityCoordination])
@TestFor(BordersSecurityCoordinationController)
class BordersSecurityCoordinationControllerSpec extends CommonUnitSpec {

    EmployeeService employeeService = mockService(EmployeeService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    ContactInfoService contactInfoService = mockService(ContactInfoService)
    PersonService personService = mockService(PersonService)
    DocumentTypeService documentTypeService = mockService(DocumentTypeService)
    BorderCrossingPointService borderCrossingPointService = mockService(BorderCrossingPointService)


    def setupSpec() {
        domain_class = BordersSecurityCoordination
        service_domain = BordersSecurityCoordinationService
        entity_name = "bordersSecurityCoordination"
        required_properties = PCPUtils.getRequiredFields(BordersSecurityCoordination)
        filtered_parameters = ["id"]
        autocomplete_property = "transientData.borderCrossingPointDTO.descriptionInfo.localName"
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["delete"]
    }


    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.contactInfoService) {
            serviceInstance.contactInfoService = contactInfoService
            serviceInstance.contactInfoService.proxyFactoryService = proxyFactoryService
        }
        if (!serviceInstance.documentTypeService) {
            serviceInstance.documentTypeService = documentTypeService
            serviceInstance.documentTypeService.proxyFactoryService = proxyFactoryService
        }
        if (!serviceInstance.borderCrossingPointService) {
            serviceInstance.borderCrossingPointService = borderCrossingPointService
            serviceInstance.borderCrossingPointService.proxyFactoryService = proxyFactoryService
        }
        if (!employeeService) {
            serviceInstance.employeeService = employeeService
        }
    }


    def "new_test_success_delete"() {

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
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    controller.params["${key}"] = getPropertyValue(key, testInstance)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                controller.params["${key}"] = getPropertyValue(key, testInstance)
            }
        }
        session["firmId"] = testInstance?.firm?.id
        params.id = testInstance.encodedId
        controller.delete()
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        deletedInstance != null
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))
        newCount == previousCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test delete success and new count is ${newCount}")
    }


    def "new_test_success_delete_ajax"() {

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
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                sendParams(key, testInstance, searchMap)
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            if (is_join_table) {
                if (!with_key_join_table) {
                    sendParams(key, testInstance, controller.params)
                } else {
                    def propertyId = join_table_ids.get(key) ?: "id"
                    controller.params[(key + "." + propertyId)] = (testInstance?."${key}"?."${propertyId}")
                }
            } else {
                sendParams(key, testInstance, controller.params)
            }
        }
        session["firmId"] = testInstance?.firm?.id
        params.id = testInstance.encodedId
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message"))).toString()
        newCount == previousCount
        println("test delete ajax success and new count is ${newCount}")
    }


    def "new_test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        def testInstance = saveEntity()
        def searchMap = controller?.params?.clone()
        def previousCount = domain_class.count()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.method = 'POST'
        session["firmId"] = testInstance?.firm?.id
        params.id = testInstance.encodedId
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(params)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        def newCount = domain_class.count()
        response.redirectedUrl == "/${entity_name}/list"
        newCount == previousCount
        flash.message == alertTagLib.error(label: (validationTagLib.message(code: "default.not.deleted.message")))
        println("test_delete fail done")
    }


    def "new_test_fail_delete_ajax"() {

        setup:
        println("************************test_fail_delete_ajax********************************")
        def testInstance = saveEntity()
        def searchMap = controller?.params?.clone()
        def previousCount = domain_class.count()
        searchMap.clear()
        primary_key_values.each { key ->
            if (is_join_table) {
                def propertyId = join_table_ids.get(key) ?: "id"
                searchMap.put((key + "." + propertyId), (testInstance?."${key}"?."${propertyId}"))
            } else {
                searchMap.put(key, (getPropertyValue(key, testInstance)))
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        session["firmId"] = testInstance?.firm?.id
        params.id = testInstance.encodedId
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(params)[0]
        deletedInstance != null
        def newCount = domain_class.count()
        newCount == previousCount
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        flash.message == null
        println("test_delete ajax fail done")
    }


    def "test_selectEmployee_success"() {
        setup:
        println("************************test_selectEmployee_success********************************")
        when:
        controller.params["employeeId"] = 1L
        controller.selectEmployee()

        then:
        response.json.success == true
        response.json.employeeId == 1L
        println("test selectEmployee success done with employeeId: ${response.json.employeeId}")
    }

    def "test_selectEmployee_failed"() {
        setup:
        println("************************test_selectEmployee_failed********************************")
        when:
        controller.params["employeeId"] = null
        controller.selectEmployee()

        then:
        response.json.success == false
        response.json.employeeId == null
        println("test selectEmployee failed done with message: ${response.json.message}")
    }


    def "test_createNewBordersSecurityCoordination"() {

        setup:
        println("************************test_createNewBordersSecurityCoordination********************************")
        when:
        controller.params["employeeId"] = 1L
        controller.createNewBordersSecurityCoordination()

        then:
        model != null
        model != [:]
        model.bordersSecurityCoordination != null
        println("test test createNewBordersSecurityCoordination done with model: ${model.bordersSecurityCoordination}")
    }


    def "test_createNewBordersSecurityCoordination_not_found"() {
        setup:
        println("************************test_createNewBordersSecurityCoordination_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewBordersSecurityCoordination()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test test createNewBordersSecurityCoordination not found done with response: ${response.status}")
    }

}