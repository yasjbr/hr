package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route EducationDegree requests between model and views.
 *@see EducationDegreeService
 *@see FormatService
**/
class EducationDegreeController {
    EducationDegreeService educationDegreeService

    def autocomplete = {
        render text: (educationDegreeService.autoCompleteEducationDegree(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

