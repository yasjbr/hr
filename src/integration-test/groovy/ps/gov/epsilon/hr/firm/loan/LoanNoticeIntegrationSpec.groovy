package ps.gov.epsilon.hr.firm.loan

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for LoanNotice service
 */
class LoanNoticeIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class = LoanNotice
        service_domain = LoanNoticeService
        entity_name = "loanNotice"
        required_properties = PCPUtils.getRequiredFields(LoanNotice)
        filtered_parameters = ["requesterOrganizationId"];
        autocomplete_property = "requesterOrganizationId"
        exclude_methods = ["autocomplete"]
        primary_keys = ["encodedId"]
        session_parameters = ["firmId": "firm.id","firm.id": "firm.id"]
        once_save_properties = ["firm"]
        is_virtual_delete = true
    }
}