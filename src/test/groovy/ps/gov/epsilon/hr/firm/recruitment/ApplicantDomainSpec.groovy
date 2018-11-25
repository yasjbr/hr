package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus
import ps.gov.epsilon.hr.firm.Firm
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([Applicant, ApplicantStatusHistory, Interview, RecruitmentCycle, RecruitmentListEmployee, Vacancy])
@Domain([Applicant, Firm])
@TestMixin([HibernateTestMixin])
class ApplicantDomainSpec extends ConstraintUnitSpec {

    void "test Applicant all constraints"() {
        when:
        List<Map> constraints = [
                [field: "age", value: null, testResult: TestResult.FAIL],
                [field: "age", value: -2147483647, testResult: TestResult.FAIL],
                [field: "age", value: 2147483645, testResult: TestResult.PASS],

                [field: "applyingDate", value: null, testResult: TestResult.FAIL],
                [field: "applyingDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],

                [field: "archiveNumber", value: null, testResult: TestResult.PASS],
                [field: "archiveNumber", value: "archiveNumber", testResult: TestResult.PASS],
                [field: "archiveNumber", value: " ", testResult: TestResult.PASS],

                [field: "arrestHistories", value: null, testResult: TestResult.PASS],

                [field: "contactInfos", value: null, testResult: TestResult.PASS],

                [field: "educationEnfos", value: null, testResult: TestResult.PASS],

                [field: "fatherJobDesc", value: null, testResult: TestResult.PASS],
                [field: "fatherJobDesc", value: " ", testResult: TestResult.FAIL],
                [field: "fatherJobDesc", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "fatherJobDesc", value: getLongString(20000), testResult: TestResult.PASS],

                [field: "fatherProfessionType", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: Firm.build(), testResult: TestResult.PASS],

                [field: "height", value: null, testResult: TestResult.FAIL],
                [field: "height", value: -2147483647, testResult: TestResult.FAIL],
                [field: "height", value: 2147483645, testResult: TestResult.PASS],


                [field: "interview", value: ps.gov.epsilon.hr.firm.recruitment.Interview.build(), testResult: TestResult.PASS],
                [field: "locationId", value: null, testResult: TestResult.FAIL],

                [field: "motherJobDesc", value: null, testResult: TestResult.PASS],
                [field: "motherJobDesc", value: " ", testResult: TestResult.FAIL],
                [field: "motherJobDesc", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "motherJobDesc", value: getLongString(20000), testResult: TestResult.PASS],

                [field: "motherProfessionType", value: null, testResult: TestResult.FAIL],

                [field: "nominationParty", value: null, testResult: TestResult.PASS],
                [field: "nominationParty", value: " ", testResult: TestResult.FAIL],
                [field: "nominationParty", value: "nominationPartyA1c_", testResult: TestResult.FAIL],

                [field: "personId", value: null, testResult: TestResult.FAIL],

                [field: "previousJobDesc", value: null, testResult: TestResult.PASS],
                [field: "previousJobDesc", value: " ", testResult: TestResult.FAIL],
                [field: "previousJobDesc", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "previousJobDesc", value: getLongString(20000), testResult: TestResult.PASS],

                [field: "previousProfessionType", value: null, testResult: TestResult.PASS],

                [field: "recruitmentCycle", value: null, testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle.build(), testResult: TestResult.PASS],

                [field: "recruitmentListEmployee", value: null, testResult: TestResult.PASS],

                [field: "relativesInCivilianFirm", value: null, testResult: TestResult.PASS],

                [field: "relativesInCivilianFirm", value: " ", testResult: TestResult.FAIL],

                [field: "relativesInCivilianFirm", value: "relativesInCivilianFirmA1c", testResult: TestResult.PASS],

                [field: "relativesInMilitaryFirms", value: null, testResult: TestResult.PASS],

                [field: "relativesInMilitaryFirms", value: " ", testResult: TestResult.FAIL],

                [field: "statusHistory", value: null, testResult: TestResult.PASS],

                [field: "trackingInfo", value: null, testResult: TestResult.PASS],

                [field: "unstructuredLocation", value: null, testResult: TestResult.PASS],
                [field: "unstructuredLocation", value: " ", testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "unstructuredLocation", value: getLongString(20000), testResult: TestResult.PASS],

                [field: "vacancy", value: null, testResult: TestResult.PASS],

                [field: "weight", value: null, testResult: TestResult.FAIL],
                [field: "weight", value: -2147483647, testResult: TestResult.FAIL],
                [field: "weight", value: 2147483645, testResult: TestResult.PASS],
        ]
        then:
        validateObject(Applicant, constraints)
    }

    void "test custom validation for rejectionReason"() {

        Applicant applicant = Applicant.buildWithoutValidation()

        when:
        ApplicantStatusHistory applicantStatusHistorySuccess = ApplicantStatusHistory.buildWithoutValidation(applicant: applicant, applicantStatus: EnumApplicantStatus.ACCEPTED)
        applicant.applicantCurrentStatus = applicantStatusHistorySuccess
        then:
        applicant.validate()
        applicant.errors.allErrors?.size() == 0

        when:
        ApplicantStatusHistory applicantStatusHistoryFail = ApplicantStatusHistory.buildWithoutValidation(applicant: applicant, applicantStatus: EnumApplicantStatus.REJECTED)
        applicant.applicantCurrentStatus = applicantStatusHistoryFail
        applicant.rejectionReason == null

        then:
        !applicant.validate()
        applicant.errors.allErrors?.size() == 1

    }

}
