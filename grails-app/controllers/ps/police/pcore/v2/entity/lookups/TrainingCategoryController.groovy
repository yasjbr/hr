package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route TrainingCategory requests between model and views.
 *@see TrainingCategoryService
 *@see FormatService
**/
class TrainingCategoryController {
    TrainingCategoryService trainingCategoryService

    def autocomplete = {
        render text: (trainingCategoryService.autoCompleteTrainingCategory(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

