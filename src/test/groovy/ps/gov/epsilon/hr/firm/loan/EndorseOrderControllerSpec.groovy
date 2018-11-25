package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for EndorseOrder controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([EndorseOrder])
@Build([EndorseOrder])
@TestFor(EndorseOrderController)
class EndorseOrderControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = EndorseOrder
        service_domain = EndorseOrderService
        entity_name = "endorseOrder"
        List requiredProperties = PCPUtils.getRequiredFields(EndorseOrder)
        requiredProperties << "orderNo"
        required_properties = requiredProperties
        filtered_parameters = ["orderNo"];
        autocomplete_property = "orderNo"
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["autocomplete","create","save","update","delete"]
        primary_key_values = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
    }


    def "test_create"() {
        setup:
        println("************************test_create********************************")

        when:
        params["loanNominatedEmployeeEncodedId"] = "abcd"
        controller.create()
        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property,model?."${entity_name}") == entity_name + "_" + property
        }
        println("test_create done with initialized ${entity_name} : ${model."${entity_name}"}")
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
        include_save_properties.each{ TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each{property ->
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
        saveEntity()
        def previousCount = domain_class.count()
        def instanceToSave = fillEntity()
        when:
        request.method = 'POST'

        required_properties.each { String property ->
            sendParams(property,instanceToSave,controller.params)
        }

        if(include_save_properties?.size() > 0){
            previousCount = domain_class.count()
        }
        counter++;
        controller.save()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/loanNominatedEmployee/list"
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
        controller.save()

        then:
        def errorList = []
        required_properties.each { String property ->
            if(!exclude_save_properties.contains(property)){
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each{
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
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property,instanceToSave,controller.params)
        }

        controller.save()

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
            controller.params["${key}"] = getPropertyValue(key,testInstance,true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }

        controller.update()

        then:
        view == 'edit'
        required_properties.each { String property ->
            getPropertyValue(property,model?."${entity_name}") == null
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
            idValues << [name: key, value: (getPropertyValue(key,testInstance,true))]
        }
        def previousCount = domain_class.count()
        when:
        request.method = 'POST'
        idValues.each { Map map ->
            controller.params["${map?.name}"] = map?.value
        }
        required_properties.each { String property ->
            sendParams(property,instanceToSave,controller.params)
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
            getPropertyValue(property,updateInstance) == entity_name + "_" + property + "_" + counter
        }
        response.redirectedUrl == "/loanNominatedEmployee/list"
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
            controller.params["${key}"] = getPropertyValue(key,testInstance,true)
        }
        required_properties.each { String property ->
            controller.params["${property}"] = null
        }
        controller.update()

        then:
        def errorList = []
        required_properties.each { String property ->
            if(!exclude_save_properties.contains(property)) {
                errorList << "Property [${property}] of class [class ${domain_class.name}] cannot be null"
            }
        }
        response.json.success == false
        errorList.each{
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
            idValues << [name: key, value: (getPropertyValue(key,testInstance,true))]
        }
        def previousCount = domain_class.count()

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        idValues.each { Map map ->
            controller.params["${map?.name}"] = map?.value
        }
        required_properties.each { String property ->
            sendParams(property,instanceToSave,controller.params)
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
            getPropertyValue(property,updateInstance) == entity_name + "_" + property + "_" + counter
        }
        println("test_update ajax success and new count is ${newCount}")
    }

}