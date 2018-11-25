package ps.police.pcore.v2.entity.person

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Person requests between model and views.
 *@see PersonService
 *@see FormatService
**/
class PersonController {
    PersonService personService

    def autocomplete = {
        render text: (personService.autoCompletePerson(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

