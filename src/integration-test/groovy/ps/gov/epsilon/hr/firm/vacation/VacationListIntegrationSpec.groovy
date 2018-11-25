package ps.gov.epsilon.hr.firm.vacation

import grails.buildtestdata.DomainInstanceBuilder
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.grails.core.DefaultGrailsDomainClass
import org.junit.Assume
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
 * integration test for VacationList service
 */
class VacationListIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = VacationList
        service_domain = VacationListService
        entity_name = "vacationList"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(VacationList)
        filtered_parameters = ["id"]
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["autocomplete", "delete", 'resultListToMap', 'search']
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
     * @goal test addVacationRequests method
     * @expectedResult VacationList
     */
    def "test_addVacationRequests_success"() {
        setup:
        println("****************************test_addVacationRequests_success*************")
        VacationList vacationList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = vacationList?.id
        params.checked_requestIdsList = [1L]
        serviceInstance.addVacationRequests(params)
        then:
        def testInstance = serviceInstance.addVacationRequests(params)
        testInstance.id == vacationList?.id
        println "test add vacation request succuess done with: ${testInstance}"
    }

    /**
     * @goal test addVacationRequests method
     * @expectedResult VacationList
     */
    def "test_addVacationRequests_failed"() {
        setup:
        println("****************************test_addVacationRequests_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = null
        then:
        def status = serviceInstance.addVacationRequests(params)
        status == null
        println "test add vacation request failed done with: ${status}"
    }

    /**
     * @goal test sendData method
     * @expectedResult VacationList
     */
    def "test_sendData_success"() {
        setup:
        println("****************************test_sendData_success*************")
        VacationList vacationList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = vacationList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.sendData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == vacationList?.id
        println "test send Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test sendData method
     * @expectedResult VacationList
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
     * @expectedResult VacationList
     */
    def "test_receiveData_success"() {
        setup:
        println("****************************test_receiveData_success*************")
        VacationList vacationList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = vacationList?.id
        params.manualOutgoingNo = 1234
        serviceInstance.receiveData(params)
        then:
        def testInstance = serviceInstance.sendData(params)
        testInstance.id == vacationList?.id
        println "test receive Data  succuess done with: ${testInstance}"
    }

    /**
     * @goal test receiveDataModal method
     * @expectedResult VacationList
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
     * @goal test approveVacationRequest method.
     * @expectedResult VacationList
     */
    def "test success approveVacationRequest"() {
        setup:
        println("*****************************test success approveVacationRequest******************************************")
        def instanceToSave = saveEntity()
        VacationListEmployee vacationListEmployee = VacationListEmployee.build(
                vacationList: instanceToSave

        )
        VacationRequest vacationRequest = VacationRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_vacationRequestTableInVacationList"] = [vacationListEmployee?.id]
        def map
        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.approveVacationRequest(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance approveVacationRequest success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved method
     * @expectedResult VacationList
     */
    def "test_approveVacationRequest_failed"() {
        setup:
        println("****************************test_approveVacationRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.approveVacationRequest(params)
        then:
        def status = serviceInstance.approveVacationRequest(params)
        status == null
        println "test approve Vacation Request failed done with: ${status}"
    }

    /**
     * @goal test changeVacationRequestToRejected method.
     * @expectedResult VacationList
     */
    def "test success changeRequestToRejected"() {
        setup:
        println("*****************************test success changeRequestToRejected******************************************")
        def instanceToSave = saveEntity()
        VacationListEmployee vacationListEmployee = VacationListEmployee.build(
                vacationList: instanceToSave

        )
        VacationRequest vacationRequest = VacationRequest.build(
                requestDate: ZonedDateTime.now(),

        )
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params["check_vacationRequestTableInVacationList"] = [vacationListEmployee?.id]
        params.note = "rejectNote"
        def map

        when:
        domain_class.withTransaction { status ->
            map = serviceInstance.changeVacationRequestToRejected(params)
            status.setRollbackOnly()
        }
        then:

        println("test instance change vacation request to rejected success is done with status: ${map}")
    }

    /**
     * @goal test changeRequestToApproved action
     * @expectedResult VacationList
     */
    def "test_rejectRequest_failed"() {
        setup:
        println("****************************test_rejectRequest_failed*************")
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        serviceInstance.changeVacationRequestToRejected(params)
        then:
        def status = serviceInstance.changeVacationRequestToRejected(params)
        status == null
        println "test reject Request failed done with: ${status}"
    }

    /**
     * @goal test closeList method
     * @expectedResult VacationList
     */
    def "test_closeList_success"() {
        setup:
        println("****************************test_closeList_success*************")
        VacationList vacationList = saveEntity()
        when:
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        params.id = vacationList?.id
        serviceInstance.closeList(params)
        then:
        def testInstance = serviceInstance.closeList(params)
        testInstance.id == vacationList?.id

        println "test close List  succuess done with: ${testInstance}"
    }

    /**
     * @goal test closeList method
     * @expectedResult VacationList
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
    VacationList saveEntity(TestDataObject tableData = null, Boolean discardSave = false) {
        if (!tableData) {
            tableData = new TestDataObject()
            tableData.requiredProperties = required_properties
            tableData.domain = domain_class
            tableData.objectName = entity_name
            tableData.data = table_data?.data
            tableData?.isJoinTable = is_join_table
        }
        VacationList instance
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
                def vacationListEmployee1 = VacationListEmployee.buildWithoutSave()
                def vacationListEmployee2 = VacationListEmployee.buildWithoutSave()
                def vacationListEmployee3 = VacationListEmployee.buildWithoutSave()

                instance.addToVacationListEmployees(vacationListEmployee1)
                instance.addToVacationListEmployees(vacationListEmployee2)
                instance.addToVacationListEmployees(vacationListEmployee3)
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