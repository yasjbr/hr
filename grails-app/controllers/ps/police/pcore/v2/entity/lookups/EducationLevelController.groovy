package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route EducationLevel requests between model and views.
 *@see EducationLevelService
 *@see FormatService
**/
class EducationLevelController {
    EducationLevelService educationLevelService

    def autocomplete = {
        render text: (educationLevelService.autoCompleteEducationLevel(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

