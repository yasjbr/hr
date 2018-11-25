package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Language requests between model and views.
 *@see LanguageService
 *@see FormatService
**/
class LanguageController {
    LanguageService languageService

    def autocomplete = {
        render text: (languageService.autoCompleteLanguage(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

