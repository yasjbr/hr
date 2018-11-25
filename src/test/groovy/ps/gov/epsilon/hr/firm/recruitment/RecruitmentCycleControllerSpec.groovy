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
import org.apache.http.HttpStatus
import org.junit.Assume
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.lookups.AttendanceType
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import spock.lang.Shared

/**
 * unit test for RecruitmentCycle controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([RecruitmentCycle])
@Build([RecruitmentCycle, JoinedFirmOperationDocument, Firm])
@TestFor(RecruitmentCycleController)
class RecruitmentCycleControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)

    def setupSpec() {
        domain_class = RecruitmentCycle
        service_domain = RecruitmentCycleService
        entity_name = "recruitmentCycle"
        required_properties = PCPUtils.getRequiredFields(RecruitmentCycle)
        filtered_parameters = ["id"];
        autocomplete_property = "name"
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ['list', 'delete', 'edit']
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
        model.operationType == EnumOperation.RECRUITMENT_CYCLE
        model.referenceObject == RecruitmentCycle.name
        println("test_list done with data : ${model}")
    }


    def "new_test_edit"() {
        when:
        controller.edit()
        then:
        def editInstance = serviceInstance.getInstance(params)
        if (editInstance?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            response editInstance
        } else {
            flash.message == alertTagLib.error(label: (validationTagLib.message(code: "recruitmentCycle.errorEditMessage.label")))

        }
    }

/**
 * @goal test delete action.
 * @expectedResult request without params and response with success delete result.
 */
    def "new_test_success_delete"() {
        setup:
        println("************************test_success_delete********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
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
        PCPSessionUtils.setValue("firmId", firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        if (deletedInstance.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance.trackingInfo.status == GeneralStatus.DELETED
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))

        } else {
            deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.not.deleted.message")))

        }
        def newCount = domain_class.count()
        deletedInstance != null
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
        def testInstance = RecruitmentCycle.build(firm: firm)
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
        PCPSessionUtils.setValue("firmId", firm.id)
        controller.delete()

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        if (deletedInstance.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [EnumRequisitionAnnouncementStatus.NEW, EnumRequisitionAnnouncementStatus.OPEN]) {
            deletedInstance.trackingInfo.status == GeneralStatus.DELETED
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.deleted.message")))
            response.json.success == true
        } else {
            deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.not.deleted.message")))
            response.json.success == false
        }
        def newCount = domain_class.count()
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
        def testInstance = RecruitmentCycle.build(firm: firm)
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
        PCPSessionUtils.setValue("firmId", firm.id)
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
        def testInstance = RecruitmentCycle.build(firm: firm)
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
        PCPSessionUtils.setValue("firmId", firm.id)
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


    def "test_changePhase"() {
        setup:
        println("************************test_changePhase********************************")
        Firm firm = Firm.build()
        def testInstance = RecruitmentCycle.build(firm: firm)
        when:
        params.id = testInstance.id
        PCPSessionUtils.setValue("firmId", firm.id)
        controller.changePhase()
        then:
        def map = serviceInstance.getNextPhase(params)
        if (map.errorType == "notAllowed") {
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "recruitmentCycle.errorNotAllowedMessage.label"))).toString()
        } else if (map.errorType == "success") {
            render(map)
        }
        println("test_changePhase done with data:${testInstance}")
    }

    def "test_manageDepartments"() {
        setup:
        println("************************test_manageDepartments********************************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance.id
        controller.changePhase()
        then:
        def map = serviceInstance.manageDepartmentData(params)
        if (map.errorType == "notAllowed") {
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "recruitmentCycle.departments.notAllowed.manage.label"))).toString()
        } else if (map.errorType == "success") {
            render(template: "/recruitmentCycle/inLine/edit", model: map)
        }
        println("test_manageDepartments done with data:${testInstance}")
    }

    def "test_addJobRequisition"() {
        setup:
        println("************************test_addJobRequisition********************************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance.id
        controller.addJobRequisition()
        then:
        boolean isAdded = serviceInstance.addRecruitmentCycleToJobRequisition(params)
        def json = [:]
        if (isAdded) {
            json.success == true
            json.message == alertTagLib.success(label: (validationTagLib.message(code: "recruitmentCycle.addJobRequisition.message"))).toString()
        } else {
            json.success == false
            flash.message == alertTagLib.success(label: (validationTagLib.message(code: "recruitmentCycle.not.addJobRequisition.message"))).toString()
        }
        println("test_addJobRequisition done with data:${testInstance}")
    }


}