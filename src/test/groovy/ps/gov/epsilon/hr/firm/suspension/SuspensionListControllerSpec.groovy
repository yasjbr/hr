package ps.gov.epsilon.hr.firm.suspension

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
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

/**
 * unit test for SuspensionList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([SuspensionList])
@Build([SuspensionList, SuspensionListEmployee, CorrespondenceListStatus, JoinedFirmOperationDocument])
@TestFor(SuspensionListController)
class SuspensionListControllerSpec extends CommonUnitSpec {


    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)


    def setupSpec() {
        domain_class = SuspensionList
        service_domain = SuspensionListService
        entity_name = "suspensionList"
        required_properties = PCPUtils.getRequiredFields(SuspensionList)
        filtered_parameters = ["id"]
        autocomplete_property = "name"
        exclude_actions = ["autocomplete", "list", "delete", "filter"]
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }


    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }
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
        model.operationType == EnumOperation.SUSPENSION_LIST
        model.referenceObject == SuspensionList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageSuspensionList action.
     * @expectedResult response with known model.
     */
    def "test_manageSuspensionList_success"() {
        setup:
        println("************************test_manageSuspensionList_success********************************")
        def testInstace = SuspensionList.build()
        when:
        params.encodedId = testInstace?.encodedId
        controller.manageSuspensionList(params)
        then:
        model != null
        model != [:]
        model.suspensionList != null
        println "test manage suspension list success with data:  ${model.suspensionList}"
    }

    /**
     * @goal test manageSuspensionList action.
     * @expectedResult response not found
     */
    def "test_manageSuspensionList_failed"() {
        setup:
        println("************************test_manageSuspensionList_failed********************************")
        when:
        controller.manageSuspensionList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test manageSuspensionList failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addSuspensionRequests action
     * @expectedResult SuspensionList
     */
    def "test_addSuspensionRequests_success"() {
        setup:
        println("****************************test_addSuspensionRequests_success*************")
        Firm firm = Firm.build()
        SuspensionList tetInstance = SuspensionList.build(firm: firm)
        SuspensionRequest suspensionRequest = SuspensionRequest.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = tetInstance?.id
        params['checked_requestIdsList'] = [suspensionRequest?.id]
        controller.addSuspensionRequests()

        then:
        response.json.data.name != null
        response.json.data.id == tetInstance?.id
        println "test add suspension request success done with: ${response.json.data.name}"
    }

    /**
     * @goal test addSuspensionRequests action
     * @expectedResult SuspensionList
     */
    def "test_addSuspensionRequests_failed"() {
        setup:
        println("****************************test_addSuspensionRequests_failed*************")
        when:
        controller.addSuspensionRequests()
        then:
        model != null
        model == [:]
        println "test add suspension request succuess done with: ${model}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult SuspensionList
     */
    def "test_sendDataModal_success"() {
        setup:
        println("****************************test_sendDataModal_success*************")
        def tetInstance = SuspensionList.build()
        when:
        params.id = tetInstance?.id
        controller.sendDataModal()
        then:
        model != null
        model != [:]
        model.suspensionList != null
        model.suspensionList.id == tetInstance?.id
        println "test send data modal  succuess done with: ${model.suspensionList}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        SuspensionList suspensionList = SuspensionList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = suspensionList?.id
        params.manualOutgoingNo = 1234
        controller.sendData()
        then:
        response.json.success == true
        println "test send data  succuess done with: ${response.json}"
    }

    /**
     * @goal test sendData action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_receiveDataModal_success"() {
        setup:
        println("****************************test_receiveDataModal_success*************")
        def tetInstance = SuspensionList.build()
        when:
        params.id = tetInstance?.id
        controller.receiveDataModal()
        then:
        model != null
        model != [:]
        model.suspensionList != null
        model.suspensionList.id == tetInstance?.id
        println "test receive Data Modal succuess done with: ${model.suspensionList}"
    }

    /**
     * @goal test receiveDataModal action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        def tetInstance = SuspensionList.build()
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
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_approveRequestModal_success"() {
        setup:
        println("****************************test_approveRequestModal_success*************")
        def tetInstance = SuspensionList.build()
        when:
        params.id = tetInstance?.id
        controller.approveRequestModal()
        then:
        model != null
        model != [:]
        model.suspensionList != null
        model.suspensionList.id == tetInstance?.id
        println "test approve Request Modall succuess done with: ${model.suspensionList}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_changeRequestToApproved_success"() {
        setup:
        println("****************************test_changeRequestToApproved_success*************")
        SuspensionList suspensionList = SuspensionList.build()
        when:
        controller.params['id'] = suspensionList?.id
        controller.params['note'] = "note"
        controller.params['check_suspensionRequestTableInSuspensionList'] = [SuspensionListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test change request to approved  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_rejectRequestModal_success"() {
        setup:
        println("****************************test_rejectRequestModal_success*************")
        def tetInstance = SuspensionList.build()
        when:
        params.id = tetInstance?.id
        controller.rejectRequestModal()
        then:
        model != null
        model != [:]
        model.suspensionList != null
        model.suspensionList.id == tetInstance?.id
        println "test reject Request Modal succuess done with: ${model.suspensionList}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_rejectRequest_success"() {
        setup:
        println("****************************test_rejectRequest_success*************")
        SuspensionList suspensionList = SuspensionList.build()
        when:
        controller.params['id'] = suspensionList?.id
        controller.params['note'] = "note"
        controller.params['check_suspensionRequestTableInSuspensionList'] = [SuspensionListEmployee.build()?.id]
        controller.rejectRequest()
        then:
        response.json.success == true
        println "test reject Request  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_closeModal_success"() {
        setup:
        println("****************************test_closeModal_success*************")
        def tetInstance = SuspensionList.build()
        when:
        params.id = tetInstance?.id
        controller.closeModal()
        then:
        model != null
        model != [:]
        model.suspensionList != null
        model.suspensionList.id == tetInstance?.id
        println "test close List succuess done with: ${model.suspensionList}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult SuspensionList
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
 * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
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
    public SuspensionList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        SuspensionList instance
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
            def suspensionListEmployee1 = SuspensionListEmployee.buildWithoutSave()
            def suspensionListEmployee2 = SuspensionListEmployee.buildWithoutSave()
            def suspensionListEmployee3 = SuspensionListEmployee.buildWithoutSave()

            suspensionListEmployee1.recordStatus = EnumListRecordStatus.APPROVED
            suspensionListEmployee2.recordStatus = EnumListRecordStatus.APPROVED
            suspensionListEmployee3.recordStatus = EnumListRecordStatus.APPROVED

            suspensionListEmployee1.id = 1L
            suspensionListEmployee2.id = 2L
            suspensionListEmployee3.id = 3L




            suspensionListEmployee1.suspensionRequest = SuspensionRequest.build()
            suspensionListEmployee2.suspensionRequest = SuspensionRequest.build()
            suspensionListEmployee3.suspensionRequest = SuspensionRequest.build()






            instance.addToSuspensionListEmployees(suspensionListEmployee1)
            instance.addToSuspensionListEmployees(suspensionListEmployee2)
            instance.addToSuspensionListEmployees(suspensionListEmployee3)

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
    public SuspensionList saveEntity(TestDataObject tableData = null) {
        SuspensionList instance = fillEntity(tableData)
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