package ps.gov.epsilon.hr.firm.absence

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
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
/**
 * unit test for ReturnFromAbsenceRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ReturnFromAbsenceRequest])
@Build([ReturnFromAbsenceRequest])
@TestFor(ReturnFromAbsenceRequestController)
class ReturnFromAbsenceRequestControllerSpec extends CommonUnitSpec {

    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    PersonService personService = mockService(PersonService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    EmployeeService employeeService = mockService(EmployeeService)
    SharedService sharedService = mockService(SharedService)
    GovernorateService governorateService = mockService(GovernorateService)
    OrganizationService organizationService = mockService(OrganizationService)
    PersonMaritalStatusService personMaritalStatusService = mockService(PersonMaritalStatusService)

    def setupSpec() {
        domain_class = ReturnFromAbsenceRequest
        service_domain = ReturnFromAbsenceRequestService
        entity_name = "returnFromAbsenceRequest"
        required_properties = PCPUtils.getRequiredFields(ReturnFromAbsenceRequest)
        filtered_parameters = ["id"];
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ["list", "delete", "create"]
    }

    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if(!serviceInstance.employeeService) {
            serviceInstance.employeeService = employeeService
            serviceInstance.employeeService.personService = personService
            serviceInstance.employeeService.personMaritalStatusService = personMaritalStatusService
            serviceInstance.employeeService.governorateService = governorateService
            serviceInstance.employeeService.organizationService = organizationService
            serviceInstance.employeeService.personService.proxyFactoryService = proxyFactoryService
        }

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
        model.operationType == EnumOperation.RETURN_FROM_ABSENCE_REQUEST
        model.referenceObject == ReturnFromAbsenceRequest.name
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


    /**
     * @goal test selectEmployee action.
     * @expectedResult response with known model.
     */
    def "test_selectEmployee"() {
        setup:
        println("************************test_selectEmployee********************************")
        when:
        controller.params["employeeId"] = 100L
        controller.selectAbsence()
        then:
        response.json.success == true
        response.json.employeeId == 100L
        println("test selectEmployee done with employeeId: ${response.json.employeeId}")
    }


    /**
     * @goal test delete action.
     * @expectedResult request without params and response with success delete result.
     */
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

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with success delete result.
     */
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

    /**
     * @goal test delete action.
     * @expectedResult request without params and response with failed deleted result.
     */
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

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request without params and response with failed deleted result.
     */
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




    /**
     * @goal test createNewRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewRequest"() {
        setup:
        println("************************test_createNewRequest********************************")
        ReturnFromAbsenceRequest returnFromAbsenceRequest = ReturnFromAbsenceRequest.build()
        when:
        controller.params["employeeId"] = returnFromAbsenceRequest?.employee?.id
        controller.createNewRequest()
        then:
        model != null
        model != [:]
        println(model)
        model.returnFromAbsenceRequest.employee.id == returnFromAbsenceRequest?.employee?.id
        println("test createNewRequest done with model: ${model}")
    }

    /**
     * @goal test createNewRequest action.
     * @expectedResult response with known model.
     */
    def "test_createNewRequest_not_found"() {
        setup:
        println("************************test_createNewAbsence_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewRequest()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test createNewRequest done with response: ${response.status}")
    }

}