package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for JobRequisition service
 */
class JobRequisitionIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = JobRequisition
        service_domain = JobRequisitionService
        entity_name = "jobRequisition"
        hashing_entity = "id"
        with_hashing_flag = false
        List requiredProperties = PCPUtils.getRequiredFields(JobRequisition)
        requiredProperties << "requestedForDepartment"
        filtered_parameters = ["id"]
        autocomplete_property = "job.descriptionInfo.localName";
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["delete", "save"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete=true
        is_encrypted_delete=false
    }

    /**
     * @goal test delete method.
     * @thenedResult request without params and response with success delete result.
     */
    /*def "new_test_success_delete"() {
        setup:
        println("************************test_success_delete********************************")
        saveEntity()
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        deletedInstance != null
        newCount == previousCount
        println("test delete success and new count is ${newCount}")
    }

    *//**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with success delete result.
     *//*
    def "new_test_success_delete_ajax"() {
        setup:
        println("************************test_success_delete_ajax********************************")
        saveEntity()
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        deleteBean?.ids = [testInstance.id]
        serviceInstance.delete(deleteBean, false)
        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        deletedInstance.trackingInfo.status == GeneralStatus.DELETED
        def newCount = domain_class.count()
        newCount == previousCount
        println("test delete ajax success and new count is ${newCount}")
    }

    *//**
     * @goal test delete method.
     * @thenedResult request without params and response with failed deleted result.
     *//*
    def "new_test_fail_delete"() {
        setup:
        println("************************test_fail delete********************************")
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
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

    *//**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with failed deleted result.
     *//*
    def "new_test_fail_delete_ajax"() {
        setup:
        println("************************test_fail_delete_ajax********************************")
        def testInstance = saveEntity()
        def previousCount = domain_class.count()
        GrailsParameterMap searchMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        primary_keys.each { key ->
            searchMap.put(key, (getPropertyValue(key, testInstance)))
        }
        when:
        DeleteBean deleteBean = new DeleteBean()
        serviceInstance.delete(deleteBean, false)

        then:
        def deletedInstance = serviceInstance.search(searchMap)[0]
        deletedInstance != null
        def newCount = domain_class.count()
        newCount == previousCount
        deletedInstance.trackingInfo.status == GeneralStatus.ACTIVE
        println("test_delete ajax fail done")
    }*/

    /**
     * @goal test search method.
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
        def result = serviceInstance.searchWithRemotingValues(params)
        then:
        result?.totalCount == (entity_total_count + 3)
        filtered_parameters.each { property ->
            getPropertyValue(property, result[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance searchWithRemotingValues done with totalCount ${result.totalCount}")
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test searchWithRemotingValues method with filter data.
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
        result = serviceInstance.searchWithRemotingValues(params)
        then:
        result.totalCount == 1
        filtered_parameters.each { property ->
            getPropertyValue(property, result[0]) == getPropertyValue(property, testInstance)
        }
        println("test instance searchWithRemotingValues done with result ${result}")
    }

    /**
     * @goal test get method.
     * @expectedResult get not null instance.
     */
    def "test success getInstanceWithRemotingValues"() {
        setup:
        println("*****************************test success getInstanceWithRemotingValues******************************************")
        def testInstance = saveEntity()
        Map map = [:]
        filtered_parameters.each { property ->
            map.put(property, getPropertyValue(property, testInstance, true))
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        session_parameters.each { key, value ->
            PCPSessionUtils.setValue(key, getPropertyValue(value, testInstance))
        }
        when:
        def instance
        if (hashing_entity && with_hashing_flag == true) {
            instance = serviceInstance.getInstance(params, true)
        } else {
            instance = serviceInstance.getInstance(params)
        }
        then:
        instance != null
        filtered_parameters.each { property ->
            getPropertyValue(property, instance) != null
        }
        println("test getInstanceWithRemotingValues instance success with data: ${instance}")
    }

    /**
     * @goal test getInstanceWithRemotingValues method.
     * @expectedResult getInstanceWithRemotingValues null instance.
     */
    def "test fail getInstanceWithRemotingValues"() {
        setup:
        println("*****************************test fail getInstanceWithRemotingValues******************************************")
        Map map = [:]
        filtered_parameters.each { property ->
            if (hashing_entity == property) {
                map.put(property, null)
            } else {
                map.put(property, "NOT_FOUND")
            }
        }
        GrailsParameterMap params = new GrailsParameterMap(map, new MockHttpServletRequest())
        when:
        def instance
        if (hashing_entity && with_hashing_flag == true) {
            instance = serviceInstance.getInstance(params, true)
        } else {
            instance = serviceInstance.getInstance(params)
        }
        then:
        instance == null
        println("test instance getInstanceWithRemotingValues fail done")
    }

}