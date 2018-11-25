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
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonUnitSpec

/**
 * unit test for VacancyAdvertisements controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([VacancyAdvertisements])
@Build([VacancyAdvertisements, JoinedFirmOperationDocument, JoinedVacancyAdvertisement, Vacancy, Firm])
@TestFor(VacancyAdvertisementsController)
class VacancyAdvertisementsControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)

    def setupSpec() {
        domain_class = VacancyAdvertisements
        service_domain = VacancyAdvertisementsService
        entity_name = "vacancyAdvertisements"
        required_properties = PCPUtils.getRequiredFields(VacancyAdvertisements)
        filtered_parameters = ["id"];
        autocomplete_property = "title"
        primary_key_values = ["encodedId", "id"]
        exclude_actions = ["list", "delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()
        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
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
        model.operationType == EnumOperation.VACANCY_ADVERTISEMENTS
        model.referenceObject == VacancyAdvertisements.name
        println("test_list done with data : ${model}")
    }

/**
 * @goal test delete action.
 * @expectedResult request without params and response with success delete result.
 */
    def "new_test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
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
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
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

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with success delete result.
     */

    def "new_test_success_delete_ajax"() {

        setup:
        println("************************test_success_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
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
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
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

    /**
     * @goal test delete action.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete"() {

        setup:
        println("************************test_fail delete********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
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
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
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

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with failed deleted result.
     */
    def "new_test_fail_delete_ajax"() {

        setup:
        println("************************test_fail_delete_ajax********************************")
        Firm firm = Firm.build()
        def testInstance = VacancyAdvertisements.build(firm: firm)
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
        params.id = testInstance.encodedId
        PCPSessionUtils.setValue("firmId",firm.id)
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


    def "test_addVacancyToVacancyAdvertisements"() {
        setup:
        println("************************test_addVacancyToVacancyAdvertisements********************************")
        def instance = saveEntity()
        when:
        params["vacancy.id"] = 1L
        params.id = instance.id
        controller.addVacancyToVacancyAdvertisements()
        then:
        boolean successAdd = serviceInstance.addVacancyToVacancyAdvertisements(params)
        def json = [:]
        if (successAdd) {
            json.success == true
            json.message == alertTagLib.success(label: (validationTagLib.message(code: "vacancyAdvertisements.successAdd.label"))).toString()
        } else {
            json.success == false
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "vacancyAdvertisements.failAdd.label"))).toString()
        }
        println("test_addVacancyToVacancyAdvertisements done with data:${instance}")
    }


    def "test_deleteVacancyFromVacancyAdvertisements"() {
        setup:
        println("************************test_deleteVacancyFromVacancyAdvertisements********************************")
        def instance = saveEntity()
        when:
        params["vacancy.id"] = 1L
        params.id = instance.id
        controller.deleteVacancyFromVacancyAdvertisements()
        then:
        boolean successDelete = serviceInstance.deleteVacancyFromVacancyAdvertisements(params)
        def json = [:]
        if (successDelete) {
            json.success == true
            json.message == alertTagLib.success(label: (validationTagLib.message(code: "vacancyAdvertisements.successDelete.label"))).toString()
        } else {
            json.success == false
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "vacancyAdvertisements.failDelete.label"))).toString()
        }
        println("test_deleteVacancyFromVacancyAdvertisements done with data:${instance}")
    }
}