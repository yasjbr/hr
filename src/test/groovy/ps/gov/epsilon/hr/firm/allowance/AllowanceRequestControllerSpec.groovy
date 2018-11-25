package ps.gov.epsilon.hr.firm.allowance

import grails.buildtestdata.DomainInstanceBuilder
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import grails.test.mixin.services.ServiceUnitTestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin
import guiplugin.AlertTagLib
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Assume
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Department
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson
import ps.gov.epsilon.hr.firm.lookups.MilitaryRank
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeService
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.test.utils.TestDataObject

/**
 * unit test for AllowanceRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([AllowanceRequest])
@Build([AllowanceRequest, Employee, JoinedFirmOperationDocument, EmploymentRecord, EmployeePromotion, MilitaryRank, Department, LoanRequestRelatedPerson])
@TestFor(AllowanceRequestController)
class AllowanceRequestControllerSpec extends CommonUnitSpec {

    PersonService personService = mockService(PersonService)
    EmployeeService employeeService = mockService(EmployeeService)
    GovernorateService governorateService = mockService(GovernorateService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    def setupSpec() {
        domain_class = AllowanceRequest
        service_domain = AllowanceRequestService
        entity_name = "allowanceRequest"
        required_properties = PCPUtils.getRequiredFields(AllowanceRequest)
        filtered_parameters = ["id"];
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ["list", "autocomplete","delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
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

        if (!employeeService) {
            serviceInstance.employeeService = employeeService
        }

        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
    }

    /**
     * @goal test list action.
     * @expectedResult known response redirect with attachment model.
     */
    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.ALLOWANCE_REQUEST
        model.referenceObject == AllowanceRequest.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test getEmployee action.
     * @expectedResult respone employee id
     */
    def "test_getEmployee_success"() {
        setup:
        println("************************test_getEmployee_success********************************")
        when:
        controller.params["employeeId"] = 1L
        controller.params["allowanceType.id"] = 1L
        controller.getEmployee()

        then:
        response.json.employeeId == 1L
        response.json.success == true
        println("test get employee success done with employeeId: ${response.json.employeeId} ")
    }

    /**
     * @goal test getEmployee action.
     * @expectedResult not found
     */
    def "test_getEmployee_failed"() {
        setup:
        println("************************test_getEmployee_failed********************************")
        when:
        controller.params["id"] = null
        controller.getEmployee()

        then:
        response.json.success == false
        response.json.employeeId == null
        println("test get employee failed done with message: ${response.json.message}")
    }

    /**
     * @goal test createNewAllowanceRequest action.
     * @expectedResult response with vacation request instance
     */
    def "test_createNewAllowanceRequest"() {

        setup:
        println("************************test_createNewAllowanceRequest********************************")
        when:
        controller.params["employeeId"] = 1L
        controller.params["allowanceTypeId"] = 1L

        controller.createNewAllowanceRequest()

        then:
        model != null
        model != [:]
        model.allowanceRequest != null
        println("test create new allowance request done with model: ${model.allowanceRequest}")
    }

    /**
     * @goal test createNewAllowanceRequest action.
     * @expectedResult not found
     */
    def "test_createNewAllowanceRequest_not_found"() {
        setup:
        println("************************test_createNewAllowanceRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.createNewAllowanceRequest()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test test create new allowance request not found done with response: ${response.status}")
    }

    /**
     * @goal test delete action.
     * @expectedResult request with params and response with success deleted result.
     */
    def "test_override_success_virtual_delete"() {

        setup:
        println("************************test_success_virtual_delete********************************")
        Firm firm = Firm.build()
        def testInstance = AllowanceRequest.build(firm: firm)
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