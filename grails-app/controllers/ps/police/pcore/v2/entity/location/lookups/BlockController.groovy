package ps.police.pcore.v2.entity.location.lookups

import ps.police.common.utils.v1.PCPUtils

/**
 *<h1>Purpose</h1>
 * Route Block requests between model and views.
 *@see BlockService
**/
class BlockController  {

    BlockService blockService

    def autocomplete = {
        render text: (blockService.autoCompleteBlock(PCPUtils.convertParamsToSearchBean(params))), contentType: "application/json"
    }

}

