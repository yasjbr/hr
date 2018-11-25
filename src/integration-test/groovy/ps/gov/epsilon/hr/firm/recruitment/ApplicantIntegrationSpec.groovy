package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for Applicant service
 */
class ApplicantIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = Applicant
        service_domain = ApplicantService
        entity_name = "applicant"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(Applicant)
        filtered_parameters = ["id"]
        autocomplete_property = "personName"
        primary_keys = ["id", 'encodedId']
        exclude_methods = ["delete"]
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
    }

    def "test_getApplicantStatus"() {
        setup:
        println("******************test_getApplicantStatus*****************")
        when:
        EnumApplicantStatus status = EnumApplicantStatus.INITIAL_REVIEW
        serviceInstance.getApplicantStatus(status)
        then:
        def map = serviceInstance.getApplicantStatus(status)
        map != [:]
        println "test_getApplicantStatus done with data: ${map}"
    }

   /* def "test_getRemoteValues"() {
        setup:
        println("******************test_getRemoteValues*****************")
        def testInstance = saveEntity()
        when:
        serviceInstance.getRemoteValues(testInstance)
        then:
        def instance = serviceInstance.getRemoteValues(testInstance)
        instance != [:]
        println "test_getRemoteValues done with data: ${instance}"
    }*/

    def "test_getPersonInstanceWithRemotingValues"() {
        setup:
        println("******************test_getPersonInstanceWithRemotingValues*****************")
        def testInstance = saveEntity()

        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.personId = testInstance?.personId
        serviceInstance.getPersonInstanceWithRemotingValues(parameterMap)
        then:
        def instance = serviceInstance.getPersonInstanceWithRemotingValues(parameterMap)
        instance != [:]
        println "test_getPersonInstanceWithRemotingValues done with data: ${instance}"
    }


}