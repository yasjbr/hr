package ps.gov.epsilon.hr.firm.allowance

import grails.buildtestdata.DomainInstanceBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.core.DefaultGrailsDomainClass
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.PagedList
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for AllowanceList service
 */
class AllowanceListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = AllowanceList
        service_domain = AllowanceListService
        entity_name = "allowanceList"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(AllowanceList)
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete", "delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    /**
     * @goal test searchWithRemotingValues method.
     * @expectedResult known total count.
     */
    def "test searchWithRemotingValues"() {
        setup:
        println("*****************************test searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        PagedList result = serviceInstance.searchWithRemotingValues(params)
        then:
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test filter searchWithRemotingValues method with filter data.
     * @expectedResult known total count.
     */
    def "test filter searchWithRemotingValues"() {
        setup:
        println("*****************************test filter searchWithRemotingValues******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property, testInstance, map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        result = serviceInstance.searchWithRemotingValues(params)
        then:
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test customSearch method.
     * @expectedResult known total count.
     */
    def "test customSearch"() {
        setup:
        println("*****************************test customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        PagedList result = serviceInstance.customSearch(params)
        then:
        println("test instance customSearch done with totalCount ${result.totalCount}")
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test filter customSearch method with filter data.
     * @expectedResult known total count.
     */
    def "test filter customSearch"() {
        setup:
        println("*****************************test filter customSearch******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            sendParams(property, testInstance, map)
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        result = serviceInstance.customSearch(params)
        then:
        println("test instance customSearch done with result ${result}")
    }

    /**
     * @goal test delete method.
     * @thenedResult request without params and response with success delete result.
     */
    def "test success delete"() {

        setup:
        println("************************test_success_delete********************************")
        saveEntity()
        def testInstance = saveEntity()
        testInstance.currentStatus = CorrespondenceListStatus.build()
        testInstance.currentStatus.correspondenceListStatus = EnumCorrespondenceListStatus.CREATED
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        searchMap.put("id", testInstance?.id)
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance?.trackingInfo?.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        deletedInstance != null
        newCount == previousCount
        println("test delete success and new count is ${newCount}")
    }

    /**
     * @goal test delete method.
     * @thenedResult request without params and response with failed deleted result.
     */
    def "test fail delete"() {

        setup:
        println("************************test_fail delete********************************")
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        DeleteBean deleteBean = new DeleteBean()
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        def newCount = domain_class.count()
        newCount == previousCount
        println("test_delete fail done")
    }

    /**
     * @goal test addAllowanceRequests method
     * @expectedResult AllowanceList
     */
    def "test_addAllowanceRequests_success"() {
        setup:
        println("****************************test_addAllowanceRequests_success*************")
        AllowanceList allowanceList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = allowanceList?.id
        params.check_allowanceRequestTableToChooseInAllowance = [1L]
        serviceInstance.addAllowanceRequests(params)
        then:
        def testInstance = serviceInstance.addAllowanceRequests(params)
        testInstance.id == allowanceList?.id
        println "test add allowance request succuess done with: ${testInstance}"
    }

    /**
     * @goal test addAllowanceRequests method
     * @expectedResult AllowanceList
     */
    def "test_addAllowanceRequests_failed"() {
        setup:
        println("****************************test_addAllowanceRequests_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = null
        then:
        def status = serviceInstance.addAllowanceRequests(params)
        status == null
        println "test add allowance request failed done with: ${status}"
    }

    /**
     * @goal test sendData method
     * @expectedResult AllowanceList
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        AllowanceList allowanceList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = allowanceList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.sendData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == allowanceList?.id
        println "test send Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test sendData method
     * @expectedResult AllowanceList
     */
    def "test_sendData_failed"() {
        setup:
        println("****************************test_sendData_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.sendData(params)
        then:
        def status = serviceInstance.sendData(params)
        status == null
        println "test send Data  failed done with: ${status}"
    }

    /**
     * @goal test receiveData method
     * @expectedResult AllowanceList
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        AllowanceList allowanceList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = allowanceList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.receiveData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == allowanceList?.id
        println "test receive Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test receiveDataModal method
     * @expectedResult AllowanceList
     */
    def "test_receiveData_failed"() {
        setup:
        println("****************************test_receiveData_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.receiveData(params)
        then:
        def status = serviceInstance.sendData(params)
        status == null
        println "test receive Data  failed done with: ${status}"
    }

    /**
     * @goal test approveAllowanceRequest method.
     * @expectedResult AllowanceList
     */
    def "test success approveAllowanceRequest"() {
        setup:
        println("*****************************test success approveAllowanceRequest******************************************")
        def instanceToSave = saveEntity()
        AllowanceListEmployee allowanceListEmployee = AllowanceListEmployee.build(
                allowanceList: instanceToSave

        )
        AllowanceRequest allowanceRequest = AllowanceRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_allowanceRequestTableInAllowanceList"] = [allowanceListEmployee?.id]
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.approveAllowanceRequest(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance approveAllowanceRequest success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved method
     * @expectedResult AllowanceList
     */
    def "test_approveAllowanceRequest_failed"() {
        setup:
        println("****************************test_approveAllowanceRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.approveAllowanceRequest(params)
        then:
        def status = serviceInstance.approveAllowanceRequest(params)
        status == null
        println "test approve Allowance Request failed done with: ${status}"
    }

    /**
     * @goal test changeAllowanceRequestToRejected method.
     * @expectedResult AllowanceList
     */
    def "test success changeRequestToRejected"() {
        setup:
        println("*****************************test success changeRequestToRejected******************************************")
        def instanceToSave = saveEntity()
        AllowanceListEmployee allowanceListEmployee = AllowanceListEmployee.build(
                allowanceList: instanceToSave

        )
        AllowanceRequest allowanceRequest = AllowanceRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_allowanceRequestTableInAllowanceList"] = [allowanceListEmployee?.id]
        params.note = "rejectNote"
        def map

        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeAllowanceRequestToRejected(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance change allowance request to rejected success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult AllowanceList
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.changeAllowanceRequestToRejected(params)
        then:
        def status = serviceInstance.changeAllowanceRequestToRejected(params)
        status == null
        println "test reject Request failed done with: ${status}"
    }

    /**
     * @goal test closeList method
     * @expectedResult AllowanceList
     */
    def "test_closeList_success"() {
        setup:
        println("****************************test_closeList_success*************")
        AllowanceList allowanceList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = allowanceList?.id
        serviceInstance.closeList(params)
        then:
        def testInstance = serviceInstance.closeList(params)
        testInstance.id == allowanceList?.id

        println "test close List  succuess done with: ${testInstance}"
    }

    /**
     * @goal test closeList method
     * @expectedResult AllowanceList
     */
    def "test_closeList_failed"() {
        setup:
        println("****************************test_closeList_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.closeList(params)
        then:
        def status = serviceInstance.closeList(params)
        status == null
        println "test close List failed done with: ${status}"
    }


    @Override
    AllowanceList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        AllowanceList instance
        Map props = [:]
        if (tableData?.disableSave) {
            instance = tableData?.domain?.newInstance(props)
        } else {
            tableData?.domain?.withTransaction { status ->
                Map addedMap = [:]
                if (tableData.isJoinTable) {
                    addedMap.putAll(props)
                }
                instance = tableData?.domain?.buildWithoutValidation(addedMap)

                //add details
                def violationListEmployee1 = AllowanceListEmployee.buildWithoutSave()
                def violationListEmployee2 = AllowanceListEmployee.buildWithoutSave()
                def violationListEmployee3 = AllowanceListEmployee.buildWithoutSave()

                instance.addToAllowanceListEmployee(violationListEmployee1)
                instance.addToAllowanceListEmployee(violationListEmployee2)
                instance.addToAllowanceListEmployee(violationListEmployee3)
                boolean validated = instance.validate()
                if (!validated) {
                    DomainInstanceBuilder builder = builders.get(tableData?.objectName)
                    if (!builder) {
                        builder = new DomainInstanceBuilder(new DefaultGrailsDomainClass(tableData?.domain))
                        builders.put(tableData?.objectName, builder)
                    }
                }
                once_save_properties.each { property ->
                    if (PCPSessionUtils.getValue(property)) {
                        instance."${property}" = PCPSessionUtils.getValue(property)
                    }
                }
                if (!discardSave) {
                    instance.save(flush: true, failOnError: true)
                }

                //set current status
                def currentStatus = CorrespondenceListStatus.buildWithoutSave(correspondenceList: instance)
                instance.currentStatus = currentStatus

                if (!discardSave) {
                    instance.save(flush: true, failOnError: true)
                }
                once_save_properties.each { property ->
                    if (!PCPSessionUtils.getValue(property)) {
                        PCPSessionUtils.setValue(property, instance."${property}")
                    }
                }
                status.setRollbackOnly()
            }
        }
        return instance
    }
}