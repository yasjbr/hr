package ps.gov.epsilon.hr.common

import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceListService
import ps.gov.epsilon.hr.firm.request.RequestChangesHandlerService

class InstantOperationsJob {

    RequestChangesHandlerService requestChangesHandlerService
    AocCorrespondenceListService aocCorrespondenceListService

    static triggers = {
        simple name: 'InstantSimpleTrigger', startDelay: 200000, repeatInterval: 60000
    }

    def concurrent = false

    def execute() {
        try{
            // handle request changes
            requestChangesHandlerService.handleRequestChanges()
        }catch (Exception ex){
            log.warn("Error in handling request changes - " + ex.message)
        }

        try{
            // handle hr lists
            aocCorrespondenceListService.handleSubmittedHrLists()
        }catch (Exception ex){
            log.warn("Error in handling submitted lists - " + ex.message)
        }
    }
}