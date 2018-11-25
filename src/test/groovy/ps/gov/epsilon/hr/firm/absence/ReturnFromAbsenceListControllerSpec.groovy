package ps.gov.epsilon.hr.firm.absence

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
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.hr.enums.v1.EnumOperation
import ps.gov.epsilon.hr.firm.Firm
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.gov.epsilon.hr.firm.profile.Employee
import ps.gov.epsilon.hr.firm.profile.EmploymentRecord
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

/**
 * unit test for ReturnFromAbsenceList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([ReturnFromAbsenceList])
@Build([ReturnFromAbsenceList])
@TestFor(ReturnFromAbsenceListController)
class ReturnFromAbsenceListControllerSpec extends CommonUnitSpec {

    SharedService sharedService = mockService(SharedService)
    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)

    def setupSpec() {
        domain_class = ReturnFromAbsenceList
        service_domain = ReturnFromAbsenceListService
        entity_name = "returnFromAbsenceList"
        required_properties = PCPUtils.getRequiredFields(ReturnFromAbsenceList)
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
        model.operationType == EnumOperation.RETURN_FROM_ABSENCE_LIST
        model.referenceObject == ReturnFromAbsenceList.name
        println("test_list done with data : ${model}")
    }

    /**
     * @goal test manageReturnFromAbsenceList action.
     * @expectedResult response with known model.
     */
    def "test_manageList_success"() {
        setup:
        println("************************test_manageList_success********************************")
        def testInstace = ReturnFromAbsenceList.build()
        when:
        params.encodedId = testInstace?.encodedId
        controller.manageReturnFromAbsenceList(params)
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        println "test manage list success with data:  ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test manageReturnFromAbsenceList action.
     * @expectedResult response not found
     */
    def "test_manageList_failed"() {
        setup:
        println("************************test_manageList_failed********************************")
        when:
        controller.manageReturnFromAbsenceList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test manageList failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addReturnFromAbsence request action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_addRequestToList_success"() {
        setup:
        println("****************************test_addRequestToList_success*************")
        Firm firm = Firm.build()
        ReturnFromAbsenceList tetInstance = ReturnFromAbsenceList.build(firm: firm)
        EmploymentRecord employmentRecord = new EmploymentRecord()
        Employee employee = Employee.build(firm: firm)
        ReturnFromAbsenceRequest returnFromAbsenceRequest = ReturnFromAbsenceRequest.build()
        returnFromAbsenceRequest.employee = employee
        returnFromAbsenceRequest.currentEmploymentRecord = employmentRecord
        returnFromAbsenceRequest.firm = firm
        returnFromAbsenceRequest.actualReturnDate = ZonedDateTime.now()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.returnFromAbsenceListId = tetInstance?.id
        params['checked_requestIdsList'] = [returnFromAbsenceRequest?.id]
        controller.addRequestToList()
        then:
        response.json.data.name != null
        response.json.data.id == tetInstance?.id
        println "test add request success done with: ${response.json.data.name}"
    }

    /**
     * @goal test addReturnFromAbsenceRequests action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_addReturnFromAbsenceRequests_failed"() {
        setup:
        println("****************************test_addRequestToList_failed*************")
        when:
        controller.addRequestToList()
        then:
        model != null
        model == [:]
        println "test add request succuess done with: ${model}"
    }

    /**
     * @goal test sendDataModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_sendListModal_success"() {
        setup:
        println("****************************test_sendListModal_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        controller.sendListModal()
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        model.returnFromAbsenceList.id == tetInstance?.id
        println "test send data modal  succuess done with: ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test sendListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_sendListModal_failed"() {
        setup:
        println("****************************test_sendListModal_failed*************")
        when:
        controller.sendListModal()
        then:
        println("test send Data Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test sendList action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_sendList_success"() {
        setup:
        println("****************************test_sendList_success*************")
        ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = returnFromAbsenceList?.id
        params.manualOutgoingNo = 1234
        controller.sendList()
        then:
        response.json.success == true
        println "test send data  succuess done with: ${response.json}"
    }

    /**
     * @goal test sendList action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_sendList_failed"() {
        setup:
        println("****************************test_sendList_failed*************")
        when:
        controller.sendList()
        then:
        println "test send Data  failed done with: ${response}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_receiveListModal_success"() {
        setup:
        println("****************************test_receiveListModal_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        controller.receiveListModal()
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        model.returnFromAbsenceList.id == tetInstance?.id
        println "test receive Data Modal succuess done with: ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_receiveListModal_failed"() {
        setup:
        println("****************************test_receiveListModal_failed*************")
        when:
        controller.receiveListModal()
        then:
        println("test receive Data Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test receiveList action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_receiveList_success"() {
        setup:
        println("****************************test_receiveList_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        params.manualOutgoingNo = 1234
        controller.receiveList()
        then:
        response.json.success == true
        println "test receive Data  succuess done with: ${response.json}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_receiveList_failed"() {
        setup:
        println("****************************test_receiveList_failed*************")
        when:
        controller.receiveList()
        then:
        println "test receive Data  failed done with: ${response}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_approveRequestModal_success"() {
        setup:
        println("****************************test_approveRequestModal_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        controller.approveRequestModal()
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        model.returnFromAbsenceList.id == tetInstance?.id
        println "test approve Request Modall succuess done with: ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult ReturnFromAbsenceList
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
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_changeRequestToApproved_success"() {
        setup:
        println("****************************test_changeRequestToApproved_success*************")
        ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.build()
        when:
        controller.params['id'] = returnFromAbsenceList?.id
        controller.params['note'] = "note"
        controller.params['check_RequestIdList'] = [ReturnFromAbsenceListEmployee.build()?.id]
        controller.changeRequestToApproved()
        then:
        response.json.success == true
        println "test change request to approved  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ReturnFromAbsenceList
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
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_rejectRequestModal_success"() {
        setup:
        println("****************************test_rejectRequestModal_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        controller.rejectRequestModal()
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        model.returnFromAbsenceList.id == tetInstance?.id
        println "test reject Request Modal succuess done with: ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult ReturnFromAbsenceList
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
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_rejectRequest_success"() {
        setup:
        println("****************************test_rejectRequest_success*************")
        ReturnFromAbsenceList returnFromAbsenceList = ReturnFromAbsenceList.build()
        when:
        controller.params['id'] = returnFromAbsenceList?.id
        controller.params['note'] = "note"
        controller.params['check_RequestIdList'] = [ReturnFromAbsenceListEmployee.build()?.id]
        controller.changeRequestToRejected()
        then:
        response.json.success == true
        println "test reject Request  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        controller.changeRequestToApproved()
        then:
        println "test reject Request failed done with: ${response}"
    }

    /**
     * @goal test closeListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_closeListModal_success"() {
        setup:
        println("****************************test_closeListModal_success*************")
        def tetInstance = ReturnFromAbsenceList.build()
        when:
        params.id = tetInstance?.id
        controller.closeListModal()
        then:
        model != null
        model != [:]
        model.returnFromAbsenceList != null
        model.returnFromAbsenceList.id == tetInstance?.id
        println "test close List succuess done with: ${model.returnFromAbsenceList}"
    }

    /**
     * @goal test closeListModal action
     * @expectedResult ReturnFromAbsenceList
     */
    def "test_closeListModal_failed"() {
        setup:
        println("****************************test_closeListModal_failed*************")
        when:
        controller.closeListModal()
        then:
        println("test close Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test closeList action
     * @expectedResult ReturnFromAbsenceList
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
     * @expectedResult ReturnFromAbsenceList
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
    public ReturnFromAbsenceList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        ReturnFromAbsenceList instance
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
            def returnFromAbsenceListEmployee1 = ReturnFromAbsenceListEmployee.buildWithoutSave()
            def returnFromAbsenceListEmployee2 = ReturnFromAbsenceListEmployee.buildWithoutSave()
            def returnFromAbsenceListEmployee3 = ReturnFromAbsenceListEmployee.buildWithoutSave()

            returnFromAbsenceListEmployee1.recordStatus = EnumListRecordStatus.APPROVED
            returnFromAbsenceListEmployee2.recordStatus = EnumListRecordStatus.APPROVED
            returnFromAbsenceListEmployee3.recordStatus = EnumListRecordStatus.APPROVED

            returnFromAbsenceListEmployee1.id = 1
            returnFromAbsenceListEmployee2.id = 2
            returnFromAbsenceListEmployee3.id = 3




            returnFromAbsenceListEmployee1.returnFromAbsenceRequest = ReturnFromAbsenceRequest.build()
            returnFromAbsenceListEmployee2.returnFromAbsenceRequest = ReturnFromAbsenceRequest.build()
            returnFromAbsenceListEmployee3.returnFromAbsenceRequest = ReturnFromAbsenceRequest.build()






            instance.addToReturnFromAbsenceListEmployees(returnFromAbsenceListEmployee1)
            instance.addToReturnFromAbsenceListEmployees(returnFromAbsenceListEmployee2)
            instance.addToReturnFromAbsenceListEmployees(returnFromAbsenceListEmployee3)

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
    public ReturnFromAbsenceList saveEntity(TestDataObject tableData = null) {
        ReturnFromAbsenceList instance = fillEntity(tableData)
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