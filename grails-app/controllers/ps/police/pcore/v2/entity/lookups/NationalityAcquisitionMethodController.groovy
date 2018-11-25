package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route NationalityAcquisitionMethod requests between model and views.
 *@see NationalityAcquisitionMethodService
 *@see FormatService
**/
class NationalityAcquisitionMethodController {
    NationalityAcquisitionMethodService nationalityAcquisitionMethodService

    def autocomplete = {
        render text: (nationalityAcquisitionMethodService.autoCompleteNationalityAcquisitionMethod(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

