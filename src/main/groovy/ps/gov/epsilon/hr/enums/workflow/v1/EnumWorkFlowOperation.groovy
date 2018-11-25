package ps.gov.epsilon.hr.enums.workflow.v1

/**
 * Created by wassi on 19/12/17.
 */

enum EnumWorkFlowOperation {
    DEFAULT_NEED_AOC_APPROVAL("DefaultNeedAocApproval"),
    DEFAULT_DOES_NOT_NEED_AOC_APPROVAL("DefaultDoesNotNeedAocApproval"),
    ALLOWANCE_REQUEST("ps.gov.epsilon.hr.firm.allowance.AllowanceRequest"),
    ALLOWANCE_EXTEND_REQUEST("ps.gov.epsilon.hr.firm.allowance.AllowanceRequest", 'ALLOWANCE_CONTINUE_REQUEST'),
    ALLOWANCE_STOP_REQUEST("ps.gov.epsilon.hr.firm.allowance.AllowanceRequest", 'ALLOWANCE_STOP_REQUEST'),
    ALLOWANCE_EDIT_REQUEST("ps.gov.epsilon.hr.firm.allowance.AllowanceRequest", 'ALLOWANCE_EDIT_REQUEST'),
    ALLOWANCE_CANCEL_REQUEST("ps.gov.epsilon.hr.firm.allowance.AllowanceRequest", 'ALLOWANCE_CANCEL_REQUEST'),

    CHILD_REQUEST("ps.gov.epsilon.hr.firm.child.ChildRequest"),
    CHILD_CANCEL_REQUEST("ps.gov.epsilon.hr.firm.child.ChildRequest",'CHILD_CANCEL_REQUEST'),
    CHILD_EDIT_REQUEST("ps.gov.epsilon.hr.firm.child.ChildRequest",'CHILD_EDIT_REQUEST'),

    MARITAL_STATUS_REQUEST("ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest"),
    MARITAL_STATUS_CANCEL_REQUEST("ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest",'MARITAL_STATUS_CANCEL_REQUEST'),
    MARITAL_STATUS_EDIT_REQUEST("ps.gov.epsilon.hr.firm.maritalStatus.MaritalStatusRequest",'MARITAL_STATUS_EDIT_REQUEST'),

    DISCIPLINARY_REQUEST("ps.gov.epsilon.hr.firm.disciplinary.DisciplinaryRequest"),

    DISPATCH_REQUEST("ps.gov.epsilon.hr.firm.dispatch.DispatchRequest"),
    DISPATCH_EXTEND_REQUEST("ps.gov.epsilon.hr.firm.dispatch.DispatchRequest", 'DISPATCH_EXTEND_REQUEST'),
    DISPATCH_STOP_REQUEST("ps.gov.epsilon.hr.firm.dispatch.DispatchRequest", 'DISPATCH_STOP_REQUEST'),
    DISPATCH_EDIT_REQUEST("ps.gov.epsilon.hr.firm.dispatch.DispatchRequest", 'DISPATCH_EDIT_REQUEST'),
    DISPATCH_CANCEL_REQUEST("ps.gov.epsilon.hr.firm.dispatch.DispatchRequest", 'DISPATCH_CANCEL_REQUEST'),

    LOAN_NOTICE_REPLAY_REQUEST("ps.gov.epsilon.hr.firm.loan.LoanNoticeReplayRequest"),
    LOAN_REQUEST("ps.gov.epsilon.hr.firm.loan.LoanRequest"),
    //    LOAN_REQUEST_RELATED_PERSON("ps.gov.epsilon.hr.firm.loan.LoanRequestRelatedPerson")
    MILITARY_CARD_REQUEST("ps.gov.epsilon.hr.firm.militaryCard.MilitaryCardRequest"),
    PROMOTION_REQUEST("ps.gov.epsilon.hr.firm.promotion.PromotionRequest"),
    UPDATE_MILITARY_RANK_REQUEST("ps.gov.epsilon.hr.firm.promotion.UpdateMilitaryRankRequest"),
    BORDERS_SECURITY_COORDINATION("ps.gov.epsilon.hr.firm.request.BordersSecurityCoordination"),
    REQUEST_CANCELLATION("ps.gov.epsilon.hr.firm.request.RequestCancellation"),
    SUSPENSION_EXTENSION_REQUEST("ps.gov.epsilon.hr.firm.suspension.SuspensionExtensionRequest"),
    SUSPENSION_REQUEST("ps.gov.epsilon.hr.firm.suspension.SuspensionRequest"),
    EXTERNAL_TRANSFER_REQUEST("ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequest"),
    INTERNAL_TRANSFER_REQUEST("ps.gov.epsilon.hr.firm.transfer.InternalTransferRequest"),
    VACATION_REQUEST("ps.gov.epsilon.hr.firm.vacation.VacationRequest"),
    VACATION_EXTENSION_REQUEST("ps.gov.epsilon.hr.firm.vacation.VacationRequest", "REQUEST_FOR_VACATION_EXTENSION"),
    STOP_VACATION_REQUEST("ps.gov.epsilon.hr.firm.vacation.VacationRequest", "REQUEST_FOR_VACATION_STOP"),
    EDIT_VACATION_REQUEST("ps.gov.epsilon.hr.firm.vacation.VacationRequest", "REQUEST_FOR_EDIT_VACATION"),
    CANCEL_VACATION_REQUEST("ps.gov.epsilon.hr.firm.vacation.VacationRequest", "REQUEST_FOR_VACATION_CANCEL"),
    EMPLOYMENT_SERVICE_REQUEST("ps.gov.epsilon.hr.firm.employmentService.EmploymentServiceRequest"),
    RETURN_FROM_ABSENCE_REQUEST("ps.gov.epsilon.hr.firm.absence.ReturnFromAbsenceRequest"),
    PETITION_REQUEST("ps.gov.epsilon.hr.firm.disciplinary.PetitionRequest"),


    AOC_CORRESPONDENCE_LIST("ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList")


    final String value;
    final String requestType;

    EnumWorkFlowOperation(String value, String requestType = null) {
        this.value = value;
        this.requestType = requestType
    }

    String toString() {
        value;
    }

}