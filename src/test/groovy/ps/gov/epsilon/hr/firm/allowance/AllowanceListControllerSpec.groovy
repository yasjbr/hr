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
import org.springframework.http.HttpStatus
import ps.gov.epsilon.hr.common.SharedService
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.enums.v1.EnumReceivingParty
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

/**
 * unit test for AllowanceList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([AllowanceList])
@Build([AllowanceList, AllowanceListEmployee, JoinedFirmOperationDocument, CorrespondenceListStatus, AllowanceRequest])
@TestFor(AllowanceListController)
class AllowanceListControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)

    def setupSpec() {
        domain_class = AllowanceList
        service_domain = AllowanceListService
        entity_name = "allowanceList"
        required_properties = PCPUtils.getRequiredFields(AllowanceList)
        filtered_parameters = ["id"];
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
        model.operationType == EnumOperation.ALLOWANCE_LIST
        model.referenceObject == AllowanceList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageAllowanceList action.
     * @expectedResult response with known model.
     */
    def "test_manageAllowanceList_success"() {
        setup:
        println("************************test_manageAllowanceList_success********************************")
        def testInstace = AllowanceList.build()
        when:
        params.encodedId = testInstace?.encodedId
        controller.manageAllowanceList(params)
        then:
        model != null
        model != [:]
        model.allowanceList != null
        println "test manage allowance list success with data:  ${model.allowanceList}"
    }

    /**
     * @goal test manageAllowanceList action.
     * @expectedResult response not found
     */
    def "test_manageAllowanceList_failed"() {
        setup:
        println("************************test_manageAllowanceList_failed********************************")
        when:
        controller.manageAllowanceList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test manageAllowanceList failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addAllowanceRequests action
     * @expectedResult success message
     */
    def "test_addAllowanceRequests_success"() {
        setup:
        println("****************************test_addAllowanceRequests_success*************")
        def testInstance = saveEntity()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = testInstance?.id
        params.check_allowanceRequestTableToChooseInAllowance = [AllowanceListEmployee.build()?.id, AllowanceListEmployee.build()?.id, AllowanceListEmployee.build()?.id, AllowanceListEmployee.build()?.id]
        controller.addAllowanceRequests()
        then:
        response.json.success == true
        response.json.data.code == testInstance?.code
        println "test add allowance request success done with: ${response.json.data.code}"
    }

    /**
     * @goal test addAllowanceRequests action
     * @expectedResult failed message
     */
    def "test_addAllowanceRequests_failed"() {
        setup:
        println("****************************test_addAllowanceRequests_failed*************")
        when:
        controller.addAllowanceRequests()
        then:
        model != null
        model == [:]
        println "test add allowance request succuess done with: ${model}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult success message
     */
    def "test_sendDataModal_success"() {
        setup:
        println("****************************test_sendDataModal_success*************")
        def tetInstance = AllowanceList.build()
        when:
        params.id = tetInstance?.id
        controller.sendDataModal()
        then:
        model != null
        model != [:]
        model.allowanceList != null
        model.allowanceList.id == tetInstance?.id
        println "test send data modal  succuess done with: ${model.allowanceList}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        AllowanceList allowanceList= AllowanceList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = allowanceList?.id
        params.manualOutgoingNo = 1234
        controller.sendData()
        then:
        response.json.success == true
        println "test send data  succuess done with: ${response.json}"
    }

    /**
     * @goal test sendData action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_receiveDataModal_success"() {
        setup:
        println("****************************test_receiveDataModal_success*************")
        def tetInstance = AllowanceList.build()
        when:
        params.id = tetInstance?.id
        controller.receiveDataModal()
        then:
        model != null
        model != [:]
        model.allowanceList != null
        model.allowanceList.id == tetInstance?.id
        println "test receive Data Modal succuess done with: ${model.allowanceList}"
    }

    /**
     * @goal test receiveDataModal action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        def tetInstance = AllowanceList.build()
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
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_approveRequestModal_success"() {
        setup:
        println("****************************test_approveRequestModal_success*************")
        def tetInstance = AllowanceList.build()
        when:
        params.id = tetInstance?.id
        controller.approveRequestModal()
        then:
        model != null
        model != [:]
        model.allowanceList != null
        model.allowanceList.id == tetInstance?.id
        println "test approve Request Modall succuess done with: ${model.allowanceList}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_changeRequestToApproved_success"() {
        setup:
        println("****************************test_changeRequestToApproved_success*************")
        AllowanceList allowanceList = AllowanceList.build()
        when:
        controller.params['id'] = allowanceList?.id
        controller.params['note'] = "note"
        controller.params['check_allowanceRequestTableInAllowanceList'] = [AllowanceListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test change request to approved  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_rejectRequestModal_success"() {
        setup:
        println("****************************test_rejectRequestModal_success*************")
        def tetInstance = AllowanceList.build()
        when:
        params.id = tetInstance?.id
        controller.rejectRequestModal()
        then:
        model != null
        model != [:]
        model.allowanceList != null
        model.allowanceList.id == tetInstance?.id
        println "test reject Request Modal succuess done with: ${model.allowanceList}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_rejectRequest_success"() {
        setup:
        println("****************************test_rejectRequest_success*************")
        AllowanceList allowanceList = AllowanceList.build()
        when:
        controller.params['id'] = allowanceList?.id
        controller.params['note'] = "note"
        controller.params['check_allowanceRequestTableInAllowanceList'] = [AllowanceListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test reject Request  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult failed message
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
     * @expectedResult success message
     */
    def "test_closeModal_success"() {
        setup:
        println("****************************test_closeModal_success*************")
        def tetInstance = AllowanceList.build()
        when:
        params.id = tetInstance?.id
        controller.closeModal()
        then:
        model != null
        model != [:]
        model.allowanceList != null
        model.allowanceList.id == tetInstance?.id
        println "test close List succuess done with: ${model.allowanceList}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult failed message
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
 * @expectedResult success message
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
     * @expectedResult failed message
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
    public AllowanceList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        AllowanceList instance
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
            def allowanceListEmployee1 = AllowanceListEmployee.buildWithoutSave()
            def allowanceListEmployee2 = AllowanceListEmployee.buildWithoutSave()
            def allowanceListEmployee3 = AllowanceListEmployee.buildWithoutSave()

            allowanceListEmployee1.recordStatus = EnumListRecordStatus.APPROVED
            allowanceListEmployee2.recordStatus = EnumListRecordStatus.APPROVED
            allowanceListEmployee3.recordStatus = EnumListRecordStatus.APPROVED

            allowanceListEmployee1.id = 1L
            allowanceListEmployee2.id = 2L
            allowanceListEmployee3.id = 3L


            allowanceListEmployee1.allowanceRequest = AllowanceRequest.build()
            allowanceListEmployee2.allowanceRequest = AllowanceRequest.build()
            allowanceListEmployee3.allowanceRequest = AllowanceRequest.build()






            instance.addToAllowanceListEmployee(allowanceListEmployee1)
            instance.addToAllowanceListEmployee(allowanceListEmployee2)
            instance.addToAllowanceListEmployee(allowanceListEmployee3)

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
    public AllowanceList saveEntity(TestDataObject tableData = null) {
        AllowanceList instance = fillEntity(tableData)
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