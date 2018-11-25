package ps.police.pcore.v2.entity.legalIdentifier

import ps.police.common.utils.v1.PCPUtils

class LegalIdentifierLevelController {

    LegalIdentifierLevelService legalIdentifierLevelService

    def autocomplete = {
        render text: (legalIdentifierLevelService.autoCompleteLegalIdentifierLevel(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }
}
