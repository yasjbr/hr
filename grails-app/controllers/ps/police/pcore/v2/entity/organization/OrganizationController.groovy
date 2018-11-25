package ps.police.pcore.v2.entity.organization

import grails.converters.JSON
import ps.police.common.utils.v1.PCPUtils

class OrganizationController {

    OrganizationService organizationService

    def autocomplete={
        render text: (organizationService.autoCompleteOrganization(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

    def getInstance = {
        render text: (organizationService.getOrganization(PCPUtils.convertParamsToSearchBean(params)) as JSON) , contentType: "application/json"
    }
}
