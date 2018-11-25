package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for JoinedVacancyAdvertisement service
 */
class JoinedVacancyAdvertisementIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = JoinedVacancyAdvertisement
        service_domain = JoinedVacancyAdvertisementService
        entity_name = "joinedVacancyAdvertisement"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(JoinedVacancyAdvertisement)
        filtered_parameters = ["id"]
        autocomplete_property = "vacancy.job.descriptionInfo.localName"
        primary_keys = ["id", "encodedId"]
    }
}