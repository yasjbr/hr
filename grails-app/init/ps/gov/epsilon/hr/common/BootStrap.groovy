package ps.gov.epsilon.hr.common

import grails.util.Holders


class BootStrap {
    SharedService sharedService
    def init = { servletContext ->
        sharedService.initSetup()
        Boolean initBootStrapData = Holders.grailsApplication.config.grails.initBootStrapData ?: false
        if (initBootStrapData) {
            sharedService.initMenu()
            sharedService.createCustomGroupsAndPermissions()
            sharedService.createBuiltInRoles()
            sharedService.createFirms()
            sharedService.createInspectionCategory()
            sharedService.createStatusCategories()
            sharedService.createEmploymentCategories()
            sharedService.createJobCategories()
            sharedService.createVacationType()
            sharedService.createFirmSetting()
            sharedService.createFirmDocument()
            sharedService.createDisciplinaryReason()
            sharedService.createNotificationType()
            sharedService.createServiceActionReason()
            sharedService.createOperationWorkflowSetting()
            sharedService.createOperationWorkflowSettingParam()
            sharedService.createMilitaryRanks()
            sharedService.createDepartmentTypes()
            sharedService.createDepartments()
            sharedService.createEmployees()
            sharedService.createAllowanceType()
        }
    }
    def destroy = {
    }
}
