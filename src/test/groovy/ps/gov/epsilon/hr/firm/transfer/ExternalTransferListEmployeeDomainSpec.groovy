package ps.gov.epsilon.hr.firm.transfer

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import ps.police.test.utils.ConstraintUnitSpec
import ps.police.test.utils.TestResult

@Build([ExternalTransferListEmployee,ps.gov.epsilon.hr.firm.profile.EmploymentRecord,ps.gov.epsilon.hr.firm.profile.Employee,ps.gov.epsilon.hr.firm.transfer.ExternalTransferList,ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequest])
@Domain([ExternalTransferListEmployee])
@TestMixin([HibernateTestMixin])
class ExternalTransferListEmployeeDomainSpec extends ConstraintUnitSpec {

    void "test ExternalTransferListEmployee all constraints"() {
        when:
        List<Map> constraints = [
                [field: "currentEmploymentRecord", value: null, testResult: TestResult.FAIL],
                [field: "currentEmploymentRecord", value: ps.gov.epsilon.hr.firm.profile.EmploymentRecord.build(), testResult: TestResult.PASS],
                [field: "effectiveDate", value: null, testResult: TestResult.FAIL],
                [field: "effectiveDate", value: java.time.ZonedDateTime.now(), testResult: TestResult.PASS],
                [field: "employee", value: null, testResult: TestResult.FAIL],
                [field: "employee", value: ps.gov.epsilon.hr.firm.profile.Employee.build(), testResult: TestResult.PASS],
                [field: "externalTransferList", value: null, testResult: TestResult.FAIL],
                [field: "externalTransferList", value: ps.gov.epsilon.hr.firm.transfer.ExternalTransferList.build(), testResult: TestResult.PASS],
                [field: "externalTransferListEmployeeNotes", value: null, testResult: TestResult.PASS],
                [field: "externalTransferRequest", value: null, testResult: TestResult.PASS],
                [field: "externalTransferRequest", value: ps.gov.epsilon.hr.firm.transfer.ExternalTransferRequest.build(), testResult: TestResult.PASS],
                [field: "recordStatus", value: null, testResult: TestResult.FAIL],
                [field: "toOrganizationId", value: null, testResult: TestResult.FAIL],
                [field: "trackingInfo", value: null, testResult: TestResult.PASS],
        ]
        then:
        validateObject(ExternalTransferListEmployee,constraints)
    }
}
