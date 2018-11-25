package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route HairFeature requests between model and views.
 *@see HairFeatureService
 *@see FormatService
**/
class HairFeatureController {
    HairFeatureService hairFeatureService

    def autocomplete = {
        render text: (hairFeatureService.autoCompleteHairFeature(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

