package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for Vacancy service
 */
class VacancyIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = Vacancy
        service_domain = VacancyService
        entity_name = "vacancy"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(Vacancy)
        filtered_parameters = ["id"]
        autocomplete_property = "job.descriptionInfo.localName"
        primary_keys = ["id", "encodedId"]
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

}