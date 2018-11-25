package ps.gov.epsilon.hr.enums.v1

/**
 * Created by mkharma on 06/03/17.
 */
enum EnumRequestType {

    GENERAL_REQUEST("GENERAL_REQUEST", "bordersSecurityCoordination", 'requestService'),

    VACATION_REQUEST("VACATION_REQUEST", "vacationRequest", "vacationRequestService"),
    REQUEST_FOR_VACATION_EXTENSION("REQUEST_FOR_VACATION_EXTENSION", "vacationRequest", "vacationRequestService", EnumRequestCategory.EXTEND),
    REQUEST_FOR_EDIT_VACATION("REQUEST_FOR_EDIT_VACATION", "vacationRequest", "vacationRequestService", EnumRequestCategory.EDIT),
    REQUEST_FOR_VACATION_STOP("REQUEST_FOR_VACATION_STOP", "vacationRequest", "vacationRequestService", EnumRequestCategory.STOP),
    REQUEST_FOR_VACATION_CANCEL("REQUEST_FOR_VACATION_CANCEL", "vacationRequest", "vacationRequestService", EnumRequestCategory.CANCEL),
    //    RETURN_FROM_VACATION("RETURN_FROM_VACATION"),

    CHILD_REQUEST("CHILD_REQUEST", "childRequest", "childRequestService"),
    CHILD_EDIT_REQUEST("CHILD_EDIT_REQUEST", "childRequest", "childRequestService", EnumRequestCategory.EDIT),
    CHILD_CANCEL_REQUEST("CHILD_CANCEL_REQUEST", "childRequest", "childRequestService", EnumRequestCategory.CANCEL),

    MARITAL_STATUS_REQUEST("MARITAL_STATUS_REQUEST", "maritalStatusRequest", "maritalStatusRequestService"),
    MARITAL_STATUS_EDIT_REQUEST("MARITAL_STATUS_EDIT_REQUEST", "maritalStatusRequest", "maritalStatusRequestService", EnumRequestCategory.EDIT),
    MARITAL_STATUS_CANCEL_REQUEST("MARITAL_STATUS_CANCEL_REQUEST", "maritalStatusRequest", "maritalStatusRequestService", EnumRequestCategory.CANCEL),

    //    UPDATE_NAME("UPDATE_NAME"),
//    UPDATE_LANGUAGE("UPDATE_LANGUAGE"),
//    UPDATE_NATIONALITY("UPDATE_NATIONALITY"),
//    UPDATE_TRAINING_RECORD("UPDATE_TRAINING_RECORD"),
//    UPDATE_EMPLOYMENT_RECORD("UPDATE_EMPLOYMENT_RECORD"),
//    UPDATE_EDUCATION_RECORD("UPDATE_EDUCATION_RECORD"),
//    UPDATE_LEGAL_IDENTIFIER("UPDATE_LEGAL_IDENTIFIER"),
//    UPDATE_COUNTRY_VISIT("UPDATE_COUNTRY_VISIT"),
//    UPDATE_ADDRESS("UPDATE_ADDRESS"),

//    EDUCATIONAL_PROFILE_MODIFICATION_REQUEST("EDUCATIONAL_PROFILE_MODIFICATION_REQUEST"),
//    MILITARY_RANK_MODIFICATION_REQUEST("MILITARY_RANK_MODIFICATION_REQUEST"),

    ALLOWANCE_REQUEST("ALLOWANCE_REQUEST", "allowanceRequest", "allowanceRequestService"),
    ALLOWANCE_RESUMPTION_REQUEST("ALLOWANCE_RESUMPTION_REQUEST", "allowanceRequest", "allowanceRequestService"),
    ALLOWANCE_CONTINUE_REQUEST("ALLOWANCE_CONTINUE_REQUEST", "allowanceRequest", "allowanceRequestService", EnumRequestCategory.EXTEND),
    ALLOWANCE_STOP_REQUEST("ALLOWANCE_STOP_REQUEST", "allowanceRequest", "allowanceRequestService", EnumRequestCategory.STOP),
    //    ALLOWANCE_EXTEND_REQUEST("ALLOWANCE_EXTEND_REQUEST", "allowanceExtendRequest"),
    ALLOWANCE_EDIT_REQUEST("ALLOWANCE_EDIT_REQUEST", "allowanceRequest", "allowanceRequestService", EnumRequestCategory.EDIT),
    ALLOWANCE_CANCEL_REQUEST("ALLOWANCE_CANCEL_REQUEST", "allowanceRequest", "allowanceRequestService", EnumRequestCategory.CANCEL),

    //    BONUS_REQUEST("BONUS_REQUEST"),

