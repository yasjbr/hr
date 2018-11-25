package ps.gov.epsilon.hr.firm.audit

import ps.police.audit.AbstractAuditLog

/**
 * AuditLog are reported to the AuditLog table.
 * This requires you to set up a table or allow
 * Grails to create a table for you. (e.g. DDL or db-migration plugin)
 */
class AuditLog extends AbstractAuditLog {

    String className

    static mapping = {
        table 'audit_log'
        cache usage: 'read-only', include: 'non-lazy'
        version false
    }

    String toString() {
        return super.toString()
    }
}
