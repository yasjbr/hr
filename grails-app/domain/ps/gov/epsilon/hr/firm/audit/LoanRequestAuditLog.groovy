package ps.gov.epsilon.hr.firm.audit

import ps.police.audit.AbstractAuditLog

/**
 * PersonAuditLog are reported to the AuditLog table.
 * This requires you to set up a table or allow
 * Grails to create a table for you. (e.g. DDL or db-migration plugin)
 */
class LoanRequestAuditLog extends AbstractAuditLog {

    static mapping = {
        table 'loan_request_audit_log'
        cache usage: 'read-only', include: 'non-lazy'
        version false
    }


    String toString() {
        return super.toString()
    }
}