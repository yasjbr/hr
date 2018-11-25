package ps.gov.epsilon.hr.firm.suspension

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
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

/**
 * unit test for SuspensionExtensionRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionExtensionRequest])
@Build([SuspensionExtensionRequest, EmployeePromotion, EmploymentRecord, Employee, SuspensionRequest])
@TestFor(SuspensionExtensionRequestController)
class SuspensionExtensionRequestControllerSpec extends CommonUnitSpec {

    PersonService personService = mockService(PersonService)
    GovernorateService governorateService = mockService(GovernorateService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)


    def setupSpec() {
        domain_class = SuspensionExtensionRequest
        service_domain = SuspensionExtensionRequestService
        entity_name = "suspensionExtensionRequest"
        required_properties = PCPUtils.getRequiredFields(SuspensionExtensionRequest)
        filtered_parameters = ["id"]
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ["autocomplete", "save", "update", "delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!serviceInstance.personService) {
            serviceInstance.personService = personService
            serviceInstance.personService.proxyFactoryService = proxyFactoryService
        }

        if (!serviceInstance.governorateService) {
            serviceInstance.governorateService = governorateService
            serviceInstance.governorateService.proxyFactoryService = proxyFactoryService
        }
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
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save"() {

        setup:
        println("************************test_success_save********************************")

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion, currentEmploymentRecord: employmentRecord)
        SuspensionRequest suspensionRequest = SuspensionRequest.build(employee: employee,
                fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1L))

        def instanceToSave = SuspensionExtensionRequest.build(employee: employee, suspensionRequest: suspensionRequest,
                fromDate: ZonedDateTime.now().plusYears(1L), toDate: ZonedDateTime.now().plusYears(2L))

        def previousCount = domain_class.count()

        Map map = saveEntityToMap();
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

        controller.save()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test save success and new count is ${newCount}")
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

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion, currentEmploymentRecord: employmentRecord)
        SuspensionRequest suspensionRequest = SuspensionRequest.build(employee: employee,
                fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1L))

        def instanceToSave = SuspensionExtensionRequest.build(employee: employee, suspensionRequest: suspensionRequest,
                fromDate: ZonedDateTime.now().plusYears(1L), toDate: ZonedDateTime.now().plusYears(2L))

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

        controller.save(params)
        then:
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
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

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion, currentEmploymentRecord: employmentRecord)
        SuspensionRequest suspensionRequest = SuspensionRequest.build(employee: employee,
                fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1L))

        def testInstance = SuspensionExtensionRequest.build(employee: employee, suspensionRequest: suspensionRequest,
                fromDate: ZonedDateTime.now().plusYears(1L), toDate: ZonedDateTime.now().plusYears(2L))
        def instanceToSave = testInstance
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

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion, currentEmploymentRecord: employmentRecord)
        SuspensionRequest suspensionRequest = SuspensionRequest.build(employee: employee,
                fromDate: ZonedDateTime.now(), toDate: ZonedDateTime.now().plusYears(1L))

        def testInstance = SuspensionExtensionRequest.build(employee: employee, suspensionRequest: suspensionRequest,
                fromDate: ZonedDateTime.now().plusYears(1L), toDate: ZonedDateTime.now().plusYears(2L))
        def instanceToSave = testInstance
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

        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.updated.message"))).toString()
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