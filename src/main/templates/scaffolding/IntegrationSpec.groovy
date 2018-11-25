<%=packageName ? "package ${packageName}" : ''%>

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import ps.police.common.utils.v1.PCPUtils
import ps.police.test.utils.CommonIntegrationSpec

@Integration
@Rollback
/**
 * integration test for ${className} service
 */
class ${className}IntegrationSpec extends CommonIntegrationSpec {
    def setupSpec(){
        domain_class =  ${className}
        service_domain =  ${className}Service
        entity_name = "${propertyName}"
        required_properties = PCPUtils.getRequiredFields( ${className})
        filtered_parameters = ["descriptionInfo.localName"];
        autocomplete_property = "descriptionInfo.localName"
    }
}