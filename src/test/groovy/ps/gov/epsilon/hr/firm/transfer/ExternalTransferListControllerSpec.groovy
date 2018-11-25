package ps.gov.epsilon.hr.firm.transfer

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
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

/**
 * unit test for ExternalTransferList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ExternalTransferList])
@Build([ExternalTransferList, ExternalTransferListEmployee, Employee, Firm, EmploymentRecord, ExternalTransferRequest, CorrespondenceListStatus, JoinedFirmOperationDocument])
@TestFor(ExternalTransferListController)
class ExternalTransferListControllerSpec extends CommonUnitSpec {


    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    def setupSpec() {
        domain_class = ExternalTransferList
        service_domain = ExternalTransferListService
        entity_name = "externalTransferList"
        required_properties = PCPUtils.getRequiredFields(ExternalTransferList)
        filtered_parameters = ["id"]
        autocomplete_property = "name"
        exclude_actions = ["autocomplete", "list", "delete", "filter"]
        primary_key_values = ["id", "encodedId"]
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

    /**
     * @goal test delete action.
     * @expectedResult request without params and response with success delete result.
     */
    def "new_test_success_delete"() {

        setup:
        println("************************test_success_delete********************************")
        saveEntity()
        def testInstance = saveEntity()
        testInstance.currentStatus = CorrespondenceListStatus.build()
        testInstance.currentStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CREATED
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
     * @expectedResult request without params and response with success delete result.
     */

    def "new_test_success_delete_ajax"() {

        setup:
        println("************************test_success_delete_ajax********************************")
        saveEntity()
        def testInstance = saveEntity()
        testInstance.currentStatus = CorrespondenceListStatus.build()
        testInstance.currentStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CREATED
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
     * @goal test filter action.
     * @expectedResult response with data table format and known total count.
     */
    def "test_filter_all_data"() {
        setup:
        println("************************test_filter_all_data********************************")
        saveEntity()
        saveEntity()
        def instance = saveEntity()
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, instance))
        }
        when:
        controller.filter()
        then:
        response.json.recordsFiltered == 3
        response.json.draw == null
        response.json.recordsTotal == 3
        println("test_filter all data done: ${response.json.data}")
    }

    /**
     * @goal test list action.
     * @expectedResult response with data table format with attachment
     */
    def "test_list"() {
        setup:
        println("************************test_list********************************")
        when:
        controller.list()

        then:
        model != [:]
        model.attachmentTypeList == []?.toString()
        model.operationType == EnumOperation.EXTERNAL_TRANSFER_LIST
        model.referenceObject == ExternalTransferList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageExternalTransferList action.
     * @expectedResult response with known model.
     */
    def "test_manageExternalTransferList_success"() {
        setup:
        println("************************test_manageExternalTransferList_success********************************")
        def testInstace = ExternalTransferList.build()
        when:
        params.encodedId = testInstace?.encodedId
        controller.manageExternalTransferList(params)
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        println "test manage externalTransfer list success with data:  ${model.externalTransferList}"
    }

    /**
     * @goal test manageExternalTransferList action.
     * @expectedResult response not found
     */
    def "test_manageExternalTransferList_failed"() {
        setup:
        println("************************test_manageExternalTransferList_failed********************************")
        when:
        controller.manageExternalTransferList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test manageExternalTransferList failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addExternalTransferRequests action
     * @expectedResult ExternalTransferList
     */
    def "test_addExternalTransferRequests_success"() {
        setup:
        println("****************************test_addExternalTransferRequests_success*************")
        Firm firm = Firm.build()
        ExternalTransferList tetInstance = ExternalTransferList.build(firm: firm)
        EmploymentRecord employmentRecord = new EmploymentRecord()
        Employee employee = Employee.build(firm: firm)
        ExternalTransferRequest externalTransferRequest = ExternalTransferRequest.build()
        externalTransferRequest.employee = employee
        externalTransferRequest.currentEmploymentRecord = employmentRecord
        externalTransferRequest.firm = firm
        externalTransferRequest.effectiveDate = ZonedDateTime.now()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = tetInstance?.id
        params['checked_requestIdsList'] = [externalTransferRequest?.id]
        controller.addExternalTransferRequests()

        then:
        response.json.data.name != null
        response.json.data.id == tetInstance?.id
        println "test add externalTransfer request success done with: ${response.json.data.name}"
    }

    /**
     * @goal test addExternalTransferRequests action
     * @expectedResult ExternalTransferList
     */
    def "test_addExternalTransferRequests_failed"() {
        setup:
        println("****************************test_addExternalTransferRequests_failed*************")
        when:
        controller.addExternalTransferRequests()
        then:
        model != null
        model == [:]
        println "test add externalTransfer request succuess done with: ${model}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult ExternalTransferList
     */
    def "test_sendDataModal_success"() {
        setup:
        println("****************************test_sendDataModal_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        controller.sendDataModal()
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        model.externalTransferList.id == tetInstance?.id
        println "test send data modal  succuess done with: ${model.externalTransferList}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult ExternalTransferList
     */
    def "test_sendDataModal_failed"() {
        setup:
        println("****************************test_sendDataModal_failed*************")
        when:
        controller.sendDataModal()
        then:
        println("test send Data Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test sendData action
     * @expectedResult ExternalTransferList
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        ExternalTransferList externalTransferList = ExternalTransferList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = externalTransferList?.id
        params.manualOutgoingNo = 1234
        controller.sendData()
        then:
        response.json.success == true
        println "test send data  succuess done with: ${response.json}"
    }

    /**
     * @goal test sendData action
     * @expectedResult ExternalTransferList
     */
    def "test_sendData_failed"() {
        setup:
        println("****************************test_sendData_failed*************")
        when:
        controller.sendData()
        then:
        println "test send Data  failed done with: ${response}"
    }

    /**
     * @goal test receiveDataModal action
     * @expectedResult ExternalTransferList
     */
    def "test_receiveDataModal_success"() {
        setup:
        println("****************************test_receiveDataModal_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        controller.receiveDataModal()
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        model.externalTransferList.id == tetInstance?.id
        println "test receive Data Modal succuess done with: ${model.externalTransferList}"
    }

    /**
     * @goal test receiveDataModal action
     * @expectedResult ExternalTransferList
     */
    def "test_receiveDataModal_failed"() {
        setup:
        println("****************************test_receiveDataModal_failed*************")
        when:
        controller.receiveDataModal()
        then:
        println("test receive Data Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test receiveData action
     * @expectedResult ExternalTransferList
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        params.manualOutgoingNo = 1234
        controller.receiveData()
        then:
        response.json.success == true
        println "test receive Data  succuess done with: ${response.json}"
    }

    /**
     * @goal test receiveDataModal action
     * @expectedResult ExternalTransferList
     */
    def "test_receiveData_failed"() {
        setup:
        println("****************************test_receiveData_failed*************")
        when:
        controller.receiveData()
        then:
        println "test receive Data  failed done with: ${response}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult ExternalTransferList
     */
    def "test_approveRequestModal_success"() {
        setup:
        println("****************************test_approveRequestModal_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        controller.approveRequestModal()
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        model.externalTransferList.id == tetInstance?.id
        println "test approve Request Modall succuess done with: ${model.externalTransferList}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult ExternalTransferList
     */
    def "test_approveRequestModal_failed"() {
        setup:
        println("****************************test_approveRequestModal_failed*************")
        when:
        controller.approveRequestModal()
        then:
        println("test approve Request Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ExternalTransferList
     */
    def "test_changeRequestToApproved_success"() {
        setup:
        println("****************************test_changeRequestToApproved_success*************")
        ExternalTransferList externalTransferList = ExternalTransferList.build()
        when:
        controller.params['id'] = externalTransferList?.id
        controller.params['note'] = "note"
        controller.params['check_externalTransferRequestTableInExternalTransferList'] = [ExternalTransferListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test change request to approved  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ExternalTransferList
     */
    def "test_changeRequestToApproved_failed"() {
        setup:
        println("****************************test_changeRequestToApproved_failed*************")
        when:
        controller.changeRequestToApproved()
        then:
        println "test change request to approved  failed done with: ${response}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult ExternalTransferList
     */
    def "test_rejectRequestModal_success"() {
        setup:
        println("****************************test_rejectRequestModal_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        controller.rejectRequestModal()
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        model.externalTransferList.id == tetInstance?.id
        println "test reject Request Modal succuess done with: ${model.externalTransferList}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult ExternalTransferList
     */
    def "test_rejectRequestModal_failed"() {
        setup:
        println("****************************test_rejectRequestModal_failed*************")
        when:
        controller.rejectRequestModal()
        then:
        println("test reject Request Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ExternalTransferList
     */
    def "test_rejectRequest_success"() {
        setup:
        println("****************************test_rejectRequest_success*************")
        ExternalTransferList externalTransferList = ExternalTransferList.build()
        when:
        controller.params['id'] = externalTransferList?.id
        controller.params['note'] = "note"
        controller.params['check_externalTransferRequestTableInExternalTransferList'] = [ExternalTransferListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test reject Request  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ExternalTransferList
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        controller.rejectRequest()
        then:
        println "test reject Request failed done with: ${response}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult ExternalTransferList
     */
    def "test_closeModal_success"() {
        setup:
        println("****************************test_closeModal_success*************")
        def tetInstance = ExternalTransferList.build()
        when:
        params.id = tetInstance?.id
        controller.closeModal()
        then:
        model != null
        model != [:]
        model.externalTransferList != null
        model.externalTransferList.id == tetInstance?.id
        println "test close List succuess done with: ${model.externalTransferList}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult ExternalTransferList
     */
    def "test_closeModal_failed"() {
        setup:
        println("****************************test_closeModal_failed*************")
        when:
        controller.closeModal()
        then:
        println("test close Modal failed not found done with response: ${response.status}")
    }

/**
 * @goal test closeList action
 * @expectedResult ExternalTransferList
 */
    def "test_closeList_success"() {
        setup:
        println("****************************test_closeList_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.id
        controller.closeList()
        then:
        response.json.success == true
        println "test close List  succuess done with: ${response.json}"
    }

    /**
     * @goal test closeList action
     * @expectedResult ExternalTransferList
     */
    def "test_closeList_failed"() {
        setup:
        println("****************************test_closeList_failed*************")
        when:
        controller.closeList()
        then:
        println "test close List failed done with: ${response}"
    }

    @Override
    public ExternalTransferList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        ExternalTransferList instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            Map addedMap = [:]
            if (tableData.hasSecurity) {
                addedMap.put("springSecurityService", springSecurityService)
            }
            if (tableData.isJoinTable) {
                addedMap.putAll(props)
            }
            instance = tableData?.domain?.buildWithoutValidation(addedMap)

            //add details
            def externalTransferListEmployee1 = ExternalTransferListEmployee.buildWithoutSave()
            def externalTransferListEmployee2 = ExternalTransferListEmployee.buildWithoutSave()
            def externalTransferListEmployee3 = ExternalTransferListEmployee.buildWithoutSave()

            externalTransferListEmployee1.recordStatus = EnumListRecordStatus.APPROVED
            externalTransferListEmployee2.recordStatus = EnumListRecordStatus.APPROVED
            externalTransferListEmployee3.recordStatus = EnumListRecordStatus.APPROVED

            externalTransferListEmployee1.id = 1L
            externalTransferListEmployee2.id = 2L
            externalTransferListEmployee3.id = 3L




            externalTransferListEmployee1.externalTransferRequest = ExternalTransferRequest.build()
            externalTransferListEmployee2.externalTransferRequest = ExternalTransferRequest.build()
            externalTransferListEmployee3.externalTransferRequest = ExternalTransferRequest.build()






            instance.addToExternalTransferListEmployees(externalTransferListEmployee1)
            instance.addToExternalTransferListEmployees(externalTransferListEmployee2)
            instance.addToExternalTransferListEmployees(externalTransferListEmployee3)

            boolean validated = instance.validate()
            if (!validated) {
                DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                if (!builder) {
                    builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                    builders.put(tableData?.objectName, builder)
                }
            }
        }
        return instance
    }


    @Override
    public ExternalTransferList saveEntity(TestDataObject tableData = null) {
        ExternalTransferList instance = fillEntity(tableData)
        once_save_properties.each { property ->
            if (PCPSessionUtils.getValue(property)) {
                instance."${property}" = PCPSessionUtils.getValue(property)
            }
        }
        instance.save(flush: true, failOnError: true)

        //set current status
        def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
        instance.currentStatus = currentStatus

        instance.save(flush: true, failOnError: true)


        once_save_properties.each { property ->
            if (!PCPSessionUtils.getValue(property)) {
                PCPSessionUtils.setValue(property, instance."${property}")
            }
        }

        return instance
    }


}