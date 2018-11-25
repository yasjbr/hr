package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

class CountryController {

    CountryService countryService

    def autocomplete = {
        render text: (countryService.autoCompleteCountry(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"

    }
}
