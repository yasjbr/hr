package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for RecruitmentCyclePhase service
 */
class RecruitmentCyclePhaseIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = RecruitmentCyclePhase
        service_domain = RecruitmentCyclePhaseService
        entity_name = "recruitmentCyclePhase"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(RecruitmentCyclePhase)
        filtered_parameters = ["id"]
        autocomplete_property = "requisitionAnnouncementStatus"
        primary_keys = ["id", "encodedId"]
        exclude_methods = ["save", "delete"]
    }
}