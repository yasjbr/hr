package ps.gov.epsilon.aoc.enums.v1

import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Created by muath on 21/03/18.
 */
enum EnumCorrespondenceType {
//    GENERAL_LIST("GENERAL_LIST", "AocListRecord"),
    PROMOTION_LIST('PROMOTION_LIST', 'aocPromotionList', 'promotionList', 'promotionListEmployee', 'PROMOTION', 'ROLE_AOC_PROMOTION_LIST', '2', null, true),
    ALLOWANCE_LIST('ALLOWANCE_LIST', 'aocAllowanceList', 'allowanceList', 'allowanceListEmployee', 'ALLONLIST', 'ROLE_AOC_ALLOWANCE_LIST'),
    CHILD_LIST('CHILD_LIST', 'aocChildList', 'childList', 'childListEmployee','CHILD' , 'ROLE_AOC_CHILD_LIST', '2', null, true),
    VIOLATION_LIST('VIOLATION_LIST', 'aocViolationList', 'violationList', 'violationListEmployee','VL', 'ROLE_AOC_VIOLATION_LIST'),
    MARITAL_STATUS_LIST('MARITAL_STATUS_LIST', 'aocMaritalStatusList', 'maritalStatusList', 'maritalStatusListEmployee', 'MARITAL', 'ROLE_AOC_MARITAL_STATUS_LIST', '2', null, true),
    DISCIPLINARY_LIST('DISCIPLINARY_LIST', 'aocDisciplinaryList', 'disciplinaryList', 'disciplinaryRecordJudgment', 'DISCIP', 'ROLE_AOC_DISCIPLINARY_LIST'),
    EXTERNAL_TRANSFER_LIST('EXTERNAL_TRANSFER_LIST', 'aocExternalTransferList', 'externalTransferList', 'externalTransferListEmployee', 'EXTRNLIST', 'ROLE_AOC_EXTERNAL_TRANSFER_LIST', '2', null, true),
    DISPATCH_LIST('DISPATCH_LIST', 'aocDispatchList', 'dispatchList', 'dispatchListEmployee' ,'DISPATCH', 'ROLE_AOC_DISPATCH_LIST'),
    EVALUATION_LIST('EVALUATION_LIST', 'aocEvaluationList', 'evaluationList', 'evaluationListEmployee', 'EVALUATION', 'ROLE_AOC_EVALUATION_LIST'),
    SUSPENSION_LIST('SUSPENSION_LIST', 'aocSuspensionList', 'suspensionList', 'suspensionListEmployee', 'SUSPENSION', 'ROLE_AOC_SUSPENSION_LIST'),
    VACATION_LIST('VACATION_LIST', 'aocVacationList', 'vacationList', 'vacationListEmployee', 'VACATION', 'ROLE_AOC_VACATION_LIST'),
    END_OF_SERVICE_LIST('END_OF_SERVICE_LIST', 'aocEndOfServiceList', 'serviceList', 'serviceListEmployee', 'SERVICELIST', 'ROLE_AOC_END_OF_SERVICE_LIST', '2', null, true),
    RETURN_TO_SERVICE_LIST('RETURN_TO_SERVICE_LIST', 'aocReturnToServiceList', 'serviceList', 'serviceListEmployee', 'SERVICELIST', 'ROLE_AOC_RETURN_TO_SERVICE_LIST', '2', null, true),
    RETURN_FROM_ABSENCE_LIST('RETURN_FROM_ABSENCE_LIST', 'aocReturnFromAbsenceList', 'returnFromAbsenceList', 'returnFromAbsenceListEmployee', 'RAL', 'ROLE_AOC_RETURN_FROM_ABSENCE_LIST'),
    LOAN_LIST('LOAN_LIST', 'aocLoanList', 'loanList', 'loanListPerson', 'LOAN', 'ROLE_AOC_LOAN_LIST', '1', 'loanRequest', true),
    LOAN_NOTICE_REPLAY_LIST('LOAN_NOTICE_REPLAY_LIST', 'aocLoanNoticeReplayList', 'loanNoticeReplayList', 'loanNominatedEmployees', 'LOANNOTICE', 'ROLE_AOC_LOAN_NOTICE_REPLAY_LIST', '1', 'loanNoticeReplayRequest', true),

    final String value;
    final String listDomain;
    final String hrListDomain;
    final String hrListEmployee;
    final String hrRequestDomain;
    final String roles;
    final String code;
    final String createSteps;
    final Boolean needsAdditionalInfoOnApproval

    EnumCorrespondenceType(String value, String listDomain, String hrListDomain, String hrListEmployee, String code, String roles, String createSteps = 2, String hrRequestDomain = null, needsAdditionalInfoOnApproval=false) {
        this.value = value
        this.code = code
        this.listDomain = listDomain
        this.hrListDomain = hrListDomain
        this.hrListEmployee = hrListEmployee
        this.roles = roles
        this.createSteps = createSteps
        this.hrRequestDomain = hrRequestDomain
        this.needsAdditionalInfoOnApproval= needsAdditionalInfoOnApproval
    }

    String toString() {
        value;
    }

    String getValue() {
        return value
    }

    String getListDomain() {
        return listDomain
    }

    String getHrListDomain() {
        return hrListDomain
    }

    String getHrListEmployee() {
        return hrListEmployee
    }

    /**
     * filtered enum values based on user roles.
     * @return correspondenceTypeList
     */
    static Collection<EnumCorrespondenceType> getPermetedValues() {
        Collection<EnumCorrespondenceType> correspondenceTypeList = []
        if (grails.util.Environment.current == grails.util.Environment.DEVELOPMENT) {
            correspondenceTypeList = EnumCorrespondenceType.values()
        } else {
            EnumCorrespondenceType?.values()?.each { EnumCorrespondenceType correspondenceType ->
                if (SpringSecurityUtils?.ifAnyGranted(correspondenceType.roles)) {
                    correspondenceTypeList.add(correspondenceType)
                }
            }
        }
        return correspondenceTypeList
    }
}