package ps.gov.epsilon.hr.firm

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import net.sf.ehcache.search.expression.Or
import org.springframework.mock.web.MockHttpServletRequest
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPUtils
import ps.police.pcore.v2.entity.organization.OrganizationService
import ps.police.pcore.v2.entity.organization.commands.v1.OrganizationCommand
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for Firm service
 */
class FirmIntegrationSpec extends CommonIntegrationSpec {

    OrganizationService organizationService

    def setupSpec() {
        domain_class = Firm
        service_domain = FirmService
        entity_name = "firm"
        hashing_entity = "id"
        with_hashing_flag = false
        List requiredProperties = PCPUtils.getRequiredFields(Firm)
        requiredProperties << "coreOrganizationId"
        required_properties = requiredProperties
        filtered_parameters = ["id"]
        autocomplete_property = "name"
        primary_keys = ["id", 'encodedId']
        entity_total_count = 1
        exclude_methods = ["delete"]

    }
    /**
     * @goal test delete method.
     * @thenedResult request without params and response with success delete result.
     */
    def "new_test_success_delete"() {

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

    /**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with success delete result.
     */

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

    /**
     * @goal test delete method.
     * @thenedResult request without params and response with failed deleted result.
     */
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

    /**
     * @goal test delete method with ajax request.
     * @thenedResult request without params and response with failed deleted result.
     */
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
    }

    def "test_saveOrganization"() {
        setup:
        println("**********************test_saveOrganization**********************")
        GrailsParameterMap params = new GrailsParameterMap([:], new MockHttpServletRequest())
        when:
        params["corporationClassification.id"] = 1L
        params["parentOrganization.id"] = 1L
        params["organizationMainActivity.id"] = 1L
        params["organizationType.id"] = 1L
        params["workingSector.id"] = 1L
        params.localName = "Dpk"
        params.latinDescription = "latinName"
        params.localDescription = "localDescription"
        params.missionStatement = "missionStatement"
        params.registrationNumber = "registrationNumber"
        params.taxId = 1234
        params.needRevision = true
        serviceInstance.saveOrganization(params)
        then:
        def organizationCommand = serviceInstance.saveOrganization(params)
        println("test_saveOrganization done with data: ${organizationCommand}")
    }
}