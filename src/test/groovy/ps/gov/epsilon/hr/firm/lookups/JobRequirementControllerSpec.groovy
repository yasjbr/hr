package ps.gov.epsilon.hr.firm.lookups

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
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for JobRequirement controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([JobRequirement])
@Build([JobRequirement, Firm])
@TestFor(JobRequirementController)
class JobRequirementControllerSpec extends CommonUnitSpec {
    def setupSpec() {
        domain_class = JobRequirement
        service_domain = JobRequirementService
        entity_name = "jobRequirement"
        required_properties = PCPUtils.getRequiredFields(JobRequirement)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_key_values = ["encodedId", "id"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["delete"]
    }

/**
 * @goal test delete action.
 * @expectedResult request with params and response with success deleted result.
 */
    def "test_override_success_virtual_delete"() {

        setup:
        println("************************test_success_virtual_delete********************************")
        Firm firm = Firm.build()
        def testInstance = JobRequirement.build(firm: firm)
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



        PCPSessionUtils.setValue("firmId", firm.id)


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
        if (is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        } else {
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
        if (is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        } else {
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
        if (is_virtual_delete) {
            Assume.assumeFalse(exclude_actions.contains("delete"))
        } else {
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
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message"))).toString()
        newCount == previousCount
        println("test virtual delete ajax success and new count is ${newCount}")
    }


}

