package ps.gov.epsilon.hr.firm.recruitment

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for JoinedInterviewCommitteeRole service
 */
class JoinedInterviewCommitteeRoleIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  JoinedInterviewCommitteeRole
        service_domain =  JoinedInterviewCommitteeRoleService
        entity_name = "joinedInterviewCommitteeRole"
        required_properties = PCPUtils.getRequiredFields( JoinedInterviewCommitteeRole)
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}