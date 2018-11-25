package ps.police.pcore.v2.entity.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Currency requests between model and views.
 *@see CurrencyService
 *@see FormatService
**/
class CurrencyController {
    CurrencyService currencyService

    def autocomplete = {
        render text: (currencyService.autoCompleteCurrency(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
    
}

