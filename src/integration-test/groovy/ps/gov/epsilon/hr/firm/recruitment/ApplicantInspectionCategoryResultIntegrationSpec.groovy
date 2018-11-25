package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ApplicantInspectionCategoryResult service
 */
class ApplicantInspectionCategoryResultIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = ApplicantInspectionCategoryResult
        service_domain = ApplicantInspectionCategoryResultService
        entity_name = "applicantInspectionCategoryResult"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(ApplicantInspectionCategoryResult)
        filtered_parameters = ["id"]
        autocomplete_property = "applicant.personName"
        primary_keys = ["id", 'encodedId']
    }
}