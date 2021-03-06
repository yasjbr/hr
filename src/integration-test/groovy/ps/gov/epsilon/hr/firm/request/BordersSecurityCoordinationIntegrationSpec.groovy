package ps.gov.epsilon.hr.firm.request

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
 * integration test for BordersSecurityCoordination service
 */
class BordersSecurityCoordinationIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = BordersSecurityCoordination
        service_domain = BordersSecurityCoordinationService
        entity_name = "bordersSecurityCoordination"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(BordersSecurityCoordination)
        filtered_parameters = ["id"]
        autocomplete_property = "transientData.borderCrossingPointDTO.descriptionInfo.localName"
        primary_keys = ["id", "encodedId"]
        exclude_methods = ["delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }


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

}