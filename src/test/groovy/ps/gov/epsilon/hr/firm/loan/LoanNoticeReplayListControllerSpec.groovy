package ps.gov.epsilon.hr.firm.loan

import grails.buildtestdata.DomainInstanceBuilder
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
import ps.gov.epsilon.hr.firm.promotion.EmployeePromotion
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

/**
 * unit test for LoanNoticeReplayList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanNoticeReplayList])
@Build([LoanNoticeReplayList, LoanNominatedEmployee,LoanNoticeReplayRequest,
        EmploymentRecord,EmployeePromotion,Employee,Firm, CorrespondenceListStatus, JoinedFirmOperationDocument])
@TestFor(LoanNoticeReplayListController)
class LoanNoticeReplayListControllerSpec extends CommonUnitSpec {


    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)

    @Shared
    GrailsApplication grailsApplication


    def setupSpec() {
        domain_class = LoanNoticeReplayList
        service_domain = LoanNoticeReplayListService
        entity_name = "loanNoticeReplayList"
        List required = PCPUtils.getRequiredFields(LoanNoticeReplayList)
        required << "code"
        required_properties = required
        filtered_parameters = ["id"]
        autocomplete_property = "name"
        exclude_actions = ["autocomplete", "list","edit"]
        primary_key_values = ["id", "encodedId"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true

        grailsApplication = Mock(GrailsApplication) {
            getArtefact(_,_) >> new DefaultGrailsClass(LoanNoticeReplayList?.class)
        }
    }

    def setup() {

        grails.buildtestdata.TestDataConfigurationHolder.reset()

        if (!controller.sharedService) {
            controller.sharedService = sharedService
            controller.sharedService.joinedFirmOperationDocumentService = joinedFirmOperationDocumentService
        }

        sharedService.grailsApplication = grailsApplication

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
        model.operationType == EnumOperation.LOAN_NOTICE_REPLAY_LIST
        model.referenceObject == LoanNoticeReplayList.name
        println("test_list done with data : ${model}")
    }


    /**
     * @goal test edit action.
     * @expectedResult response with known model.
     */
    def "test_edit"() {

        setup:
        println("************************test_edit********************************")
        LoanNoticeReplayList instance = fillEntity()
        once_save_properties.each { property ->
            if (PCPSessionUtils.getValue(property)) {
                instance."${property}" = PCPSessionUtils.getValue(property)
            }
        }
        instance.save(flush: true, failOnError: true)

        //set current status
        def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance,correspondenceListStatus:EnumCorrespondenceListStatus.CREATED)
        instance.currentStatus = currentStatus
        instance.save(flush: true, failOnError: true)
        once_save_properties.each { property ->
            if (!PCPSessionUtils.getValue(property)) {
                PCPSessionUtils.setValue(property, instance."${property}")
            }
        }

        session_parameters.each {key,value->
            PCPSessionUtils.setValue(key,getPropertyValue(value,instance))
        }
        when:
        primary_key_values.each { key ->
            controller.params["${key}"] = getPropertyValue(key,instance,true)
        }
        controller.edit()

        then:
        model."${entity_name}" != null
        required_properties.each { String property ->
            getPropertyValue(property,model?."${entity_name}") == entity_name + "_" + property + "_" + counter
        }
        println("test_edit done with ${entity_name} : ${model."${entity_name}"}")
        counter++
    }

    /**
     * @goal test edit action.
     * @expectedResult response with null model.
     */
    def "test_edit_not_exists"() {

        setup:
        println("************************test_edit_not_exists********************************")
        when:
        counter++
        required_properties.each { String property ->
            controller.params["${property}"] = entity_name + "_" + property + "_" + counter
        }
        controller.edit()

        then:
        model."${entity_name}" == null
        println("test edit not exit done with not model: ${model}")
    }

    /**
     * @goal test edit action.
     * @expectedResult response with status 404.
     */
    def "test_edit_not_found_element"() {
        setup:
        println("************************test_edit_not_found_element********************************")

        when:
        controller.edit()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test_edit not found with status: ${response.status}")
    }


    /**
     * @goal test manageList action.
     * @expectedResult response with known model.
     */
    def "test_manageList_success"() {
        setup:
        println("************************test_managelist_success********************************")
        def testInstace = saveEntity()
        when:
        params.encodedId = testInstace?.encodedId
        controller.manageList(params)
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        println "test manage list success with data:  ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test manageList action.
     * @expectedResult response not found
     */
    def "test_manageList_failed"() {
        setup:
        println("************************test_manageList_failed********************************")
        when:
        controller.manageList()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test manageList failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addRequestModal action
     * @expectedResult response with known model
     */
    def "test_addRequestModal_success"() {
        setup:
        println("****************************test_addRequestModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.addRequestModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test addRequestModal succuess done with: ${model.loanNoticeReplayList}"



    }

    /**
     * @goal test addRequestModal action
     * @expectedResult response with known model
     */
    def "test_addRequestModal_failed"() {
        setup:
        println("****************************test_addRequestModal_failed*************")
        when:
        controller.addRequestModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test addRequestModal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test addRequest action
     * @expectedResult response with known model
     */
    def "test_addRequest_success"() {
        setup:
        println("****************************test_addRequest_success*************")
        LoanNoticeReplayList testInstance = saveEntity()
        List<Long> requestIdsList = []
        testInstance.loanNominatedEmployees?.each { LoanNominatedEmployee loanNominatedEmployee ->
            loanNominatedEmployee?.loanNoticeReplayRequest?.each { LoanNoticeReplayRequest loanNoticeReplayRequest ->
                requestIdsList << loanNoticeReplayRequest?.id
            }
        }

        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.loanNoticeReplayListId = testInstance?.id
        params['checked_requestIdsList'] = requestIdsList
        controller.addRequest()

        then:
        response.json.success == true
        response.json.errorList == []
        println "test add request success done with: ${response.json}"
    }

    /**
     * @goal test addRequest action
     * @expectedResult response with known model
     */
    def "test_addRequest_failed"() {
        setup:
        println("****************************test_addRequest_failed*************")
        when:
        request.makeAjaxRequest()
        controller.addRequest()
        then:
        response.json.success == false
        response.json.data == null
        response.json.errorList.size() > 0
        println "test add externalTransfer request succuess done with: ${response.json}"
    }

    /**
     * @goal test sendListModal action
     * @expectedResult response with known model
     */
    def "test_sendListModal_success"() {
        setup:
        println("****************************test_sendListModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.sendListModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test sendListModal succuess done with: ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test sendListModal action
     * @expectedResult response with known model
     */
    def "test_sendListModal_failed"() {
        setup:
        println("****************************test_sendListModal_failed*************")
        when:
        controller.sendListModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()
        println("test sendListModal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test sendList action
     * @expectedResult response with known model
     */
    def "test_sendList_success"() {
        setup:
        println("****************************test_sendList_success*************")
        LoanNoticeReplayList loanNoticeReplayList = LoanNoticeReplayList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = loanNoticeReplayList?.id
        params.manualOutgoingNo = 1234
        controller.sendList()
        then:
        response.json.success == true
        println "test send data  succuess done with: ${response.json}"
    }

    /**
     * @goal test sendList action
     * @expectedResult response with known model
     */
    def "test_sendList_failed"() {
        setup:
        println("****************************test_sendList_failed*************")
        when:
        request.makeAjaxRequest()
        controller.sendList()
        then:
        response.json.success == false
        response.json.message.contains(validationTagLib.message(code: "default.not.found.message"))

        println "test send Data  failed done with: ${response}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult response with known model
     */
    def "test_receiveListModal_success"() {
        setup:
        println("****************************test_receiveListModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.receiveListModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test receiveListModal succuess done with: ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult response with known model
     */
    def "test_receiveListModal_failed"() {
        setup:
        println("****************************test_receiveListModal_failed*************")
        when:
        controller.receiveListModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()

        println("test receiveListModal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test receiveList action
     * @expectedResult response with known model
     */
    def "test_receiveList_success"() {
        setup:
        println("****************************test_receiveList_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.id
        params.manualOutgoingNo = 1234
        request.makeAjaxRequest()
        controller.receiveList()
        then:
        response.json.success == true
        println "test receiveListModal succuess done with: ${response.json}"
    }

    /**
     * @goal test receiveListModal action
     * @expectedResult response with known model
     */
    def "test_receiveList_failed"() {
        setup:
        println("****************************test_receiveList_failed*************")
        when:
        request.makeAjaxRequest()
        controller.receiveList()
        then:
        response.json.success == false
        response.json.message.contains(validationTagLib.message(code: "default.not.found.message"))

        println "test receive Data  failed done with: ${response}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult response with known model
     */
    def "test_approveRequestModal_success"() {
        setup:
        println("****************************test_approveRequestModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.approveRequestModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test approveRequestModal succuess done with: ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test approveRequestModal action
     * @expectedResult response with known model
     */
    def "test_approveRequestModal_failed"() {
        setup:
        println("****************************test_approveRequestModal_failed*************")
        when:
        controller.approveRequestModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()

        println("test approveRequestModal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test approveRequest action
     * @expectedResult response with known model
     */
    def "test_approveRequest_success"() {
        setup:
        println("****************************test_approveRequest_success*************")
        Firm firm = Firm.build()
        LoanNoticeReplayRequest loanNoticeReplayRequest = LoanNoticeReplayRequest.build(firm:firm)
        LoanNominatedEmployee loanNominatedEmployee = LoanNominatedEmployee.build(loanNoticeReplayRequest:loanNoticeReplayRequest)

        when:

        PCPSessionUtils.setValue("firmId",firm?.id)

        controller.params['note'] = "note"
        controller.params['noteDate'] = "16/12/2017"
        controller.params['effectiveDate'] = "16/12/2017"
        controller.params['checked_loanNominatedEmployeeIdsList'] = [loanNominatedEmployee?.id]


        request.makeAjaxRequest()
        controller.approveRequest()

        then:
        response.json.success == true
        println "test change approveRequest succuess done with: ${response.json}"
    }

    /**
     * @goal test approveRequest action
     * @expectedResult response with known model
     */
    def "test_approveRequest_failed"() {
        setup:
        println("****************************test_approveRequest_failed*************")
        when:
        request.makeAjaxRequest()
        controller.approveRequest()
        then:
        response.json.success == false
        response.json.data == null
        response.json.errorList.size() > 0

        println "test approveRequest failed done with: ${response}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult response with known model
     */
    def "test_rejectRequestModal_success"() {
        setup:
        println("****************************test_rejectRequestModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.rejectRequestModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test reject Request Modal succuess done with: ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test rejectRequestModal action
     * @expectedResult response with known model
     */
    def "test_rejectRequestModal_failed"() {
        setup:
        println("****************************test_rejectRequestModal_failed*************")
        when:
        controller.rejectRequestModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()

        println("test reject Request Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult response with known model
     */
    def "test_rejectRequest_success"() {
        setup:
        println("****************************test_rejectRequest_success*************")
        LoanNoticeReplayRequest loanNoticeReplayRequest = LoanNoticeReplayRequest.build()
        LoanNominatedEmployee loanNominatedEmployee = LoanNominatedEmployee.build(loanNoticeReplayRequest:loanNoticeReplayRequest)

        when:
        controller.params['note'] = "note"
        controller.params['noteDate'] = "16/12/2017"
        controller.params['checked_loanNominatedEmployeeIdsList'] = [loanNominatedEmployee?.id]

        request.makeAjaxRequest()
        controller.rejectRequest()

        then:
        response.json.success == true
        println "test reject Request  succuess done with: ${response.json}"
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult response with known model
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        request.makeAjaxRequest()
        controller.rejectRequest()
        then:
        response.json.success == false
        response.json.data == null
        response.json.errorList.size() > 0

        println "test reject Request failed done with: ${response}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult response with known model
     */
    def "test_closeListModal_success"() {
        setup:
        println("****************************test_closeListModal_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.encodedId
        controller.closeListModal()
        then:
        model != null
        model != [:]
        model.loanNoticeReplayList != null
        model.loanNoticeReplayList.id == testInstance?.id
        println "test closeListModal succuess done with: ${model.loanNoticeReplayList}"
    }

    /**
     * @goal test closeModal action
     * @expectedResult response with known model
     */
    def "test_closeListModal_failed"() {
        setup:
        println("****************************test_closeListModal_failed*************")
        when:
        controller.closeListModal()
        then:
        response.status == HttpStatus.NOT_FOUND.value()

        println("test close Modal failed not found done with response: ${response.status}")
    }

    /**
     * @goal test closeList action
     * @expectedResult response with known model
     */
    def "test_closeList_success"() {
        setup:
        println("****************************test_closeList_success*************")
        def testInstance = saveEntity()
        when:
        params.id = testInstance?.id
        request.makeAjaxRequest()
        controller.closeList()
        then:
        response.json.success == true
        println "test close List  succuess done with: ${response.json}"
    }

    /**
     * @goal test closeList action
     * @expectedResult response with known model
     */
    def "test_closeList_failed"() {
        setup:
        println("****************************test_closeList_failed*************")
        when:
        request.makeAjaxRequest()
        controller.closeList()
        then:
        response.json.success == false
        response.json.data == null
        response.json.errorList.size() > 0

        println "test close List failed done with: ${response}"
    }

    @Override
    public LoanNoticeReplayList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        LoanNoticeReplayList instance
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
            EmploymentRecord currentEmploymentRecord = EmploymentRecord.build()
            EmployeePromotion currentEmployeeMilitaryRank = EmployeePromotion.build()

            Employee employee1 =  Employee.build(currentEmploymentRecord:currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank)
            Employee employee2 =  Employee.build(currentEmploymentRecord:currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank)
            Employee employee3 =  Employee.build(currentEmploymentRecord:currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank)

            def loanNominatedEmployee1 = LoanNominatedEmployee.buildWithoutSave(employee:employee1,currentEmploymentRecord: currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank )
            def loanNominatedEmployee2 = LoanNominatedEmployee.buildWithoutSave(employee:employee2,currentEmploymentRecord: currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank)
            def loanNominatedEmployee3 = LoanNominatedEmployee.buildWithoutSave(employee:employee3,currentEmploymentRecord: currentEmploymentRecord,currentEmployeeMilitaryRank:currentEmployeeMilitaryRank)

            loanNominatedEmployee1.recordStatus = EnumListRecordStatus.APPROVED
            loanNominatedEmployee2.recordStatus = EnumListRecordStatus.APPROVED
            loanNominatedEmployee3.recordStatus = EnumListRecordStatus.APPROVED

            loanNominatedEmployee1.id = 1L
            loanNominatedEmployee2.id = 2L
            loanNominatedEmployee3.id = 3L


            loanNominatedEmployee1.loanNoticeReplayRequest = LoanNoticeReplayRequest.build(employee:employee1)
            loanNominatedEmployee2.loanNoticeReplayRequest = LoanNoticeReplayRequest.build(employee:employee2)
            loanNominatedEmployee3.loanNoticeReplayRequest = LoanNoticeReplayRequest.build(employee:employee3)


            instance.addToLoanNominatedEmployees(loanNominatedEmployee1)
            instance.addToLoanNominatedEmployees(loanNominatedEmployee2)
            instance.addToLoanNominatedEmployees(loanNominatedEmployee3)

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
    public LoanNoticeReplayList saveEntity(TestDataObject tableData = null) {
        LoanNoticeReplayList instance = fillEntity(tableData)
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