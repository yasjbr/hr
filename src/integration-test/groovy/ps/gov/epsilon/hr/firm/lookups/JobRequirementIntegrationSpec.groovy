package ps.gov.epsilon.hr.firm.lookups

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.mock.web.MockHttpServletRequest
import ps.gov.epsilon.hr.common.domains.v1.DescriptionInfo
import ps.gov.epsilon.hr.firm.Firm
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for JobRequirement service
 */
class JobRequirementIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = JobRequirement
        service_domain = JobRequirementService
        entity_name = "jobRequirement"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(JobRequirement)
        filtered_parameters = ["id"];
        autocomplete_property = "descriptionInfo.localName"
        primary_keys = ["id", 'encodedId']
        session_parameters = ["firmId": "firm.id"]
        is_virtual_delete=true
        is_encrypted_delete=false
        once_save_properties = ["firm"]
    }
}