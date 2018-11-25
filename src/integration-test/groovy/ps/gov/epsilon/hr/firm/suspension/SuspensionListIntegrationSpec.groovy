package ps.gov.epsilon.hr.firm.suspension

import grails.buildtestdata.DomainInstanceBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Assume
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.correspondenceList.CorrespondenceListStatus
import ps.police.common.beans.v1.PagedList
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec
import ps.police.test.utils.TestDataObject

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for SuspensionList service
 */
class SuspensionListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = SuspensionList
        service_domain = SuspensionListService
        entity_name = "suspensionList"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(SuspensionList)
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete", "delete", 'resultListToMap', 'search']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
        is_remoting = true
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
     * @goal test addSuspensionRequests method
     * @expectedResult SuspensionList
     */
    def "test_addSuspensionRequests_success"() {
        setup:
        println("****************************test_addSuspensionRequests_success*************")
        SuspensionList suspensionList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = suspensionList?.id
        params.checked_requestIdsList = [1L]
        serviceInstance.addSuspensionRequests(params)
        then:
        def testInstance = serviceInstance.addSuspensionRequests(params)
        testInstance.id == suspensionList?.id
        println "test add suspension request succuess done with: ${testInstance}"
    }

    /**
     * @goal test addSuspensionRequests method
     * @expectedResult SuspensionList
     */
    def "test_addSuspensionRequests_failed"() {
        setup:
        println("****************************test_addSuspensionRequests_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = null
        then:
        def status = serviceInstance.addSuspensionRequests(params)
        status == null
        println "test add suspension request failed done with: ${status}"
    }

    /**
     * @goal test sendData method
     * @expectedResult SuspensionList
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        SuspensionList suspensionList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = suspensionList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.sendData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == suspensionList?.id
        println "test send Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test sendData method
     * @expectedResult SuspensionList
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
     * @expectedResult SuspensionList
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        SuspensionList suspensionList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = suspensionList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.receiveData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == suspensionList?.id
        println "test receive Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test receiveDataModal method
     * @expectedResult SuspensionList
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
     * @goal test approveSuspensionRequest method.
     * @expectedResult SuspensionList
     */
    def "test success approveSuspensionRequest"() {
        setup:
        println("*****************************test success approveSuspensionRequest******************************************")
        def instanceToSave = saveEntity()
        SuspensionListEmployee suspensionListEmployee = SuspensionListEmployee.build(
                suspensionList: instanceToSave

        )
        SuspensionRequest suspensionRequest = SuspensionRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_suspensionRequestTableInSuspensionList"] = [suspensionListEmployee?.id]
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.approveSuspensionRequest(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance approveSuspensionRequest success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved method
     * @expectedResult SuspensionList
     */
    def "test_approveSuspensionRequest_failed"() {
        setup:
        println("****************************test_approveSuspensionRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.approveSuspensionRequest(params)
        then:
        def list = serviceInstance.approveSuspensionRequest(params)
        println "test approve Suspension Request failed done with: ${list}"
    }

    /**
     * @goal test changeSuspensionRequestToRejected method.
     * @expectedResult SuspensionList
     */
    def "test success changeRequestToRejected"() {
        setup:
        println("*****************************test success changeRequestToRejected******************************************")
        def instanceToSave = saveEntity()
        SuspensionListEmployee suspensionListEmployee = SuspensionListEmployee.build(
                suspensionList: instanceToSave

        )
        SuspensionRequest suspensionRequest = SuspensionRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_suspensionRequestTableInSuspensionList"] = [suspensionListEmployee?.id]
        params.note = "rejectNote"
        def map

        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeSuspensionRequestToRejected(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance change suspension request to rejected success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult SuspensionList
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.changeSuspensionRequestToRejected(params)
        then:
        def list = serviceInstance.changeSuspensionRequestToRejected(params)
        println "test reject Request failed done with: ${list}"
    }

    /**
     * @goal test closeList method
     * @expectedResult SuspensionList
     */
    def "test_closeList_success"() {
        setup:
        println("****************************test_closeList_success*************")
        SuspensionList suspensionList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = suspensionList?.id
        serviceInstance.closeList(params)
        then:
        def testInstance = serviceInstance.closeList(params)
        testInstance.id == suspensionList?.id

        println "test close List  succuess done with: ${testInstance}"
    }

    /**
     * @goal test closeList method
     * @expectedResult SuspensionList
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

    /**
     * @goal test search method.
     * @expectedResult known total count.
     */
    def "test search"() {
        setup:
        Assume.assumeFalse(exclude_methods.contains("search"))
        println("*****************************test search******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        def result = serviceInstance.search(params)
        then:
        result?.totalCount == (entity_total_count + 12)
        filtered_parameters.each { property ->
            getPropertyValue(property, result[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance search done with totalCount ${result.totalCount}")
        println("test instance search done with result ${result}")
    }

    /**
     * @goal test search method with filter data.
     * @expectedResult known total count.
     */
    def "test filter search"() {
        setup:
        Assume.assumeFalse(exclude_methods.contains("search"))
        println("*****************************test filter search******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            if (is_join_table) {
                def key = join_table_ids.get(primary_keys) ?: "id"
                map[(property + ".${key}")] = testInstance?."${property}"?."${key}"
            } else {
                sendParams(property, testInstance, map)
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        def result

        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }

        when:
        result = serviceInstance.search(params)
        then:
        result.totalCount == 12
        filtered_parameters.each { property ->
            getPropertyValue(property, result[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance search done with result ${result}")
    }

    /**
     * @goal test resultListToMap method.
     * @expectedResult known total count with known format.
     */
    def "test all data resultListToMap"() {
        setup:
        Assume.assumeFalse(exclude_methods.contains("resultListToMap"))
        println("*****************************test all data resultListToMap******************************************")
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        def searchResult = serviceInstance.search(params)
        Map resultMap
        when:
        resultMap = serviceInstance.resultListToMap(searchResult, params)
        then:
        resultMap.data != null
        resultMap.data instanceof List
        resultMap.data[0] instanceof Map
        filtered_parameters.each { property ->
            getPropertyMap(property, resultMap.data[0]) == getPropertyValue(property, testInstance)
        }
        resultMap.data.size() == (entity_total_count + 12)
        resultMap.recordsTotal == (entity_total_count + 12)
        resultMap.recordsFiltered == (entity_total_count + 12)
        println("test success resultListToMap with recordsTotal ${resultMap.recordsTotal}")
    }

    /**
     * @goal test resultListToMap method with filter.
     * @expectedResult known total count with known format.
     */
    def "test filtered resultListToMap"() {
        setup:
        Assume.assumeFalse(exclude_methods.contains("resultListToMap"))
        println("*****************************test filtered resultListToMap******************************************")
        saveEntity()
        saveEntity()
        saveEntity()
        def testInstance = saveEntity()
        Map map = [max: 10000, offset: 0]
        filtered_parameters.each { String property ->
            if (is_join_table) {
                def key = join_table_ids.get(primary_keys) ?: "id"
                map[(property + ".${key}")] = testInstance?."${property}"?."${key}"
            } else {
                sendParams(property, testInstance, map)
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        def searchResult = serviceInstance.search(params)
        Map resultMap
        when:
        resultMap = serviceInstance.resultListToMap(searchResult, params)
        then:
        resultMap.data != null
        resultMap.data instanceof List
        resultMap.data[0] instanceof Map
        filtered_parameters.each { property ->
            getPropertyMap(property, resultMap.data[0]) == getPropertyValue(property, testInstance)
        }
        resultMap.data.size() == 12
        resultMap.recordsTotal == 12
        resultMap.recordsFiltered == 12
        println("test filtered resultListToMap with recordsTotal ${resultMap.recordsTotal}")
    }


    @Override
    SuspensionList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        SuspensionList instance
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
                def suspensionListEmployee1 = SuspensionListEmployee.buildWithoutSave()
                def suspensionListEmployee2 = SuspensionListEmployee.buildWithoutSave()
                def suspensionListEmployee3 = SuspensionListEmployee.buildWithoutSave()

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