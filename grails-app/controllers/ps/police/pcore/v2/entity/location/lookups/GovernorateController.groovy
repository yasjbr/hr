package ps.police.pcore.v2.entity.location.lookups

import grails.converters.JSON
import grails.gorm.PagedResultList
import ps.police.common.beans.v1.DeleteBean
import ps.police.common.utils.v1.PCPUtils

import static org.springframework.http.HttpStatus.NOT_FOUND

/**
 *<h1>Purpose</h1>
 * Route Governorate requests between model and views.
 *@see GovernorateService
**/
class GovernorateController  {

    GovernorateService governorateService

    def autocomplete = {
        render text: (governorateService.autoCompleteGovernorate(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

