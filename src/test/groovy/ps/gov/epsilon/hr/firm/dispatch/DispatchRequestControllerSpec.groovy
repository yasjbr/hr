package ps.gov.epsilon.hr.firm.dispatch

import grails.buildtestdata.mixin.Build
import grails.converters.JSON
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
import guiplugin.ElementsTagLib
import ps.gov.epsilon.core.location.ManageLocationService
import ps.gov.epsilon.hr.common.ProxyFactoryService
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmployeeStatusHistory
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatus
import ps.gov.epsilon.hr.firm.profile.lookups.EmployeeStatusCategory
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.recruitment.JobRequisition
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.pcore.v2.entity.location.LocationService
import ps.police.pcore.v2.entity.location.lookups.CountryService
import ps.police.pcore.v2.entity.lookups.EducationMajorService
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import spock.lang.Shared

/**
 * unit test for DispatchRequest controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([DispatchRequest])
@Build([DispatchRequest, Employee, JoinedFirmOperationDocument, DispatchList, EmployeePromotion, EmploymentRecord, EmployeeStatusCategory,EmployeeStatus, EmployeeStatusHistory])
@TestFor(DispatchRequestController)
class DispatchRequestControllerSpec extends CommonUnitSpec {

    OrganizationService organizationService = mockService(OrganizationService)
    LocationService locationService = mockService(LocationService)
    EducationMajorService educationMajorService = mockService(EducationMajorService)
    ProxyFactoryService proxyFactoryService = mockService(ProxyFactoryService)
    DispatchListService dispatchListService = mockService(DispatchListService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)
    ManageLocationService manageLocationService = mockService(ManageLocationService)
    CountryService countryService  = mockService(CountryService)
    ElementsTagLib elementsTagLib = mockTagLib(ElementsTagLib)

    @Shared
    GrailsApplication grailsApplication

    def setupSpec() {
        domain_class = DispatchRequest
        service_domain = DispatchRequestService
        entity_name = "dispatchRequest"
        required_properties = PCPUtils.getRequiredFields(DispatchRequest)
        filtered_parameters = ["id"];
        autocomplete_property = "employee.transientData.personDTO.localFullName"
        primary_key_values = ["id", "encodedId"]
        exclude_actions = ["create","delete","save", "list"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(DispatchList?.class)
        }

        //to exclude other data for json response
        JSON.registerObjectMarshaller(DispatchList) {
            def returnArray = [:]
            returnArray['id'] = it.id
            returnArray['version'] = it.version
            returnArray['code'] = it.code
            returnArray['name'] = it.name
            returnArray['manualOutgoingNo'] = it.manualOutgoingNo
            returnArray['manualIncomeNo'] = it.manualIncomeNo
            returnArray['currentStatus'] = it.currentStatus
            returnArray['trackingInfo'] = it.trackingInfo
            returnArray['receivingParty'] = it.receivingParty
            returnArray['orderNo'] = it.orderNo
            returnArray['firm'] = it.firm
            return returnArray
        }
    }


    def setup() {
        grails.buildtestdata.TestDataConfigurationHolder.reset()
        if (!serviceInstance.organizationService) {
            serviceInstance.organizationService = organizationService
            serviceInstance.organizationService.proxyFactoryService = proxyFactoryService
        }
        if (!serviceInstance.dispatchListService) {
            serviceInstance.dispatchListService = dispatchListService
        }
        if (!serviceInstance.locationService) {
            serviceInstance.locationService = locationService
            serviceInstance.locationService.proxyFactoryService = proxyFactoryService
        }

        if(!serviceInstance.manageLocationService) {
            serviceInstance.manageLocationService = manageLocationService
            serviceInstance.manageLocationService.countryService = countryService
            serviceInstance.manageLocationService.locationService = locationService
        }

        if (!serviceInstance.educationMajorService) {
            serviceInstance.educationMajorService = educationMajorService
            serviceInstance.educationMajorService.proxyFactoryService = proxyFactoryService
        }
        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }

        sharedService.grailsApplication = grailsApplication
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


    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()
        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.DISPATCH
        model.referenceObject == DispatchRequest.name
        println("test_list done with data : ${model}")
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
        testInstance.requestStatus = EnumRequestStatus.CREATED
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
        testInstance.requestStatus = EnumRequestStatus.CREATED
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
     * @goal test save action.
     * @expectedResult response with known model contains errors.
     */
    def "test_override_fail_save"() {
        setup:
        println("************************test_fail_save********************************")
        when:
        request.method = 'POST'
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
    def "test_override_success_save"() {
        setup:
        println("************************test_success_save********************************")
        def instanceToSave = fillEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee
        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }
        def previousCount = domain_class.count()
        when:
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
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
    def "test_override_fail_save_ajax"() {
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
    def "test_override_success_save_ajax"() {

        setup:
        println("************************test_success_save_ajax********************************")
        def instanceToSave = fillEntity()

        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee
        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }
        def previousCount = domain_class.count()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
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
     * @goal test save action.
     * @expectedResult response with known model not contains any errors.
     */
    def "test_save_with_location"() {
        setup:
        println("************************test_save_with_location********************************")
        def instanceToSave = fillEntity()
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        instanceToSave.employee = employee
        session_parameters.each {String key,String value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instanceToSave))
        }
        def previousCount = domain_class.count()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        required_properties.each { String property ->
            sendParams(property, instanceToSave, controller.params)
        }
        //set location info with France country
        controller.params["location.country.id"] = "3" //France Country
        controller.save()
        then:
        def newCount = domain_class.count()
        response.json.success == true
        response.json.message == alertTagLib.success(label: (validationTagLib.message(code: "default.created.message"))).toString()
        required_properties.each { String property ->
            response.json.data."${property}" == entity_name + "_" + property + "_" + counter
        }
        previousCount != newCount
        println("test save with location success and new location id is ${response.json.data.locationId}")
        counter++;
    }


    /**
     * @goal test edit action when status is not created.
     * @expectedResult response with null model.
     */
    def "test_edit_status_not_created"() {
        setup:
        println("************************test_override_edit_status_not_created********************************")
        def instance = fillEntity()
        instance.requestStatus = EnumRequestStatus.APPROVED
        instance.save(flush: true, failOnError: true)
        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,instance,true)
        }
        controller.edit()

        then:
        model."${entity_name}" == null
        println("test edit not exit done with not model: ${model}")
    }


    /**
     * @goal test selectEmployee action.
     * @expectedResult response with known model.
     */
    def "test_selectEmployee"() {
        setup:
        println("************************test_selectEmployee********************************")
        EmployeePromotion employeePromotion = EmployeePromotion.build()
        EmploymentRecord employmentRecord = EmploymentRecord.build()
        Employee employee = Employee.build(currentEmployeeMilitaryRank: employeePromotion,currentEmploymentRecord: employmentRecord)
        when:
        controller.params["employeeId"] = employee.id
        controller.selectEmployee()
        then:
        response.json.success == true
        response.json.employeeId == employee.id
        println("test selectEmployee done with employeeId: ${response.json.employeeId}")
    }


    /**
     * @goal test selectEmployee action.
     * @expectedResult response with status 404.
     */
    def "test_selectEmployee_not_found"() {

        setup:
        println("************************test_selectEmployee_not_found********************************")
        when:
        controller.params["employeeId"] = null
        controller.selectEmployee()

        then:
        response.json.success == false
        response.json.employeeId == null
        println("test selectEmployee not found done with message: ${response.json.message}")
    }


}