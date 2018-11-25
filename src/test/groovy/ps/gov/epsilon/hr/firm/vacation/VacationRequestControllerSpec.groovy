package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.mixin.Build
import grails.core.DefaultGrailsClass
import grails.core.GrailsApplication
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
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationType
import ps.gov.epsilon.hr.firm.vacation.lookup.VacationTypeService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.pcore.v2.entity.location.lookups.GovernorateService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.person.ContactInfoService
import ps.police.pcore.v2.entity.person.PersonMaritalStatusService
import ps.police.pcore.v2.entity.person.PersonService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

import java.time.ZonedDateTime

/**
 * unit test for vacationRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([VacationRequest])
@Build([VacationRequest, Firm, VacationList, JoinedFirmOperationDocument, VacationListEmployee, VacationType, VacationConfiguration, EmployeeVacationBalance, Employee, JoinedFirmOperationDocument, EmploymentRecord, EmployeePromotion, MilitaryRank, Department, LoanRequestRelatedPerson])
@TestFor(VacationRequestController)
class VacationRequestControllerSpec extends CommonUnitSpec {

    EmployeeService employeeService = mockService(EmployeeService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    ContactInfoService contactInfoService = mockService(ContactInfoService)
    PersonService personService = mockService(PersonService)
    VacationTypeService vacationTypeService = mockService(VacationTypeService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)
    EmployeeVacationBalanceService employeeVacationBalanceService = mockService(EmployeeVacationBalanceService)
    @Shared
    GrailsApplication grailsApplication


    def setupSpec() {
        domain_class = VacationRequest
        service_domain = VacationRequestService
        entity_name = "vacationRequest"
        required_properties = PCPUtils.getRequiredFields(VacationRequest)
        filtered_parameters = ["id"]
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        exclude_actions = ["delete", "list", 'save', 'saveAll', 'update']

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_, _) >> new DefaultGrailsClass(VacationRequest?.class)
        }

    }

    def setup() {

        sharedService.grailsApplication = grailsApplication

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        params.currentBalance = 10

        if (!employeeService) {
            serviceInstance.employeeService = employeeService
        }

        if (!vacationTypeService) {
            serviceInstance.vacationTypeService = vacationTypeService
        }
        if (!employeeVacationBalanceService) {
            serviceInstance.employeeVacationBalanceService = employeeVacationBalanceService
        }

        if (!serviceInstance.contactInfoService) {
            serviceInstance.contactInfoService = contactInfoService
            serviceInstance.contactInfoService.proxyFactoryService = proxyFactoryService
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
        model.operationType == EnumOperation.VACATION_REQUEST
        model.referenceObject == VacationRequest.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test delete action.
     * @expectedResult request with params and response with success deleted result.
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

    /**
     * @goal test delete action with ajax request.
     * @expectedResult request with params and response with success deleted result.
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

    /**
     * @goal test selectEmployee action.
     * @expectedResult response employee id & vacation type id
     */
    def "test_selectEmployee_success"() {
        setup:
        println("************************test_selectEmployee_success********************************")
        Firm firm = Firm.build()
        Employee employee = Employee.build(firm: firm)
        VacationType vacationType = VacationType.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.params["employee.id"] = employee?.id
        controller.params["vacationType.id"] = vacationType?.id
        controller.selectEmployee()

        then:
        response.json.success == false
        println("test selectEmployee success done with : ${response.json.message}")

    }

    /**
     * @goal test selectEmployee action.
     * @expectedResult not found
     */
    def "test_selectEmployee_failed"() {
        setup:
        println("************************test_selectEmployee_failed********************************")
        when:
        controller.params["employee.id"] = null
        controller.params["vacationType.id"] = null
        controller.selectEmployee()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test selectEmployee failed done with message: ${response.status}")
    }

    /**
     * @goal test createNewVacationRequest action.
     * @expectedResult response with vacation request instance
     */
    def "test_createNewVacationRequest"() {

        setup:
        println("************************test_createNewVacationRequest********************************")
        Firm firm = Firm.build()
        Employee employee = Employee.build(firm: firm, personId: 1055206)
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        controller.params["employee.id"] = employee?.id
        controller.params["vacationType.id"] = 1L
        controller.createNewVacationRequest()

        then:

//        model.vacationRequest != null
        println("test createNewVacationRequest done with model: ${model}")
    }

    /**
     * @goal test createNewVacationRequest action.
     * @expectedResult not found
     */
    def "test_createNewVacationRequest_not_found"() {
        setup:
        println("************************test_createNewVacationRequest_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.params["vacationType.id"] = null
        controller.createNewVacationRequest()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test test createNewVacationRequest not found done with response: ${response.status}")
    }

    /**
     * @goal test extensionRequestList action.
     * @expectedResult return id
     */
    def "test_extensionRequestList"() {
        setup:
        println("************************test_extensionRequestList********************************")
        when:
        controller.params["id"] = 1L
        controller.extensionRequestList()

        then:
        println("test extension Request List success done with id: ${response}")
    }

/**
 * @goal test stopRequestCreate action.
 * @expectedResult response with extension request instance
 */
    def "test_extensionRequestCreate"() {

        setup:
        println("************************test_extensionRequestCreate********************************")
        when:
        controller.params["id"] = 1L
        controller.extensionRequestCreate()

        then:
        model != null
        model != [:]
        model.vacationExtensionRequest != null
        println("test extensionRequestCreate done with model: ${model.vacationExtensionRequest}")
    }

    /**
     * @goal test extensionRequestCreate action.
     * @expectedResult not found
     */
    def "test_extensionRequestCreate_not_found"() {
        setup:
        println("************************test_extensionRequestCreate_not_found********************************")
        when:
        controller.params["id"] = null
        controller.extensionRequestCreate()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test extension Request Create not found done with model: ${model.vacationExtensionRequest}")
    }

    /**
     * @goal test stopRequestCreate action.
     * @expectedResult response with stopRequest instance
     */
    def "test_stopRequestCreate"() {

        setup:
        println("************************test_stopRequestCreate********************************")
        when:
        controller.params["id"] = 1L
        controller.stopRequestCreate()

        then:
        model != null
        model != [:]
        model.stopVacation != null
        println("test  stop Request Create done with model: ${model.stopVacation}")
    }

    /**
     * @goal test stopRequestCreate action.
     * @expectedResult not found
     */
    def "test_stopRequestCreate_not_found"() {
        setup:
        println("************************test_extensionRequestCreate_not_found********************************")
        when:
        controller.params["id"] = null
        controller.stopRequestCreate()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test stop Request Create not found done with model: ${model.vacationExtensionRequest}")
    }

    /**
     * @goal test saveAll action.
     * @expectedResult response with known model contains errors.
     */
    def "test_fail_saveAll"() {
        setup:
        println("************************test_fail_saveAll********************************")

        when:
        request.method = 'POST'
        include_save_properties.each { TestDataObject object ->
            controller.params[object.paramName] = null
            object.requiredProperties.each { property ->
                controller.params[object.paramName + "." + property] = null
            }
        }

        controller.saveAll()

        then:
        required_properties.each { String property ->
            model?."${entity_name}"?."${property}" == null
        }
        model == [:]
        println("test save all fail with: ${model} and error size: ${model?."${entity_name}"?.errors?.allErrors?.size()}")
    }

    /**
     * @goal test saveAll action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_success_saveAll"() {

        setup:
        println("************************test_success_saveAll********************************")
        saveEntity()
        def previousCount = domain_class.count()
        Employee employee = Employee.build()
        Employee employee1 = Employee.build()
        Employee employee2 = Employee.build()
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
        controller.params["employeeIdList"] = [employee?.id, employee1?.id, employee2?.id]
        controller.params["fromDate"] = "09/11/2017"
        controller.params["toDate"] = "13/11/2017"
        controller.saveAll()

        then:
        def newCount = domain_class.count()
        flash.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message")))
        previousCount != newCount
        response.redirectedUrl == "/${entity_name}/list"
        println("test save all success and new count is ${newCount}")
    }

    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList"() {

        setup:
        println("************************test_goToList********************************")

        Employee employee = Employee.build()
        VacationRequest vacationRequest = VacationRequest.build(employee: employee)
        VacationList vacationList = VacationList.build()
        VacationListEmployee.build(vacationList: vacationList, vacationRequest: vacationRequest)

        when:
        controller.params["encodedId"] = vacationRequest?.encodedId
        controller.goToList()

        then:
        response.redirectedUrl.toString().contains("/vacationList/manageVacationList?encodedId=")
        println("test goToList done with model: ${model}")
    }

    /**
     * @goal test goToList action.
     * @expectedResult response with known model.
     */
    def "test_goToList_not_found"() {

        setup:
        println("************************test_goToList_not_found********************************")
        when:
        controller.goToList()

        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test goToList done with response: ${response.status}")
    }


}