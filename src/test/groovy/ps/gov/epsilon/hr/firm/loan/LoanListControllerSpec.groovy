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
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocument
import ps.gov.epsilon.hr.firm.settings.JoinedFirmOperationDocumentService
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.test.utils.CommonUnitSpec
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.TestDataObject
import spock.lang.Shared

/**
 * unit test for LoanList controller
 */
@TestMixin([HibernateTestMixin, GroovyPageUnitTestMixin, ServiceUnitTestMixin])
@Mock([AlertTagLib])
@Domain([LoanList])
@Build([LoanList, LoanListPerson,LoanRequest, Firm, CorrespondenceListStatus, JoinedFirmOperationDocument])
@TestFor(LoanListController)
class LoanListControllerSpec extends CommonUnitSpec {


    JoinedFirmOperationDocumentService joinedFirmOperationDocumentService = mockService(JoinedFirmOperationDocumentService)
    SharedService sharedService = mockService(SharedService)

    @Shared
    GrailsApplication grailsApplication
    
    
    def setupSpec() {
        domain_class = LoanList
        service_domain = LoanListService
        entity_name = "loanList"
        List required = PCPUtils.getRequiredFields(LoanList)
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
            getArtefact(_,_) >> new DefaultGrailsClass(LoanList?.class)
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
        model.operationType == EnumOperation.LOAN_LIST
        model.referenceObject == LoanList.name
        println("test_list done with data : ${model}")
    }


    /**
     * @goal test edit action.
     * @expectedResult response with known model.
     */
    def "test_edit"() {

        setup:
        println("************************test_edit********************************")
        LoanList instance = fillEntity()
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
        controller.manageLoanList(params)
        then:
        model != null
        model != [:]
        model.loanList != null
        println "test manage list success with data:  ${model.loanList}"
    }

    /**
     * @goal test manageList action.
     * @expectedResult response not found
     */
    def "test_manageList_failed"() {
        setup:
        println("************************test_manageList_failed********************************")
        when:
        controller.manageLoanList()
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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test addRequestModal succuess done with: ${model.loanList}"



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
        LoanList testInstance = saveEntity()
        LoanRequest loanRequest = LoanRequest.build(firm:testInstance?.firm)
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.loanListId = testInstance?.id
        params['checked_requestIdsList'] = [loanRequest?.id]
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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test sendListModal succuess done with: ${model.loanList}"
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
        LoanList loanList = LoanList.build()
        when:
        request.makeAjaxRequest()
        request.method = 'POST'
        params.id = loanList?.id
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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test receiveListModal succuess done with: ${model.loanList}"
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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test approveRequestModal succuess done with: ${model.loanList}"
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
        LoanRequest loanRequest = LoanRequest.build(firm:firm)
        LoanListPerson loanListPerson = LoanListPerson.build(loanRequest:loanRequest,firm:firm)

        when:

        PCPSessionUtils.setValue("firmId",firm?.id)

        controller.params['note'] = "note"
        controller.params['noteDate'] = "16/12/2017"
        controller.params['effectiveDate'] = "16/12/2017"
        controller.params['checked_loanPersonIdsList'] = [loanListPerson?.id]
        controller.params['receivedPersonId'] = [1750L,1752L]


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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test reject Request Modal succuess done with: ${model.loanList}"
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
        LoanRequest loanRequest = LoanRequest.build()
        LoanListPerson loanListPerson = LoanListPerson.build(loanRequest:loanRequest)

        when:
        controller.params['note'] = "note"
        controller.params['noteDate'] = "16/12/2017"
        controller.params['checked_loanPersonIdsList'] = [loanListPerson?.id]

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
        model.loanList != null
        model.loanList.id == testInstance?.id
        println "test closeListModal succuess done with: ${model.loanList}"
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
    public LoanList fillEntity(TestDataObject tableData = null) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.data = table_data?.data
            tableData.objectName = entity_name
            tableData.hasSecurity = has_security
            tableData.isJoinTable = is_join_table
        }
        LoanList instance
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
            def loanListPerson1 = LoanListPerson.buildWithoutSave()
            def loanListPerson2 = LoanListPerson.buildWithoutSave()
            def loanListPerson3 = LoanListPerson.buildWithoutSave()

            loanListPerson1.recordStatus = EnumListRecordStatus.APPROVED
            loanListPerson2.recordStatus = EnumListRecordStatus.APPROVED
            loanListPerson3.recordStatus = EnumListRecordStatus.APPROVED

            loanListPerson1.id = 1L
            loanListPerson2.id = 2L
            loanListPerson3.id = 3L


            loanListPerson1.loanRequest = LoanRequest.build()
            loanListPerson2.loanRequest = LoanRequest.build()
            loanListPerson3.loanRequest = LoanRequest.build()


            instance.addToLoanListPerson(loanListPerson1)
            instance.addToLoanListPerson(loanListPerson2)
            instance.addToLoanListPerson(loanListPerson3)

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
    public LoanList saveEntity(TestDataObject tableData = null) {
        LoanList instance = fillEntity(tableData)
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