package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.firm.lookups.CommitteeRole
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.enums.v1.GeneralStatus
import ps.police.common.utils.v1.PCPSessionUtils
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

import java.time.ZonedDateTime

@Integration
@Rollback
/**
 * integration test for Interview service
 */
class InterviewIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = Interview
        service_domain = InterviewService
        entity_name = "interview"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(Interview)
        filtered_parameters = ["id"]
        autocomplete_property = "description"
        primary_keys = ["id", 'encodedId']
        session_parameters = ["firmId": "firm.id"]
        once_save_properties = ["firm"]
        is_encrypted_delete = true
        is_virtual_delete = false
        is_remoting=true
    }

    def "test_changeInterviewStatus"() {
        setup:
        println "*************test_changeInterviewStatus***********"
        def testInstance = saveEntity()
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.encodedId = testInstance?.encodedId
        serviceInstance.changeInterviewStatus(parameterMap)
        then:
        def status = serviceInstance.changeInterviewStatus(parameterMap)
        status != null
        println "test_changeInterviewStatus done with status: ${status}"
    }

    def "test_addApplicantToInterview"() {
        setup:
        println "*************test_addApplicantToInterview***********"
        def testInstance = saveEntity()
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.interviewId = testInstance?.id
        serviceInstance.addApplicantToInterview(parameterMap)
        then:
        def status = serviceInstance.addApplicantToInterview(parameterMap)
        status != null
        println "test_addApplicantToInterview done with status: ${status}"
    }

    def "test_deleteApplicantFromInterview"() {
        setup:
        println "*************test_deleteApplicantFromInterview***********"
        def testInstance = saveEntity()
        when:
        GrailsParameterMap parameterMap = new GrailsParameterMap([:], new MockHttpServletRequest())
        parameterMap.encodedId = testInstance?.encodedId
        serviceInstance.deleteApplicantFromInterview(parameterMap)
        then:
        def status = serviceInstance.deleteApplicantFromInterview(parameterMap)
        status != null
        println "test_deleteApplicantFromInterview done with status: ${status}"
    }


}
