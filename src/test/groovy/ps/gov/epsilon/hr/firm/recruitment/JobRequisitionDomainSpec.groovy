package ps.gov.epsilon.hr.firm.recruitment

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([JobRequisition,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.lookups.Job,ps.gov.epsilon.hr.firm.lookups.JobType,ps.gov.epsilon.hr.firm.lookups.MilitaryRank,ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle,ps.gov.epsilon.hr.firm.Department,ps.gov.epsilon.hr.firm.Department])
@Domain([JobRequisition])
@TestMixin([HibernateTestMixin])
class JobRequisitionDomainSpec extends ConstraintUnitSpec {

    void "test JobRequisition all constraints"() {
        when:
        List<Map> constraints = [
                [field: "educationDegrees", value: null, testResult: TestResult.PASS],
                [field: "educationMajors", value: null, testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "fromAge", value: null, testResult: TestResult.PASS],
                [field: "fromAge", value: 5, testResult: TestResult.PASS],
                [field: "fromAge", value: -1, testResult: TestResult.PASS],
                [field: "fromGovernorates", value: null, testResult: TestResult.PASS],
                [field: "fromTall", value: null, testResult: TestResult.PASS],
                [field: "fromTall", value: 5, testResult: TestResult.PASS],
                [field: "fromTall", value: -1, testResult: TestResult.PASS],
                [field: "fromWeight", value: null, testResult: TestResult.PASS],
                [field: "fromWeight", value: 5, testResult: TestResult.PASS],
                [field: "fromWeight", value: -1, testResult: TestResult.PASS],
                [field: "fulfillFromDate", value: null, testResult: TestResult.FAIL],
                [field: "fulfillFromDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "fulfillToDate", value: null, testResult: TestResult.FAIL],
                [field: "fulfillToDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "governorates", value: null, testResult: TestResult.PASS],
                [field: "inspectionCategories", value: null, testResult: TestResult.PASS],
                [field: "jobDescription", value: null, testResult: TestResult.PASS],
                [field: "jobDescription", value: " ", testResult: TestResult.FAIL],
                [field: "jobDescription", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "jobDescription", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "job", value: null, testResult: TestResult.FAIL],
                [field: "job", value: ps.gov.epsilon.hr.firm.lookups.Job.build(), testResult: TestResult.PASS],
                [field: "jobType", value: null, testResult: TestResult.FAIL],
                [field: "jobType", value: ps.gov.epsilon.hr.firm.lookups.JobType.build(), testResult: TestResult.PASS],
                [field: "maritalStatusId", value: null, testResult: TestResult.PASS],
                [field: "note", value: null, testResult: TestResult.PASS],
                [field: "note", value: " ", testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "note", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "numberOfApprovedPositions", value: null, testResult: TestResult.PASS],
                [field: "numberOfPositions", value: null, testResult: TestResult.FAIL],
                [field: "proposedRank", value: null, testResult: TestResult.PASS],
                [field: "proposedRank", value: ps.gov.epsilon.hr.firm.lookups.MilitaryRank.build(), testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: null, testResult: TestResult.PASS],
                [field: "recruitmentCycle", value: ps.gov.epsilon.hr.firm.recruitment.RecruitmentCycle.build(), testResult: TestResult.PASS],
                [field: "requestDate", value: null, testResult: TestResult.FAIL],
                [field: "requestDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "requestedByDepartment", value: null, testResult: TestResult.PASS],
                [field: "requestedByDepartment", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "requestedForDepartment", value: null, testResult: TestResult.FAIL],
                [field: "requestedForDepartment", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "requisitionStatus", value: null, testResult: TestResult.FAIL],
                [field: "toAge", value: 5, testResult: TestResult.PASS],
                [field: "toAge", value: -1, testResult: TestResult.PASS],
                [field: "toTall", value: 5, testResult: TestResult.PASS],
                [field: "toTall", value: -1, testResult: TestResult.PASS],
                [field: "toWeight", value: 5, testResult: TestResult.PASS],
                [field: "toWeight", value: -1, testResult: TestResult.PASS],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "workExperience", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(JobRequisition,constraints)
    }
}