    DISCIPLINARY_REQUEST("DISCIPLINARY_REQUEST", "disciplinaryRequest", "disciplinaryRequestService"),
    PETITION_REQUEST("PETITION_REQUEST", "petitionRequest", "petitionRequestService"),
    RETURN_FROM_ABSENCE_REQUEST("RETURN_FROM_ABSENCE_REQUEST", "returnFromAbsenceRequest", "returnFromAbsenceRequestService"),
    //    DISCIPLINARY_RECORD_CANCELLATION_REQUEST("DISCIPLINARY_RECORD_CANCELLATION_REQUEST"),
//    DISCIPLINARY_BECAUSE_OF_ABSENCE("DISCIPLINARY_BECAUSE_OF_ABSENCE"),

//    UPDATE_EMPLOYEE_RANK("UPDATE_EMPLOYEE_RANK"),

    UPDATE_MILITARY_RANK_TYPE("UPDATE_MILITARY_RANK_TYPE", "updateMilitaryRankRequest", "updateMilitaryRankRequestService"),
    UPDATE_MILITARY_RANK_CLASSIFICATION("UPDATE_MILITARY_RANK_CLASSIFICATION", "updateMilitaryRankRequest", "updateMilitaryRankRequestService"),

    SITUATION_SETTLEMENT("SITUATION_SETTLEMENT", "promotionRequest", 'promotionRequestService'),
    PERIOD_SETTLEMENT_OLD_ARREST("PERIOD_SETTLEMENT_OLD_ARREST", "promotionRequest", 'promotionRequestService'),
    PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD("PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD", "promotionRequest", 'promotionRequestService'),
    PERIOD_SETTLEMENT("PERIOD_SETTLEMENT", "promotionRequest", 'promotionRequestService'),
    PERIOD_SETTLEMENT_CURRENT_ARREST("PERIOD_SETTLEMENT_CURRENT_ARREST", "promotionRequest", 'promotionRequestService'),
    EXCEPTIONAL_REQUEST("EXCEPTIONAL_REQUEST", "promotionRequest", 'promotionRequestService'),
    ELIGIBLE_REQUEST("ELIGIBLE_REQUEST", "promotionRequest", 'promotionRequestService'),
    INTERNAL_TRANSFER_REQUEST("INTERNAL_TRANSFER_REQUEST", "internalTransferRequest", "internalTransferRequestService"),
    EXTERNAL_TRANSFER_REQUEST("EXTERNAL_TRANSFER_REQUEST", "externalTransferRequest", "externalTransferRequestService"),


    //    ASSIGN_EMPLOYEE("ASSIGN_EMPLOYEE"),
//    GROUP_TRANSFER("GROUP_TRANSFER"),
//    ISSUING_CARD("ISSUING_CARD"),

    END_OF_SERVICE("END_OF_SERVICE", "employmentServiceRequest", "employmentServiceRequestService"),
    RETURN_TO_SERVICE("RETURN_TO_SERVICE", "employmentServiceRequest", "employmentServiceRequestService"),

    DISPATCH_REQUEST("DISPATCH_REQUEST", "dispatchRequest", "dispatchRequestService"),
    DISPATCH_STOP_REQUEST("DISPATCH_STOP_REQUEST", "dispatchRequest", "dispatchRequestService", EnumRequestCategory.STOP),
    DISPATCH_EXTEND_REQUEST("DISPATCH_EXTEND_REQUEST", "dispatchRequest", "dispatchRequestService", EnumRequestCategory.EXTEND),
    DISPATCH_EDIT_REQUEST("DISPATCH_EDIT_REQUEST", "dispatchRequest", "dispatchRequestService", EnumRequestCategory.EDIT),
    DISPATCH_CANCEL_REQUEST("DISPATCH_CANCEL_REQUEST", "dispatchRequest", "dispatchRequestService", EnumRequestCategory.CANCEL),


    SUSPENSION("SUSPENSION", "suspensionRequest", "suspensionRequestService"),
    REQUEST_FOR_SUSPENSION_EXTENSION("REQUEST_FOR_SUSPENSION_EXTENSION", "suspensionExtensionRequest", "suspensionRequestService"),

    LOAN_REQUEST("LOAN_REQUEST", "loanRequest", "loanRequestService"),
    LOAN_NOTICE_REPLAY_REQUEST("LOAN_NOTICE_REPLAY_REQUEST", "loanNoticeReplayRequest", "loanNoticeReplayRequestService")

    final String value;
    final String domain;
    final EnumRequestCategory requestCategory;
    final String serviceName;

    EnumRequestType(String value, String domain, String serviceName, EnumRequestCategory requestCategory = EnumRequestCategory.ORIGINAL) {
        this.value = value;
        this.domain = domain;
        this.requestCategory = requestCategory
        this.serviceName = serviceName
    }


    String toString() {
        value;
    }

    String getKey() {
        name()
    }

}

