package ps.gov.epsilon.hr.firm.promotion

import ps.gov.epsilon.hr.enums.v1.EnumRequestType
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankClassification
import ps.gov.epsilon.hr.firm.lookups.MilitaryRankType
import ps.gov.epsilon.hr.firm.request.Request
import ps.police.config.v1.Constants
import ps.police.userTypes.persistent.dateTime.PersistentDocumentaryDate

import java.time.ZonedDateTime

/**
 *<h1>Purpose</h1>
 * This request is initialized when an employee demands to change his/her military rank type like "Engineer, honored"
 * <h1>Usage</h1>
 * Used as to represent when the employee demands to change his/her military rank type
 * **/


class UpdateMilitaryRankRequest extends Request {

    static auditable = true

    // the old military rank type
    MilitaryRankType oldRankType;
    //the new military rank type
    MilitaryRankType newRankType;
    // the old military rank Classification
    MilitaryRankClassification oldRankClassification;
    //the new military rank Classification
    MilitaryRankClassification newRankClassification;
    //the new military rank type due date
    ZonedDateTime dueDate
    // the reason of changing the military rank type
    String dueReason



    public UpdateMilitaryRankRequest() {
        requestType = EnumRequestType.UPDATE_MILITARY_RANK_TYPE
//        requestType = EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION
    }
    static  constraints = {

        oldRankType (nullable: true,widget:"autocomplete", validator: { value, object,errors ->
            if (object?.requestType==EnumRequestType.UPDATE_MILITARY_RANK_TYPE &&
                    !value && !object?.newRankType)
                errors.reject('UpdateMilitaryRankRequest.newOrOldRankType.error.required')
            return true
        })

        newRankType (nullable: true,widget:"autocomplete"/*, validator: { value, object,errors ->
            if (object?.requestType==EnumRequestType.UPDATE_MILITARY_RANK_TYPE &&
                    !value && !object?.oldRankType)
                errors.reject('UpdateMilitaryRankRequest.newRankType.error.required')
            return true
        }*/)


        oldRankClassification (nullable: true,widget:"autocomplete", validator: { value, object,errors ->
            if (object?.requestType==EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION &&
                    !value && !object?.newRankClassification)
                errors.reject('UpdateMilitaryRankRequest.newOrOldRankClassification.error.required')
            return true
        })

        newRankClassification (nullable: true,widget:"autocomplete"/*, validator: { value, object,errors ->
            if (object?.requestType==EnumRequestType.UPDATE_MILITARY_RANK_CLASSIFICATION &&
                    !value && !object?.oldRankClassification)
                errors.reject('UpdateMilitaryRankRequest.newRankClassification.error.required')
            return true
        }*/)

        dueReason(Constants.DESCRIPTION_NULLABLE)

        employee nullable: false
        currentEmploymentRecord nullable: false
        currentEmployeeMilitaryRank nullable: false
    }

    static mapping = {
        dueDate type: PersistentDocumentaryDate, {
            column name: 'due_date_datetime'
            column name: 'due_date_date_tz'
        }
    }
}