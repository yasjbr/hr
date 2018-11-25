package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ExternalTransferRequest,ps.gov.epsilon.hr.firm.promotion.EmployeePromotion,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.Firm,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.Department])
@Domain([ExternalTransferRequest])
@TestMixin([HibernateTestMixin])
class ExternalTransferRequestDomainSpec extends ConstraintUnitSpec {

    void "test ExternalTransferRequest all constraints"() {
        when:
        List<Map> constraints = [
                [field: "clearanceDate", value: null, testResult: TestResult.FAIL],
                [field: "clearanceDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "clearanceNote", value: null, testResult: TestResult.PASS],
                [field: "clearanceNote", value: " ", testResult: TestResult.FAIL],
                [field: "clearanceNote", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "clearanceNote", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "clearanceOrderNo", value: null, testResult: TestResult.PASS],
                [field: "clearanceOrderNo", value: "clearanceOrderNo", testResult: TestResult.PASS],
                [field: "clearanceOrderNo", value: " ", testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: null, testResult: TestResult.FAIL],
                [field: "currentEmployeeMilitaryRank", value: ps.gov.epsilon.hr.firm.promotion.EmployeePromotion.build(), testResult: TestResult.PASS],
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "currentRequesterEmploymentRecord", value: null, testResult: TestResult.PASS],
                [field: "currentRequesterEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "firm", value: null, testResult: TestResult.FAIL],
                [field: "firm", value: ps.gov.epsilon.hr.firm.Firm.build(), testResult: TestResult.PASS],
                [field: "hasClearance", value: null, testResult: TestResult.FAIL],
                [field: "hasTransferPermission", value: null, testResult: TestResult.FAIL],
                [field: "parentRequestId", value: null, testResult: TestResult.PASS],
                [field: "requestDate", value: null, testResult: TestResult.FAIL],
                [field: "requestDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "requestReason", value: null, testResult: TestResult.PASS],
                [field: "requestReason", value: " ", testResult: TestResult.FAIL],
                [field: "requestReason", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "requestReason", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "requestStatus", value: null, testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: null, testResult: TestResult.PASS],
                [field: "requestStatusNote", value: " ", testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "requestStatusNote", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "requestType", value: null, testResult: TestResult.FAIL],
                [field: "requester", value: null, testResult: TestResult.PASS],
                [field: "requester", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "requesterDepartment", value: null, testResult: TestResult.PASS],
                [field: "requesterDepartment", value: ps.gov.epsilon.hr.firm.Department.build(), testResult: TestResult.PASS],
                [field: "toOrganizationId", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
                [field: "transferPermissionDate", value: null, testResult: TestResult.FAIL],
                [field: "transferPermissionDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "transferPermissionNote", value: null, testResult: TestResult.PASS],
                [field: "transferPermissionNote", value: " ", testResult: TestResult.FAIL],
                [field: "transferPermissionNote", value: getLongString(20001), testResult: TestResult.FAIL],
                [field: "transferPermissionNote", value: getLongString(20000), testResult: TestResult.PASS],
                [field: "transferPermissionOrderNo", value: null, testResult: TestResult.PASS],
                [field: "transferPermissionOrderNo", value: "transferPermissionOrderNo", testResult: TestResult.PASS],
                [field: "transferPermissionOrderNo", value: " ", testResult: TestResult.FAIL],
        ]
        then:
        validateObject(ExternalTransferRequest,constraints)
    }
}
