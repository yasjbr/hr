package ps.gov.epsilon.hr.firm

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
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.lookups.ContactMethodService
import ps.police.pcore.v2.entity.lookups.ContactTypeService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for DepartmentContactInfo controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DepartmentContactInfo])
@Build([DepartmentContactInfo])
@TestFor(DepartmentContactInfoController)
class DepartmentContactInfoControllerSpec extends CommonUnitSpec {

    ContactTypeService contactTypeService = mockService(ContactTypeService)
    ContactMethodService contactMethodService = mockService(ContactMethodService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)

    def setupSpec() {
        domain_class = DepartmentContactInfo
        service_domain = DepartmentContactInfoService
        entity_name = "departmentContactInfo"
        required_properties = PCPUtils.getRequiredFields(DepartmentContactInfo)
        filtered_parameters = ["id"];
        autocomplete_property = "value"
        primary_key_values = ["encodedId", "id"]
        exclude_actions=["delete","save","update"]
        is_virtual_delete=true
    }

    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.contactTypeService) {
            serviceInstance.contactTypeService = contactTypeService
            serviceInstance.contactTypeService.proxyFactoryService = proxyFactoryService
        }
        if (!serviceInstance.contactMethodService) {
            serviceInstance.contactMethodService = contactMethodService
            serviceInstance.contactMethodService.proxyFactoryService = proxyFactoryService
        }
    }

    /**
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save"() {

        setup:
        println("************************test_success_save********************************")
        saveEntity()
        def previousCount = domain_class.count()
        Map map = saveEntityToMap();
        def instanceToSave = fillEntity()
        when:
        request.method = 'POST'

        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value != null) {
                controller.params[property] = value
            } else if (is_join_table) {
                def propertyId = join_table_ids.get(property) ?: "id"
                if (!propertyId) propertyId = "id"
                if (hashing_entity && hashing_entity == property) {
                    propertyId = "encodedId"
                }
                controller.params[(property + ".${propertyId}")] = map["${property}"]?."${propertyId}"
            } else {
                sendParams(property, instanceToSave, controller.params)
            }
        }
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = fillEntity(object)
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                def val = getPropertyValue(property, objectParams)
                controller.params[object.paramName + "." + property] = val
            }
            objectParams = null
        }
        if (include_save_properties?.size() > 0) {
            previousCount = domain_class.count()
        }
        counter++;
        params["firm.id"] = PCPSessionUtils.getValue("firmId")
        controller.save()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test save success and new count is ${newCount}")
    }

    /**
     * @goal test save action.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save"() {
        setup:
        println("************************test_fail_save********************************")

        when:
        request.method = 'POST'
        include_save_properties.each { TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = null
            }
        }
        controller.save()

        then:
        view == 'create'
        required_properties.each { String property ->
            model?."${entity_name}"?."${property}" == null
        }
        model?."${entity_name}"?.errors?.allErrors?.size() >= 1
        println("test save fail with: ${model} and error size: ${model?."${entity_name}"?.errors?.allErrors?.size()}")
    }

    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save_ajax"() {
        setup:
        println("************************test_fail_save_ajax********************************")
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        include_save_properties.each { TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = null
            }
        }
        controller.save()

        then:
        def errorList = []
        required_properties.each { String property ->
            if (!exclude_save_properties.contains(property)) {
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each {
            response.json.message.contains(it)
        }
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test save ajax fail with error size: ${response.json.errorList.size()}")
    }

    /**
     * @goal test save action with ajax request.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save_ajax"() {

        setup:
        println("************************test_success_save_ajax********************************")
        saveEntity()
        def instanceToSave = fillEntity()
        def previousCount = domain_class.count()
        when:
        Map map = saveEntityToMap();
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value) {
                controller.params[property] = value
            } else if (is_join_table) {
                def propertyId = join_table_ids.get(property) ?: "id"
                if (!propertyId) propertyId = "id"
                if (hashing_entity && hashing_entity == property) {
                    propertyId = "encodedId"
                }
                controller.params[(property + ".${propertyId}")] = map["${property}"]?."${propertyId}"
            } else {
                sendParams(property, instanceToSave, controller.params)
            }
        }
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = fillEntity(object)
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = getPropertyValue(property, objectParams)
            }
            objectParams = null
        }
        if (include_save_properties?.size() > 0) {
            previousCount = domain_class.count()
        }
        controller.save()

        then:
        def newCount = domain_class.count()
        response.json.success == true

        println "response.json.message:${response.json.message}"
        println "response.json:${response.json}"
        response.json.message == "default.created.message"
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount != newCount
        println("test save ajax success and new count is ${newCount}")
        counter++;
    }

    /**
     * @goal test update action.
     * @expectedResult request with params and response with known model contains errors.
     */
    def "test_fail_update"() {

        setup:
        println("************************test_fail_update********************************")
        def testInstance = saveEntity()

        when:
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, testInstance, true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }
        include_save_properties.each { TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = null
            }
        }
        controller.update()

        then:
        view == 'edit'
        required_properties.each { String property ->
            getPropertyValue(property, model?."${entity_name}") == null
        }
        model."${entity_name}".errors.allErrors.size() >= 1
        println("test_update fail with: ${model} and error size: ${model."${entity_name}".errors.allErrors.size()}")
    }

    /**
     * @goal test update action.
     * @expectedResult request with params and response with known model not contains any errors.
     */
    def "test_success_update"() {

        setup:
        println("************************test_success_update********************************")
        def testInstance = saveEntity()
        def instanceToSave = fillEntity()
        List idValues = []
        primary_key_values.each { key ->
            idValues << [name: key, value: (getPropertyValue(key, testInstance, true))]
        }
        def previousCount = domain_class.count()
        when:
        request.method = 'POST'
        idValues.each { Map map ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(map?.name)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value) {
                controller.params["${map?.name}"] = value
            } else {
                controller.params["${map?.name}"] = map?.value
            }
        }
        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value) {
                controller.params[property] = value
            } else if (!primary_key_values.contains(property)) {
                sendParams(property, instanceToSave, controller.params)
            }
        }
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = fillEntity(object)
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = getPropertyValue(property, objectParams)
            }
            objectParams = null
        }
        if (include_save_properties?.size() > 0) {
            previousCount = domain_class.count()
        }
        controller.update()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.updated.message")))
        previousCount == newCount
        def updateInstance = domain_class.find {
            return idValues.collect { it.name == it.value }
        }
        required_properties.each { String property ->
            getPropertyValue(property, updateInstance) == entity_name + "_" + property + "_" + counter
        }
        response.redirectedUrl == "/${entity_name}/list"
        println("test_update success and new count is ${newCount}")
    }

    /**
     * @goal test update action with ajax request.
     * @expectedResult request with params and response with known model contains errors.
     */
    def "test_fail_update_ajax"() {

        setup:
        println("************************test_fail_update_ajax********************************")
        def testInstance = saveEntity()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key, testInstance, true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }
        include_save_properties.each { TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = null
            }
        }
        controller.update()

        then:
        def errorList = []
        required_properties.each { String property ->
            if (!exclude_save_properties.contains(property)) {
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each {
            response.json.message.contains(it)
        }
        response.json.data == null
        response.json.errorList.size() >= 1
        println("test_update ajax fail with error size: ${response.json.errorList.size()}")
    }

    /**
     * @goal test update action with ajax request.
     * @expectedResult request with params and response with known model not contains any errors.
     */
    def "test_success_update_ajax"() {

        setup:
        println("************************test_success_update_ajax********************************")
        def testInstance = saveEntity()
        def instanceToSave = fillEntity()
        List idValues = []
        primary_key_values.each { key ->
            idValues << [name: key, value: (getPropertyValue(key, testInstance, true))]
        }
        def previousCount = domain_class.count()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        idValues.each { Map map ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(map?.name)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value) {
                controller.params["${map?.name}"] = value
            } else {
                controller.params["${map?.name}"] = map?.value
            }
        }
        required_properties.each { String property ->
            def value
            if (table_data && table_data?.data) {
                value = table_data?.data?.get(property)
                if (value instanceof TestDataObject) {
                    if (isEmbeddedClass(value?.domain)) {
                        value = saveEntity(value)
                    } else {
                        value = saveEntity(value)?.id
                    }
                }
            }
            if (value) {
                controller.params[property] = value
            } else if (!primary_key_values.contains(property)) {
                sendParams(property, instanceToSave, controller.params)
            }
        }
        def objectParams
        include_save_properties.each { TestDataObject object ->
            objectParams = fillEntity(object)
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = getPropertyValue(property, objectParams)
            }
            objectParams = null
        }
        if (include_save_properties?.size() > 0) {
            previousCount = domain_class.count()
        }
        controller.update()

        then:
        def newCount = domain_class.count()
        response.json.success == true

        response.json.message ==  "default.updated.message"
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount == newCount
        def updateInstance = domain_class.find {
            return idValues.collect { it.name == it.value }
        }
        required_properties.each { String property ->
            getPropertyValue(property, updateInstance) == entity_name + "_" + property + "_" + counter
        }
        println("test_update ajax success and new count is ${newCount}")
    }

}