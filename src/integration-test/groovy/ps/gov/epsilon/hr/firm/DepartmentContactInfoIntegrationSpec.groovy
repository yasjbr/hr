package ps.gov.epsilon.hr.firm

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for DepartmentContactInfo service
 */
class DepartmentContactInfoIntegrationSpec extends CommonIntegrationSpec {
    def setupSpec() {
        domain_class = DepartmentContactInfo
        service_domain = DepartmentContactInfoService
        entity_name = "departmentContactInfo"
        hashing_entity = "id"
        with_hashing_flag = false
        required_properties = PCPUtils.getRequiredFields(DepartmentContactInfo)
        filtered_parameters = ["id"]
        autocomplete_property = "value"
        primary_keys = ["id", 'encodedId']

    }
}