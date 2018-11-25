package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route District requests between model and views.
 *@see DistrictService
**/
class DistrictController  {

    DistrictService districtService

    def autocomplete = {
        render text: (districtService.autoCompleteDistrict(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

