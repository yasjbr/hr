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
import org.junit.Assume
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for SuspensionListEmployeeNote controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionListEmployeeNote])
@Build([SuspensionListEmployeeNote])
@TestFor(SuspensionListEmployeeNoteController)
class SuspensionListEmployeeNoteControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = SuspensionListEmployeeNote
        service_domain = SuspensionListEmployeeNoteService
        entity_name = "suspensionListEmployeeNote"
        required_properties = PCPUtils.getRequiredFields(SuspensionListEmployeeNote)
        required_properties << "note"
        filtered_parameters = ["id"]
        primary_key_values = ['id', 'encodedId']
        exclude_actions = ["autocomplete", "edit", "update", "save"]
    }

    /**
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_save"() {

        setup:
        Assume.assumeFalse(exclude_actions.contains("save"))
        println("************************test_success_save********************************")
        saveEntity()
        def previousCount = domain_class.count()
        Map map = saveEntityToMap();
        def instanceToSave = fillEntity()
        when:

        params["note"] = "suspensionListEmployeeNote"
        params["orderNo"] = 124
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
        controller.save(params)

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
        Assume.assumeFalse(exclude_actions.contains("save"))
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
        Assume.assumeFalse(exclude_actions.contains("save"))
        println("************************test_success_save_ajax********************************")
        saveEntity()
        def instanceToSave = fillEntity()
        def previousCount = domain_class.count()
        when:
        params["note"] = "suspensionListEmployeeNote"
        params["orderNo"] = 124


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
     * @goal test save action.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_save"() {
        setup:
        Assume.assumeFalse(exclude_actions.contains("save"))
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
}